package com.wald.mainject.inject

import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.definition.ComponentKey
import com.wald.mainject.inject.definition.key
import com.wald.mainject.inject.definition.satisfies
import javax.inject.Provider


/**
 * @author vkosolapov
 * @since
 */
class ComponentRegistry {
    private val components: MutableMap<BeanDefinition<*>, Component<*>> = mutableMapOf()

    fun <T : Any> register(component: Component<T>) {
        components[component.definition] = component
    }

    operator fun get(definition: BeanDefinition<*>): Component<*>? {
        return components[definition]
    }

    operator fun get(key: ComponentKey): Component<*> {
        return components.keys
            .firstOrNull { it.key.satisfies(key) }
            ?.let(this::get)
            ?: error("No component found that satisfies $key")
    }

    fun <T : Any> getProvider(key: ComponentKey): Provider<T> {
        return (get(key).provider ?: error("No provided resolved for component $key")) as Provider<T>
    }

    operator fun contains(definition: BeanDefinition<*>): Boolean {
        return definition in components
    }
}