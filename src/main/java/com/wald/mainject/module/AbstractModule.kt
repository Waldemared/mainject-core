package com.wald.mainject.module

import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.inject.definition.AspectDefinition
import com.wald.mainject.inject.definition.BeanDefinition


/**
 * @author vkosolapov
 * @since
 */
abstract class AbstractModule(final override val name: String,
                              override val registeredBeans: Set<BeanDefinition<*>> = emptySet(),
                              override val imports: Set<ModuleRef> = emptySet(),
                              override val importedSources: Set<ModulesSource> = emptySet(),
                              override val propertySources: Set<PropertySource> = emptySet(),
                              override val interceptors: Set<AspectDefinition> = emptySet()) : Module {
    init {
        if (name.isBlank()) {
            error("Module should have unique and non empty name")
        }
    }
}