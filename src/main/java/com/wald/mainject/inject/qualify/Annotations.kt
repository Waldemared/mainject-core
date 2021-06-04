package com.wald.mainject.inject.qualify

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaMethod


typealias AnnotationType = KClass<out Annotation>

typealias AnnotationTypes = Collection<AnnotationType>

inline fun <reified T : Annotation> KClass<*>.hasAnnotationDeep(): Boolean {
    return this.hasAnnotation<T>() || this.superclasses.any { it.hasAnnotation<T>() }
}

inline fun <reified T : Annotation> KClass<*>.findAnnotationDeep(): List<T> {
    val classes = this.withSuperclasses()
    return classes.mapNotNull { it.findAnnotation<T>() }
}

inline fun <T : Any> KClass<T>.findAnnotationsThatDeep(predicate: (Annotation) -> Boolean): Map<KClass<in T>, List<Annotation>> {
    val classes = this.withSuperclasses()
    val result = classes.associateWith { it.findAnnotationsThat(predicate) }
    return result
}

inline fun <reified T : Annotation> KAnnotatedElement.hasAnnotation(): Boolean = hasAnnotation(T::class)
fun <T : Annotation> KAnnotatedElement.hasAnnotation(clazz: KClass<T>): Boolean {
    return this.annotations.any { it.annotationClass == clazz }
}

inline fun <reified T : Annotation> KAnnotatedElement.findAnnotation(): T? = findAnnotation(T::class)

fun <T : Annotation> KAnnotatedElement.findAnnotation(clazz: KClass<T>): T? {
    return this.annotations.firstOrNull { it.annotationClass == clazz } as? T
}

inline fun KAnnotatedElement.findAnnotationsThat(predicate: (Annotation) -> Boolean): List<Annotation> {
    return this.annotations.filter(predicate)
}

inline fun <reified T : Annotation> KAnnotatedElement.hasAnnotationDeep(): Boolean {
    when (this) {
        is KClass<*> -> return this.hasAnnotationDeep<T>()
        is KFunction<*> -> {
            if (this.hasAnnotation<T>()) return true
            val superClazz = this.javaMethod?.declaringClass?.kotlin?.superclasses ?: return false
            for (clazz in superClazz) {
                val overridenMethod =
                    clazz.declaredFunctions
                        .firstOrNull { it.name == this.name && it.typeParameters == this.typeParameters }
                        ?: continue
                if (overridenMethod.hasAnnotation<T>()) return true
            }
            return false
        }
        else -> return false
    }
}

inline fun <reified T : Annotation> KFunction<*>.findFirstAnnotatedDeep(): KFunction<*>? = findFirstAnnotatedDeep(T::class)

fun <T : Annotation> KFunction<*>.findFirstAnnotatedDeep(annotationType: KClass<T>): KFunction<*>? {
    val thisHas = this.hasAnnotation(annotationType)
    if (thisHas) {
        if (this.ownerClass().java.isInterface) {
            return this
        } else {
            return this.allOverrides().firstOrNull { it.ownerClass().java.isInterface }
        }
    }

    val overridden = this.allOverrides().asSequence()
        .mapNotNull { it.findFirstAnnotatedDeep(annotationType) }
        .firstOrNull()

    return overridden
}

fun KFunction<*>.ownerClass() = this.javaMethod!!.declaringClass.kotlin

fun KFunction<*>.allOverrides(): List<KFunction<*>> {
    val superClazz = this.ownerClass().superclasses
    return superClazz.mapNotNull { it.findFunctionBySignature(this) }
}

fun KFunction<*>.firstOverride(): KFunction<*>? {
    val superClazz = this.javaMethod?.declaringClass?.kotlin?.superclasses ?: return null
    return superClazz.asSequence()
        .mapNotNull { it.findFunctionBySignature(this) }
        .firstOrNull()
}

fun KClass<*>.findFunctionBySignature(function: KFunction<*>): KFunction<*>? {
    for (func in this.declaredFunctions) {
        if (function.signaturesEqual(func)) {
            return func
        }
    }
    return null
}

fun KFunction<*>.signaturesEqual(otherFunction: KFunction<*>): Boolean {
    return this.name == otherFunction.name && this.typeParameters == otherFunction.typeParameters
}

fun KAnnotatedElement.hasAnnotationThat(predicate: (Annotation) -> Boolean): Boolean {
    return this.annotations.any(predicate)
}

fun <T : Any> KClass<T>.withSuperclasses(): List<KClass<in T>> {
    val classes = mutableListOf<KClass<in T>>(this)
    classes += this.superclasses as List<KClass<in T>>
    return classes
}

