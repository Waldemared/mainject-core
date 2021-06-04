package com.wald.mainject.config.dsl

import com.wald.mainject.inject.definition.AspectDefinition


/**
 * @author vkosolapov
 * @since
 */
class InterceptionDsl {
    val interceptors: MutableSet<AspectDefinition> = mutableSetOf()


}