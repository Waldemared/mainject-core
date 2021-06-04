package com.wald.mainject.config.property.resolver

import com.wald.mainject.config.property.PropertyResolver


/**
 * @author vkosolapov
 * @since
 */
class PlainPropertyResolver(val bindings: Map<String, Any?>) : PropertyResolver {
    override fun applies(propertyValue: String): Boolean = propertyValue in bindings

    override fun resolve(propertyValue: String): Any? = bindings[propertyValue]
}