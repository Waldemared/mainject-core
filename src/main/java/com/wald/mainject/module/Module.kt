package com.wald.mainject.module

import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.inject.definition.AspectDefinition
import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.lifecylce.PreInitializer

/**
 * @author vkosolapov
 * @since
 */
interface Module {
    /**
     * Unique name of this module
     */
    val name: String

    /**
     * Bean definitions supplied by this module
     */
    val registeredBeans: Set<BeanDefinition<*>>

    /**
     * References to modules imported by this module
     */
    val imports: Set<ModuleRef>

    /**
     * Sources of [ModuleRef] objects to be read by container
     */
    val importedSources: Set<ModulesSource>

    /**
     * Sources of properties to be read by container
     */
    val propertySources: Set<PropertySource>

    val interceptors: Set<AspectDefinition>

    val preInitializers: Set<PreInitializer<*>>
}