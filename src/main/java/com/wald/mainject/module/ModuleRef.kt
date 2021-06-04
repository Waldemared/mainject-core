package com.wald.mainject.module

import com.wald.mainject.inject.definition.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.typeOf



sealed class ModuleRef {
    abstract val clazz: KClass<out Module>
    abstract val value: Module

    fun override(overridesBuilder: OverridesBuilder.() -> Unit): ModuleRef {
        val overrides = OverridesBuilder().apply(overridesBuilder).build()
        return OverriddenModule(this, overrides)
    }
}


data class ModuleClass(override val clazz: KClass<out Module>) : ModuleRef() {
    override val value: Module = clazz.createInstance()

    constructor(clazz: Class<out Module>) : this(clazz.kotlin)
}


data class ModuleValue(override val value: Module) : ModuleRef() {
    override val clazz: KClass<out Module> = value::class
}


data class OverriddenModule(private val delegateRef: ModuleRef,
                            private val overrides: ModuleOverrides) : ModuleRef() {
    override val clazz: KClass<out Module> = delegateRef.clazz
    override val value: Module = with(delegateRef.value) {
        BaseModule(
            name = name,
            registeredBeans = overrideComponentDefinitions(registeredBeans),
            imports = imports,
            importedSources = importedSources,
            propertySources = propertySources,
            interceptors = interceptors,
            preInitializers = preInitializers
        )
    }

    private fun overrideComponentDefinitions(definitions: Set<BeanDefinition<*>>): Set<BeanDefinition<*>> {
        return (definitions + overrides.includedDefinitions).asSequence()
            .filterNot { overrides.shouldBeExcluded(it) }
            .toSet()
    }
}

data class ModuleOverrides(val includedDefinitions: Set<BeanDefinition<*>>,
                           val excludedDefinitions: Set<ComponentKey>) {

    fun shouldBeExcluded(definition: BeanDefinition<*>): Boolean {
        return excludedDefinitions.any { it.satisfies(definition.key) }
    }
}


class OverridesBuilder {
    internal val includedDefinition: MutableSet<BeanDefinition<*>> = mutableSetOf()
    @PublishedApi
    internal val excludedDefinitions: MutableSet<ComponentKey> = mutableSetOf()

    fun include(definition: BeanDefinition<*>) = apply { includedDefinition += definition }

    fun exclude(componentKey: ComponentKey) = apply { excludedDefinitions += componentKey }

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T : Any> excludeThat(keyBuilder: ComponentKeyBuilder.() -> Unit = {}) = apply {
        val key = ComponentKeyBuilder(typeOf<T>()).apply(keyBuilder).build()
        excludedDefinitions += key
    }

    fun build() = ModuleOverrides(includedDefinition, excludedDefinitions)
}