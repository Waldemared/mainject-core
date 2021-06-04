package com.wald.mainject.config.property.source

import com.wald.mainject.config.property.PropertySource

/**
 * Extension class for [PropertySource]es that supply all their properties only once.
 *
 * @author vkosolapov
 * @since
 */
abstract class PersistentPropertySource() : PropertySource {
    /**
     * Properties holder. To be initialized on the first property access.
     */
    override val properties: Map<String, Any> by lazy { doGetAll() }

    override fun get(propertyKey: String): Any? = properties[propertyKey]

    override fun contains(propertyKey: String): Boolean = propertyKey in properties

    /**
     * Actual property supplier.
     */
    protected abstract fun doGetAll(): Map<String, Any>
}