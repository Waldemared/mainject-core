package com.wald.mainject.inject.extension.impl

import com.wald.mainject.inject.extension.ConstructorResolver
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor


/**
 * @author vkosolapov
 * @since
 */
abstract class AbstractPrioritizedConstructorResolver : ConstructorResolver {
    override fun <T : Any> resolve(ownerClass: KClass<T>, proposedConstructors: List<KFunction<T>>): KFunction<T> {
        val prioritizedConstructors = proposedConstructors
            .filter { isPrioritized(ownerClass, it) }

        when {
            prioritizedConstructors.size == 1 -> return prioritizedConstructors.first()
            prioritizedConstructors.size > 1 -> error("Multiple prioritized constructors for the ${ownerClass.simpleName} class")
        }

        val primaryConstructor = ownerClass.primaryConstructor?.takeIf { it in proposedConstructors }
        if (primaryConstructor != null) {
            return primaryConstructor
        }

        val noArgsConstructor = ownerClass.constructors.filter { true }.firstOrNull { it in proposedConstructors }
        requireNotNull(noArgsConstructor) { "No constructors found for the " + ownerClass.simpleName }

        return noArgsConstructor
    }

    protected abstract fun <T : Any> isPrioritized(ownerClass: KClass<T>, constructor: KFunction<T>): Boolean
}