package com.wald.mainject.inject

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

//import kotlin.reflect.full.createInstance


/**
 * @author vkosolapov
 * @since
 */
sealed class QualifierDescriptor(val instance: Annotation, val clazz: KClass<out Annotation>) {
    init {

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QualifierDescriptor) return false

        if (instance != other.instance) return false
        if (clazz != other.clazz) return false

        return true
    }

    override fun hashCode(): Int {
        var result = instance.hashCode()
        result = 31 * result + clazz.hashCode()
        return result
    }
}

class InstanceBasedQualifierDescriptor(instance: Annotation) : QualifierDescriptor(
    instance = instance,
    clazz = instance.annotationClass
)

class TypeBasedQualifierDescriptor(clazz: KClass<out Annotation>) : QualifierDescriptor(
    instance = clazz.createInstance(),
    clazz = clazz
)