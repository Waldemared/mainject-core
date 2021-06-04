package com.wald.mainject.draft

import com.wald.mainject.inject.BeanDefinition
import com.wald.mainject.inject.BeanDefinitionRegistry
import com.wald.mainject.module.Module
import com.wald.mainject.module.ModulesSource
import com.wald.mainject.config.property.PropertySource
import kotlin.reflect.KClass

/**
 * @author vkosolapov
 * @since
 */
class CombinedModuleConfiguration(

    ) {

    val registeredBeans = object : BeanDefinitionRegistry {
        override val beanDefinitions: MutableList<BeanDefinition<*>>
            get() = TODO("Not yet implemented")

        override fun <T : Any> getWithName(componentName: String): BeanDefinition<T>? {
            TODO("Not yet implemented")
        }

        override fun <T : Any> getOfType(componentClass: Class<T>): List<BeanDefinition<T>> {
            TODO("Not yet implemented")
        }

        override fun <T : Any> getOfType(componentClass: KClass<T>): List<BeanDefinition<T>> {
            TODO("Not yet implemented")
        }

        override fun add(beanDefinition: BeanDefinition<*>) {
            TODO("Not yet implemented")
        }
    }

    val propertySources = emptySet<PropertySource>()

    val configurationSources = emptySet<ModulesSource>()
}