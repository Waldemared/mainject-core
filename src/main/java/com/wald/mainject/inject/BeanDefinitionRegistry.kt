package com.wald.mainject.inject

import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.definition.ComponentKey
import kotlin.reflect.KClass


/**
 * @author vkosolapov
 * @since
 */
interface BeanDefinitionRegistry {
    val beanDefinitions: Set<BeanDefinition<*>>

    fun add(beanDefinition: BeanDefinition<*>)

    operator fun get(key: ComponentKey): List<BeanDefinition<*>>

    fun <T : Any> getWithName(componentName: String): BeanDefinition<T>?

    fun <T : Any> getOfType(componentClass: Class<T>): List<BeanDefinition<T>> = getOfType(componentClass.kotlin)

    fun <T : Any> getOfType(componentClass: KClass<T>): List<BeanDefinition<T>>
}