package com.wald.mainject.config.dsl

import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.config.property.source.ClassPathPropertySource
import com.wald.mainject.config.property.source.PlainProperties
import com.wald.mainject.config.property.source.PropertiesFile
import java.io.File


/**
 * @author vkosolapov
 * @since
 */
class PropertiesDsl {
    private val propertySources: MutableSet<PropertySource> = mutableSetOf()

    fun properties(propertySource: PropertySource, transformer: (PropertySource) -> Unit = {}) {
        propertySources += propertySource.also(transformer)
    }

    fun properties(vararg properties: Pair<String, Any>, transformer: (PropertySource) -> Unit = {}) {
        properties(PlainProperties(*properties), transformer)
    }

    fun fromFile(filePath: String, transformer: PropertySource.() -> Unit = {}) {
        properties(PropertiesFile(File(filePath)), transformer)
    }

    fun fromClassPath(path: String, transformer: PropertySource.() -> Unit) {
        properties(ClassPathPropertySource(path), transformer)
    }

    fun build(): Set<PropertySource> {
        return propertySources
    }
}