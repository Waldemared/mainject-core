package com.wald.mainject.inject

import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.config.property.source.EmptyProperties
import com.wald.mainject.config.property.source.MixedPropertySource
import com.wald.mainject.inject.definition.AspectDefinition
import com.wald.mainject.inject.lifecylce.PreInitializer
import com.wald.mainject.module.Module


/**
 * @author vkosolapov
 * @since
 */
class ModulesComposer(val registry: ModuleRegistry) {

    fun compose(): MainModule {
        return MainModule(
            modules = registry,
            componentDefinitions = composeDefinitions(),
            properties = composeProperties(),
            interceptors = composeInterceptors(),
            preInitializers = composePreInitializers()
        )
    }

    private fun composeDefinitions(): BeanDefinitionRegistry {
        val definitionRegistry = DefinitionRegistry()
        registry.modules.asSequence()
            .map(Module::registeredBeans)
            .flatten()
            .forEach(definitionRegistry::add)

        return definitionRegistry
    }

    private fun composeProperties(): PropertySource {
        val properties = registry.modules.asSequence()
            .map(Module::propertySources)
            .flatten()
            .distinct()
            .toSet()

        return MixedPropertySource(properties)
    }

    private fun composeInterceptors(): Set<AspectDefinition> {
        val interceptors = registry.modules.asSequence()
            .map(Module::interceptors)
            .flatten()
            .distinct()
            .toSet()

        return interceptors
    }

    private fun composePreInitializers(): Set<PreInitializer<*>> {
        return registry.modules.asSequence()
            .map(Module::preInitializers)
            .flatten()
            .distinct()
            .toSet()
    }
}


class MainModule(val modules: ModuleRegistry,
                 val componentDefinitions: BeanDefinitionRegistry,
                 val properties: PropertySource = EmptyProperties,
                 var interceptors: Set<AspectDefinition>,
                 var preInitializers: Set<PreInitializer<*>>)