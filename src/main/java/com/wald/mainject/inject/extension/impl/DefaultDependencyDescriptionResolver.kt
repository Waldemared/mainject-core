package com.wald.mainject.inject.extension.impl

import com.wald.mainject.inject.DependencyDescriptor
import com.wald.mainject.inject.DependencyInfo
import com.wald.mainject.inject.extension.DependencyDescriptorResolver
import com.wald.mainject.inject.qualify.qualifier
import com.wald.mainject.inject.qualify.qualifiers
import kotlin.reflect.KParameter


/**
 * @author vkosolapov
 * @since
 */
class DefaultDependencyDescriptionResolver : DependencyDescriptorResolver {
    override fun resolve(callableParameter: KParameter) = with(callableParameter) {
        DependencyInfo(type, qualifiers.toSet(), isOptional)
    }
}