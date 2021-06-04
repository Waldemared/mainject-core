package com.wald.mainject.integrate

import java.time.LocalDateTime
import javax.inject.Named


/**
 * @author vkosolapov
 * @since
 */
@Named("docs.persistentDocumentService")
class PersistentDocumentService(val repository: DocumentRepository) : DocumentService {
    override fun findInRange(range: ClosedRange<LocalDateTime>): List<Document> {
        return repository.find(range.start, range.endInclusive)
    }

    override fun register(document: Document) {
        repository.persist(document)
    }
}