package com.wald.mainject.inject.extension.impl

import com.wald.mainject.inject.extension.SetterSelector
import com.wald.mainject.inject.qualify.hasAnnotationThat
import kotlin.reflect.KClass
import kotlin.reflect.KFunction


/**
 * @author vkosolapov
 * @since
 */
class AnnotationBasedSetterSelector(private val injectableMarkers: Collection<KClass<out Annotation>>)
    : SetterSelector {
    constructor(vararg injectableMarkers: KClass<out Annotation>) : this(injectableMarkers.toList())

    override fun isInjectable(setter: KFunction<*>): Boolean {
        return setter.hasAnnotationThat { it.annotationClass in injectableMarkers }
    }
}