package com.wald.mainject.inject

import com.wald.mainject.Scope
import com.wald.mainject.inject.aop.pointcut.InterceptedMethods
import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.lifecylce.PreInitializer
import javax.inject.Provider
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction


/**
 * @author vkosolapov
 * @since
 */
interface Component<T : Any> {
    val definition: BeanDefinition<T>

    var instance: T?

    var provider: Provider<T>?

    var constructor: InjectionPoint<KFunction<T>>?

    val members: MutableSet<InjectionPoint<out KCallable<*>>>

    val preInitializers: MutableSet<PreInitializer<in T>>

    val initializers: MutableSet<KFunction<*>>

    val destructors: MutableSet<KFunction<*>>

    val interceptors: MutableSet<InterceptedMethods>

    val scope: Scope

    /**
     * Whether the component is marked as primary, that is,
     * eligible for injection when several injection candidates found
     */
    val primary: Boolean
}