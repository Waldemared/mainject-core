package com.wald.mainject.config.property.source

import java.io.File
import java.util.*

/**
 * @author vkosolapov
 * @since
 */
class PropertiesFile(val file: File) : PersistentPropertySource() {
    override fun doGetAll(): Map<String, Any> {
        val props = Properties().apply { load(file.reader()) }
        return props.mapKeys { (propertyName, _) -> propertyName.toString() }
    }
}