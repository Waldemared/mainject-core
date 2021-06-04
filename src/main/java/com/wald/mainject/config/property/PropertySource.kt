package com.wald.mainject.config.property

import com.wald.mainject.config.property.source.CachingPropertySource
import com.wald.mainject.config.property.source.ResolvableProperties
import com.wald.mainject.config.property.source.ResolvableProperties.ResolvablePropertiesBuilder

/**
 * Supplier of properties to be used by application at runtime.
 * Each property represents a `<key>`-`<value>` pair where `<key>` is a string-typed
 * property qualifier and `<value>` is either encoded or actual property value.
 *
 * @author vkosolapov
 * @since
 */
interface PropertySource {
    /**
     * Eager supplier of all the properties available for this source.
     */
    val properties: Map<String, Any?>

    /**
     * Retrieves optional property by the provided [propertyKey].
     *
     * This method provides EDLS-style object access in kotlin:
     * ```
     * .kt: val vmVersion = SystemProperties["java.vm.version"]
     * .java: Object vmVersion = SystemProperties.get("java.vm.version")
     * ```
     */
    operator fun get(propertyKey: String): Any?

    /**
     * Check whether this object can retrieve a property value by the provided [propertyKey]
     *
     * Provides EDSL-style object access in kotlin:
     * .kt: val contains: Boolean = "some.key" in SystemProperties
     * .java: boolean contains: Boolean = SystemProperties.contains("some.key")
     */
    operator fun contains(propertyKey: String): Boolean

    fun getString(propertyKey: String) = this[propertyKey].toString()

    @JvmDefault
    fun resolveWith(resolver: PropertyResolver) = ResolvableProperties(this).apply { addResolver(resolver) }

    @JvmDefault
    fun caching() = CachingPropertySource(this)
}