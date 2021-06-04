package com.wald.mainject.inject.extension

import com.wald.mainject.inject.DependencyDescriptor
import kotlin.reflect.KParameter


/**
 * @author vkosolapov
 * @since
 */
interface DependencyDescriptorResolver {
    fun resolve(callableParameter: KParameter): DependencyDescriptor
}