package com.wald.mainject.inject

import com.wald.mainject.inject.definition.BeanDefinition
import kotlin.reflect.KType


/**
 * @author vkosolapov
 * @since
 */
class DependencyInfo(override val type: KType,
                     override val qualifiers: Set<QualifierDescriptor>,
                     override val optional: Boolean) : DependencyDescriptor