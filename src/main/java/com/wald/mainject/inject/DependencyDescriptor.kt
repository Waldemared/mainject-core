package com.wald.mainject.inject

import com.wald.mainject.inject.definition.BeanDefinition
import javax.inject.Provider
import kotlin.reflect.KClass
import kotlin.reflect.KType


/**
 * @author vkosolapov
 * @since
 */
interface DependencyDescriptor {
    val type: KType

    val qualifiers: Set<QualifierDescriptor>

    val optional: Boolean
}