package com.wald.mainject.inject

import com.wald.mainject.inject.definition.*
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType


/**
 * @author vkosolapov
 * @since
 */
class DefinitionRegistry : BeanDefinitionRegistry {
    override val beanDefinitions: MutableSet<BeanDefinition<*>> = mutableSetOf()

    override fun add(beanDefinition: BeanDefinition<*>) {
        beanDefinitions += beanDefinition
    }

    override fun get(key: ComponentKey): List<BeanDefinition<*>> {
        return beanDefinitions.filter { it.satisfies(key) }
    }

    override fun <T : Any> getOfType(componentClass: KClass<T>): List<BeanDefinition<T>> {
        val key = PlainComponentKey(componentClass.starProjectedType, emptyList())
        return get(key) as List<BeanDefinition<T>>
    }

    override fun <T : Any> getWithName(componentName: String): BeanDefinition<T>? {
        return null
    }

    private fun BeanDefinition<*>.satisfies(key: ComponentKey): Boolean {
        return this.key.satisfies(key)
    }

    private fun <T : Any> Collection<T>.hasSameElements(other: Collection<T>): Boolean {
        return this.size == other.size && this.containsAll(other)
    }
}