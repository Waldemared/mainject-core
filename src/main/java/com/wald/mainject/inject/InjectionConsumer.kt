package com.wald.mainject.inject

import com.wald.mainject.inject.definition.BeanDefinition
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty


/**
 * @author vkosolapov
 * @since
 */
class InjectionConsumer(val definition: BeanDefinition<*>,
                        val constructor: KFunction<*>?,
                        val lateinitProperties: List<KMutableProperty<*>>,
                        val setters: List<KFunction<*>>,
                        val initMethod: KFunction<Unit>,
                        val destroyMethod: KFunction<Unit>) {

}