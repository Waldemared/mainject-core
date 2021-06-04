package com.wald.mainject.module

import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.inject.definition.AspectDefinition
import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.lifecylce.PreInitializer
import kotlin.reflect.KClass


/**
 * @author vkosolapov
 * @since
 */
class BaseModule(
    override val name: String,
    override val registeredBeans: Set<BeanDefinition<*>>,
    override val imports: Set<ModuleRef>,
    override val importedSources: Set<ModulesSource>,
    override val propertySources: Set<PropertySource>,
    override val interceptors: Set<AspectDefinition>,
    override val preInitializers: Set<PreInitializer<*>>
) : Module

val Module.ref: ModuleRef
    get() = ModuleValue(this)

val KClass<out Module>.ref: ModuleRef
    get() = ModuleClass(this)

data class OverridableModule(private val module: Module) : Module by module