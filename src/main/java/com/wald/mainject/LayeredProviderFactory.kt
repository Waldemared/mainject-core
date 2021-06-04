package com.wald.mainject

import com.wald.mainject.inject.Component
import com.wald.mainject.inject.ProviderComposer
import javax.inject.Provider


/**
 * @author vkosolapov
 * @since
 */
class LayeredProviderFactory : ProviderComposer {
    override fun create(component: Component<*>): Provider<*> {
        component as Component<Any>
        var provider = initializedInstance(component)
        provider = component.scope.wrap(provider) { instance ->
            component.destructors.forEach { it.call(instance) }
        }

        return provider
    }

    fun <T : Any> initializedInstance(component: Component<T>): Provider<T> {
        val constructedInstanceProvider = Provider<T> { component.constructor!!.callConstructor() as T }
        return constructedInstanceProvider.applyForInstance {
            component.members.forEach { it.inject(this) }
            component.preInitializers.forEach { it.apply(this) }
            component.initializers.forEach { it.call(this) }
        }
    }

    private fun <T : Any> Provider<T>.applyForInstance(instanceTransformer: T.() -> Unit): ApplicableProvider<T> {
        return object : ApplicableProvider<T>(this) {
            override fun apply(instance: T) {
                instanceTransformer(instance)
            }
        }
    }

    abstract class ApplicableProvider<T : Any>(val baseProvider: Provider<T>): Provider<T> {
        override fun get(): T {
            val instance: T = baseProvider.get()
            apply(instance)
            return instance
        }

        abstract fun apply(instance: T)
    }
}