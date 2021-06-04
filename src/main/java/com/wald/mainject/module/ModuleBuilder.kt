package com.wald.mainject.module

import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.inject.definition.AspectDefinition
import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.definition.ImplClassBeanDefinition
import com.wald.mainject.inject.definition.InstanceBeanDefinition
import com.wald.mainject.inject.lifecylce.PreInitializer
import groovy.lang.Closure
import javax.inject.Provider
import kotlin.reflect.KClass


/**
 * @author vkosolapov
 * @since
 */
class ModuleBuilder(val name: String) {
    private val registeredBeans: MutableSet<BeanDefinition<*>> = mutableSetOf()
    private val imports: MutableSet<ModuleRef> = mutableSetOf()
    private val importedSources: MutableSet<ModulesSource> = mutableSetOf()
    private val propertySources: MutableSet<PropertySource> = mutableSetOf()
    private val interceptors: MutableSet<AspectDefinition> = mutableSetOf()
    private val preInitializers: MutableSet<PreInitializer<*>> = mutableSetOf()

    fun component(definition: BeanDefinition<*>) = apply { registeredBeans += definition }

    fun <T : Any> component(instance: T) = component(InstanceBeanDefinition(instance))

    fun <T : Any> component(componentClass: KClass<T>) = component(ImplClassBeanDefinition(componentClass))

    fun <T : Any> provider(providerClass: KClass<Provider<T>>) = component(providerClass)

    fun import(moduleInstance: Module, override: (OverridesBuilder.() -> Unit)?) = import(moduleInstance.ref, override)

    fun import(moduleType: KClass<out Module>, override: (OverridesBuilder.() -> Unit)?) = import(moduleType.ref, override)

    fun moduleSource(source: ModulesSource) = apply { importedSources += source }

    fun properties(properties: PropertySource) = apply { propertySources += properties }

    fun interceptor(aspect: AspectDefinition) = apply { interceptors += aspect }

    fun preInitializers(preInitializer: PreInitializer<*>) = apply { preInitializers += preInitializer }

    internal fun import(module: ModuleRef, override: (OverridesBuilder.() -> Unit)? = null) = apply {
        val overriddenModule = override?.let { module.override(it) } ?: module
        imports += overriddenModule
    }

    fun build() = BaseModule(
        name,
        registeredBeans,
        imports,
        importedSources,
        propertySources,
        interceptors,
        preInitializers
    )
}

fun module(name: String): ModuleBuilder = ModuleBuilder(name)

fun module(name: String, construct: Closure<*>): Module {
    val builder = ModuleBuilder(name)
    construct.apply {
        delegate = builder
        call()
    }
    return builder.build()
}