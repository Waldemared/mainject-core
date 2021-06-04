package com.wald.mainject.config.property.source

import com.wald.mainject.config.property.PropertySource


/**
 * @author vkosolapov
 * @since
 */
class MixedPropertySource(val sources: Collection<PropertySource>) : PropertySource {
    constructor(vararg sources: PropertySource) : this(sources.toList())

    override val properties: Map<String, Any?>
        get() = sources.asSequence()
            .map { it.properties }
            .fold(HashMap()) { accumulator, map ->
                accumulator.putAll(map)
                accumulator
            }

    override fun get(propertyKey: String): Any? {
        return sources.asSequence()
            .filter { propertyKey in it }
            .map { it[propertyKey] }
            .firstOrNull()
    }

    override fun contains(propertyKey: String): Boolean = sources.any { propertyKey in it }
}