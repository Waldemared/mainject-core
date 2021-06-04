package com.wald.mainject.inject.extension.impl

import com.wald.mainject.inject.extension.LifecycleCallbacksResolver
import com.wald.mainject.inject.qualify.AnnotationTypes
import com.wald.mainject.inject.qualify.hasAnnotationThat
import kotlin.reflect.KFunction


/**
 * @author vkosolapov
 * @since
 */
class AnnotationBasedLifecycleCallbackResolver(private val initMethodMarkers: AnnotationTypes,
                                               private val destroyMethodMarkers: AnnotationTypes) :
    LifecycleCallbacksResolver {
    override fun isInitMethod(method: KFunction<Unit>): Boolean {
        return method.hasAnnotationThat { it.annotationClass in initMethodMarkers }
    }

    override fun isDestroyMethod(method: KFunction<Unit>): Boolean {
        return method.hasAnnotationThat { it.annotationClass in destroyMethodMarkers }
    }
}