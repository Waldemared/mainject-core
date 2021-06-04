package com.wald.mainject.integrate

import java.net.http.HttpClient
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Named
import javax.inject.Provider
import javax.persistence.EntityManager
import javax.sql.DataSource


/**
 * @author vkosolapov
 * @since
 */
interface DocumentService {
    fun register(document: Document)


    fun findInRange(range: ClosedRange<LocalDateTime>): List<Document>

}



class PlainHttpClientProvider(val builder: HttpClient.Builder) : Provider<HttpClient> {
    override fun get(): HttpClient {
        return builder
            //.authenticator()
            .executor {  }
            .build()
    }
}

annotation class ExtApiUrl

annotation class HttpClientArchetype
annotation class ProxyingHttpClientArchetype

data class Document(val id: Long, val systemDate: LocalDateTime)

interface DocumentRepository {
    fun find(startDate: LocalDateTime, endDate: LocalDateTime): List<Document>

    fun persist(document: Document)
}

@Named("docs.jdbcDocumentRepository")
class JDBCDocumentRepository(val dataSource: DataSource) : DocumentRepository {
    override fun find(startDate: LocalDateTime, endDate: LocalDateTime): List<Document> {
        val connection = getConnection()
        val statement = connection.prepareStatement(SELECT_BY_DATE_STATEMENT)
        statement.setParameters(startDate, endDate)
        statement.execute()
        val documents = mutableListOf<Document>()
        val resultSet = statement.resultSet
        with(resultSet) {
            while (next()) {
                documents += Document(getLong(0), LocalDateTime.from(getTimestamp(1).toLocalDateTime()))
            }
        }

        releaseConnection(connection)
        return documents
    }

    override fun persist(document: Document) {
        val connection = getConnection()
        val statement = connection.prepareStatement(INSERT_STATEMENT)
        statement.setParameters(document.id, document.systemDate)
        statement.execute()
        releaseConnection(connection)
    }

    private fun PreparedStatement.setParameters(vararg parameters: Any) {
        parameters.forEachIndexed { index, parameter ->
            when (parameter) {
                is Long -> this.setLong(index, parameter)
                is LocalDateTime -> this.setTimestamp(index, Timestamp.from(parameter.toInstant(ZoneOffset.UTC)))
            }
        }
    }

    private fun getConnection() = dataSource.connection

    private fun releaseConnection(connection: Connection) = kotlin.runCatching { connection.close() }.getOrElse {  }

    companion object {
        const val SELECT_BY_DATE_STATEMENT = "select id, system_date from document where system_date between ? and ?";
        const val INSERT_STATEMENT = "insert into document value ?, ?"
    }
}

@Named("docs.jpaDocumentRepository")
class JPADocumentRepository(val entityManager: EntityManager) : DocumentRepository {
    override fun find(startDate: LocalDateTime, endDate: LocalDateTime): List<Document> {
        val builder = entityManager.criteriaBuilder
            val query = builder.createQuery(Document::class.java).apply {
                val root = from(Document::class.java)
                select(root)
                    .where(
                        builder.between(root["systemDate"], startDate, endDate)
                    )
            }

            return entityManager.createQuery(query).resultList
    }

    override fun persist(document: Document) {
        entityManager.persist(document)
    }
}


