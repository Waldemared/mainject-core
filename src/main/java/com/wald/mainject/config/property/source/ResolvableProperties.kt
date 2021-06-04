package com.wald.mainject.config.property.source

import com.wald.mainject.config.property.PropertyResolver
import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.config.property.resolver.SourceAwarePropertyResolver

/**
 * Wrapper over a [PropertySource] whose properties can be resolved at runtime.
 * This wrapper should be used when the underlying [PropertySource] may contain encoded
 * values, placeholders, etc and those values must be resolved when they are requested.
 *
 * The property values are to be resolved with the first [applicable][PropertyResolver.applies] resolver
 * that resolves a non-null value against the property value. Resolvers are checked against property value
 * in the same order they were supplied to this wrapper.
 * The property value shall not be changed if no resolvers can resolve it.
 *
 * Resolving of embedded property values is also supported for iterables and maps.
 * Those values are to be resolved recursively.
 *
 * @author vkosolapov
 * @since
 */
class ResolvableProperties<T : PropertySource>(private val resolvablePropertiesHolder: T) : PropertySource {
    private val resolvers = mutableListOf<PropertyResolver>()

    override val properties: Map<String, Any?>
        get() = resolvablePropertiesHolder.properties.mapValues { (propertyName, _) -> this.get(propertyName) }

    override fun get(propertyKey: String): Any? {
        val propertyValue = resolvablePropertiesHolder[propertyKey]
        return propertyValue?.resolve()
    }

    override fun contains(propertyKey: String): Boolean = propertyKey in resolvablePropertiesHolder
    
    public fun addResolver(resolver: PropertyResolver) {
        resolvers += resolver
    }
    
    private fun Any.resolve(): Any {
        if (this is Iterable<*>) {
            return this.map { it?.resolve() }
        }

        if (this is Map<*, *>) {
            return this.mapValues { it.value?.resolve() }
        }

        if (this is String) {
            val result = resolvers.asSequence()
                .filter { it.applies(this) }
                .mapNotNull { it.resolve(this) }
                .firstOrNull()

            // Return the original value if no resolver can apply this string
            return result ?: this
        }

        return this
    }

    class ResolvablePropertiesBuilder<T : PropertySource>(private val propertiesToResolve: T) {
        private val resolvableInstance: ResolvableProperties<T> = ResolvableProperties(propertiesToResolve)

        fun resolveWith(resolver: PropertyResolver) = apply {
            resolvableInstance.resolvers += resolver
        }

        fun resolveWith(resolver: SourceAwarePropertyResolver) = apply {
            if (resolver.propertySource == EmptyProperties) {
                resolver.propertySource = propertiesToResolve
            }
            resolvableInstance.resolvers += resolver
        }

        fun build() = resolvableInstance
    }
}