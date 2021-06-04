package com.wald.mainject.config.property.source

import com.wald.mainject.config.property.PropertySource


/**
 * @author vkosolapov
 * @since
 */
class CachingPropertySource<T : PropertySource>(val delegate: T) : PropertySource {
    protected val cache: MutableMap<String, Any?> = HashMap()

    override val properties: Map<String, Any?> by lazy {
        val allProperties = delegate.properties
        cache.putAll(properties)
        allProperties
    }

    override fun get(propertyKey: String): Any? {
        if (propertyKey in cache) {
            return cache[propertyKey]
        }

        return delegate[propertyKey]
    }

    override fun contains(propertyKey: String): Boolean = propertyKey in cache || propertyKey in delegate
}