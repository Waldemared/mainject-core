package com.wald.mainject.inject

import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.definition.ComponentKey
import com.wald.mainject.inject.definition.key
import com.wald.mainject.inject.definition.satisfies
import com.wald.mainject.inject.extension.*
import com.wald.mainject.module.Module


/**
 * @author vkosolapov
 * @since
 */


class ContainerBuilder {
    private var modules: MutableSet<Module> = mutableSetOf()

    private val extensions: MutableExtensions = MutableExtensions()

    fun module(module: Module) = apply { modules += module }

    fun module(module: Module, transformer: Module.() -> Unit) = module(module.apply(transformer))

    fun constructorResolver(resolver: ConstructorResolver) = apply { extensions.constructorResolver = resolver }

    fun propertySelector(selector: LateinitPropertySelector) = apply { extensions.propertySelector = selector }

    fun setterSelector(selector: SetterSelector) = apply { extensions.setterSelector = selector }

    fun dependencyResolver(resolver: DependencyDescriptorResolver) = apply { extensions.dependencyResolver = resolver }

    fun lifecycleCallbacksResolver(resolver: LifecycleCallbacksResolver) = apply { extensions.lifecycleCallbacksResolver = resolver }

    fun build(): Container {
        val bindings = ContainerBindings(
            modules = modules,
            extensions = extensions
        )
        val creator = ContainerCreator(bindings)
        return creator.initializeContainer()
    }
}



fun Module.override(overriddenKey: ComponentKey, newComponent: BeanDefinition<*>) {
    registeredBeans.filter { !it.key.satisfies(overriddenKey) } + newComponent
}