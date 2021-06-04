package com.wald.mainject.inject

import com.wald.mainject.inject.definition.ComponentKey
import com.wald.mainject.inject.definition.PlainComponentKey
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf


/**
 * @author vkosolapov
 * @since
 */
class Container(private val components: ComponentRegistry,
                private val memberInjector: MemberInjector) {

    fun <T : Any> get(key: ComponentKey) = getProvider<T>(key).get()

    fun <T : Any> get(clazz: KClass<T>) = getProvider(clazz).get()

    fun <T : Any> get(clazz: Class<T>) = getProvider(clazz).get()

    @ExperimentalStdlibApi
    inline fun <reified T : Any> getOfType() = getProviderOfType<T>().get()

    fun <T : Any> getProvider(key: ComponentKey)  = components.getProvider<T>(key)

    fun <T : Any> getProvider(clazz: KClass<T>) = getProvider<T>(key = keyOfType(clazz.starProjectedType))

    fun <T : Any> getProvider(clazz: Class<T>) = getProvider(clazz.kotlin)

    @ExperimentalStdlibApi
    inline fun <reified T : Any> getProviderOfType() = getProvider<T>(keyOfType(typeOf<T>()))

    fun inject(instance: Any) {
        memberInjector.accept(instance)
    }

    @PublishedApi
    internal fun keyOfType(type: KType) = PlainComponentKey(type = type, qualifiers = emptyList())
}