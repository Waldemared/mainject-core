package com.wald.mainject.inject

import com.wald.mainject.inject.qualify.hasAnnotation
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


interface Matcher<T : Any> {
    fun matches(value: T): Boolean
}

fun <T : KAnnotatedElement> annotatedAs(annotationType: KClass<out Annotation>): Matcher<T> {
    return AnnotationMatcher(annotationType)
}

inline fun <reified A : Annotation> anotatedAs(): Matcher<*> {
    return annotatedAs(A::class)
}

fun extends(superclass: KClass<*>) : Matcher<KClass<*>> {
    return HasSuperclassMatcher<*>(superclass)
}

inline fun <reified T : Any> extends(): Matcher<KClass<*>> {
    return extends(T::class)
}

infix fun Matcher<*>.and(other: Matcher<*>): Matcher<*> {
    return ComposedMatcher(this, other)
}

infix fun Matcher<KClass<*>>.and(other: Matcher<*>): Matcher<*> {
    return ComposedMatcher(this, other)
}

class ComposedMatcher<T : Any>(val left: Matcher<T>, val right: Matcher<T>) : Matcher<T> {
    override fun matches(value: T): Boolean {
        return left.matches(value) && right.matches(value)
    }
}

class AnnotationMatcher<T : KAnnotatedElement>(val annotationType: KClass<out Annotation>) : Matcher<T> {
    override fun matches(value: T): Boolean {
        return value.hasAnnotation(annotationType)
    }
}

class HasSuperclassMatcher<T : Any>(val superclass: KClass<in T>) : Matcher<T> {
    override fun matches(value: T) = value::class.isSubclassOf(superclass)
}

object TrueMatcher : Matcher<Any> {
    override fun matches(value: Any) = true
}