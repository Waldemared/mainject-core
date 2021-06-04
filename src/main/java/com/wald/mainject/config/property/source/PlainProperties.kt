package com.wald.mainject.config.property.source

import com.wald.mainject.config.property.PropertySource


/**
 * The [PropertySource] implementation that allows to define properties programmatically.
 *
 * @author vkosolapov
 * @since
 */
class PlainProperties(private val providedProperties: Map<String, Any>) : PersistentPropertySource() {
    /**
     * Constructs the object using [properties] pairs in a convenient style.
     *
     * Usage example:
     * ```
     * .kt: val properties = FlatProperties("key1" to "value", "key3" to listOf("a", "b", "c"))
     * .java: var properties = new FlatProperties(to("key1", "value"), to("key3", List.of("a", "b", "c")))
     * ```
     */
    constructor(vararg properties: Pair<String, Any>) : this(properties.toMap())

    override fun doGetAll(): Map<String, Any> = providedProperties
}