package com.wald.mainject

import com.wald.mainject.inject.Component
import com.wald.mainject.inject.InjectionPoint
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
class ApplicationComponent<T : Any>(
    override val definition: BeanDefinition<T>,
    override val primary: Boolean,
    override val scope: Scope
) : Component<T> {
    override var instance: T? = null
    override var provider: Provider<T>? = null
    override var constructor: InjectionPoint<KFunction<T>>? = null
    override var members: MutableSet<InjectionPoint<out KCallable<*>>> = mutableSetOf()
    override val preInitializers: MutableSet<PreInitializer<in T>> = mutableSetOf()
    override val initializers: MutableSet<KFunction<*>> = mutableSetOf()
    override val destructors: MutableSet<KFunction<*>> = mutableSetOf()
    override val interceptors: MutableSet<InterceptedMethods> = mutableSetOf()
}