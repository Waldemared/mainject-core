package com.wald.mainject.inject.aop.pointcut

import com.wald.mainject.inject.Matcher
import com.wald.mainject.inject.and
import com.wald.mainject.inject.annotatedAs
import com.wald.mainject.inject.aop.Pointcut
import com.wald.mainject.inject.extends
import groovy.lang.Lazy
import kotlin.reflect.KClass
import kotlin.reflect.KFunction


/**
 * @author vkosolapov
 * @since
 */
open class PlainPointcut(val predicate: (KClass<*>, KFunction<*>) -> Boolean) : Pointcut {
    constructor(classPredicate: (KClass<*>) -> Boolean, methodPredicate: (KFunction<*>) -> Boolean) : this ({
            clazz, method -> classPredicate(clazz) && methodPredicate(method)
    })

    constructor(classMatcher: Matcher<*>, methodMatcher: Matcher<*>) : this ({
            clazz, method -> classMatcher.matches(clazz) && methodMatcher.matches(method)
    })

    override fun check(clazz: KClass<*>, method: KFunction<*>): Boolean {
        return predicate(clazz, method)
    }

    init {
        PlainPointcut(
            classMatcher = extends(IntRange::class) and annotatedAs(Lazy::class),
            methodMatcher = annotatedAs<DslMarker>()
        )
    }
}