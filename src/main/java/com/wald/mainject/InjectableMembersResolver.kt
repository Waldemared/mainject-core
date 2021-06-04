package com.wald.mainject

import com.wald.mainject.inject.extension.LateinitPropertySelector
import com.wald.mainject.inject.extension.SetterSelector
import com.wald.mainject.inject.utils.isPublic
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure


/**
 * @author vkosolapov
 * @since
 */
class InjectableMembersResolver(private val propertySelector: LateinitPropertySelector,
                                private val setterSelector: SetterSelector) {
    fun resolve(clazz: KClass<*>): Set<KCallable<*>> {
        return selectLateinitProperties(clazz) + selectSetters(clazz)
    }

    private fun selectLateinitProperties(clazz: KClass<*>): Set<KMutableProperty<*>> {
        val properties = clazz.memberProperties.asSequence()
            .filter { it.isPublic }
            .filter { it is KMutableProperty<*> }
            .map { it as KMutableProperty<*> }
            .filter { propertySelector.isInjectable(it) }
            .toSet()

        return properties
    }

    private fun selectSetters(clazz: KClass<*>): Set<KFunction<Unit>> {
        val setters = clazz.memberFunctions.asSequence()
            .filter { it.isPublic }
            .filter { it.valueParameters.isNotEmpty() }
            .filter { it.returnType.jvmErasure == Unit::class }
            .map { it as KFunction<Unit> }
            .filter { setterSelector.isInjectable(it) }
            .toSet()

        return setters
    }
}