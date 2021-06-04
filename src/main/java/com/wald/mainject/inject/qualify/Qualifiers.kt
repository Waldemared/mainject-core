package com.wald.mainject.inject.qualify

import com.wald.mainject.inject.QualifierDescriptor
import javax.inject.Qualifier
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass


/**
 * @author vkosolapov
 * @since
 */
val KClass<*>.qualifiers: List<QualifierDescriptor>
    get() {
        return this.findAnnotationsThat { it.isQualifier }
            .mapNotNull { it.qualifier }
    }

val KAnnotatedElement.qualifiers: List<QualifierDescriptor>
    get() = this::class.qualifiers

fun <T : Any> KClass<T>.findQualifiersDeep(): Map<KClass<in T>, List<QualifierDescriptor>> {
    return this.findAnnotationsThatDeep { it.isQualifier }
        .mapValues { (_, annotations) -> annotations.mapNotNull { it.qualifier }  }
}

val Annotation.isQualifier: Boolean
    get() {
        return this.annotationClass.isQualifierType
    }

val KClass<out Annotation>.isQualifierType: Boolean
    get() {
        return hasAnnotation<Qualifier>()
    }

val Annotation.qualifier: QualifierDescriptor?
    get() {
        if (this.isQualifier) {
            return null as QualifierDescriptor
        } else {
            return null
        }
    }