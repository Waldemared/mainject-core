package com.wald.mainject.inject.extension.impl

import com.wald.mainject.inject.qualify.hasAnnotationThat
import kotlin.reflect.KClass
import kotlin.reflect.KFunction


/**
 * @author vkosolapov
 * @since
 */
class AnnotationBasedConstructorResolver(private val injectableMarkers: Collection<KClass<out Annotation>>)
    : AbstractPrioritizedConstructorResolver() {
    constructor(vararg injectableMarkers: KClass<out Annotation>) : this (injectableMarkers.toList())

    override fun <T : Any> isPrioritized(ownerClass: KClass<T>, constructor: KFunction<T>): Boolean {
        return constructor.hasAnnotationThat { it.annotationClass in injectableMarkers }
    }
}