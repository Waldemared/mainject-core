package com.wald.mainject.inject.extension.impl

import com.wald.mainject.inject.extension.LateinitPropertySelector
import com.wald.mainject.inject.qualify.hasAnnotationThat
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty


/**
 * @author vkosolapov
 * @since
 */
class AnnotationBasedLateinitPropertySelector(private val injectableMarkers: Collection<KClass<out Annotation>>)
    : LateinitPropertySelector {
    constructor(vararg injectableMarkers: KClass<out Annotation>) : this(injectableMarkers.toList())

    override fun isInjectable(property: KMutableProperty<*>): Boolean {
        return property.hasAnnotationThat { it.annotationClass in injectableMarkers }
    }
}