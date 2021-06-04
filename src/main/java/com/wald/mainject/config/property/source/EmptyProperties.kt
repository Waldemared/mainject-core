package com.wald.mainject.config.property.source

import com.wald.mainject.config.property.PropertySource


/**
 * @author vkosolapov
 * @since
 */
object EmptyProperties : PropertySource {
    override val properties: Map<String, Any> = emptyMap()

    override fun get(propertyKey: String): Any? = null

    override fun contains(propertyKey: String): Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is PropertySource && other.properties.isEmpty()
    }
}