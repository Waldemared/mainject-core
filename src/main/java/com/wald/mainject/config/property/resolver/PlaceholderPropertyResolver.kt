package com.wald.mainject.config.property.resolver

import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.config.property.source.EmptyProperties

/**
 * @author vkosolapov
 * @since
 */
open class PlaceholderPropertyResolver @JvmOverloads constructor(
    private val placeholderPrefix: String,
    private val placeholderSuffix: String,
    override var propertySource: PropertySource = EmptyProperties
) : CachingPropertyResolver(), SourceAwarePropertyResolver {
    override fun applies(propertyValue: String) = findPlaceholderBounds(propertyValue) != null

    override fun doResolve(propertyValue: String): Any? {
        val placeholder = findPlaceholder(propertyValue) ?: return propertyValue

        val unboundedPropertyIdentifier = unbound(placeholder)
        val actualValue = propertySource[unboundedPropertyIdentifier]

        val resolved = propertyValue.replace(placeholder, actualValue.toString())

        return if (applies(resolved)) {
            super.resolve(resolved)
        } else {
            resolved
        }
    }

    private fun findPlaceholder(text: String): String? {
        val bounds = findPlaceholderBounds(text) ?: return null
        return text.substring(bounds.first, bounds.last)
    }

    private fun findPlaceholderBounds(text: String): IntRange? {
        val prefixStartIndex = text.indexOf(placeholderPrefix)
        if (prefixStartIndex < 0) {
            return null
        }

        val suffixStartIndex = text.indexOf(placeholderSuffix)
        if (suffixStartIndex < 0) {
            return null
        }
        val suffixEndIndex = suffixStartIndex + placeholderSuffix.length

        return prefixStartIndex..suffixEndIndex
    }

    private fun unbound(boundedPropertyIdentifier: String): String {
        val prefixEndPosition = placeholderPrefix.length
        val suffixStartPosition = boundedPropertyIdentifier.length - placeholderSuffix.length
        return boundedPropertyIdentifier.substring(prefixEndPosition, suffixStartPosition)
    }
}