package com.wald.mainject.config.property.resolver

import com.wald.mainject.config.property.PropertyResolver
import com.wald.mainject.config.property.PropertySource


/**
 * @author vkosolapov
 * @since
 */
interface SourceAwarePropertyResolver : PropertyResolver {
    var propertySource: PropertySource
}