package com.wald.mainject.inject.extension

import kotlin.reflect.KClass
import kotlin.reflect.KFunction


/**
 * @author vkosolapov
 * @since
 */
interface ConstructorResolver {
    fun <T : Any> resolve(ownerClass: KClass<T>, proposedConstructors: List<KFunction<T>>) : KFunction<T>
}