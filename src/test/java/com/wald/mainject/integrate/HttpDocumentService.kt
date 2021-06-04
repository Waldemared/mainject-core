package com.wald.mainject.integrate

import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime
import java.util.stream.Collectors
import javax.inject.Named
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonValue
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.UriBuilder


/**
 * @author vkosolapov
 * @since
 */
@Named("docs.httpDocumentService")
class HttpDocumentService(val httpClient: HttpClient, @ExtApiUrl val baseUrl: String) : DocumentService {
    override fun findInRange(range: ClosedRange<LocalDateTime>): List<Document> {
        val uri = UriBuilder.fromUri("$baseUrl/documents")
            .queryParam("dateFrom", range.start)
            .queryParam("dateTo", range.endInclusive)
            .build()
        val request = HttpRequest.newBuilder(uri)
            .GET()
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream())
        if (response.statusCode() != 200) error("Failed to fetch documents")
        return retrieveDocuments(response.body())
    }

    override fun register(document: Document) {
        val uri = UriBuilder.fromUri("$baseUrl/documents").build()
        val request = HttpRequest.newBuilder(uri)
            .POST(HttpRequest.BodyPublishers.ofInputStream{ document.asJsonStream() })
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.discarding())
        if (response.statusCode() != 200) error("Failed to register document")
    }

    private fun retrieveDocuments(jsonByteStream: InputStream): List<Document> {
        return Json.createParser(jsonByteStream).arrayStream
            .map(JsonValue::asJsonObject)
            .map(::documentFromJson)
            .collect(Collectors.toList())
    }

    private fun documentFromJson(json: JsonObject): Document {
        val id = json.getJsonNumber("id").longValue()
        val systemDate = LocalDateTime.parse(json.getJsonString("systemDate").string)
        return Document(id, systemDate)
    }

    private fun Document.asJsonStream(): InputStream {
        val json = Json.createObjectBuilder()
            .add("id", id)
            .add("systemDate", systemDate.toString())
            .build()

        val jsonResult = PipedInputStream()
        PipedOutputStream(jsonResult).apply {
            Json.createWriter(this).writeObject(json)
            flush()
            close()
        }

        return jsonResult
    }
}