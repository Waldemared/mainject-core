package com.wald.mainject.inject.definition

import com.wald.mainject.inject.aop.Pointcut
import com.wald.mainject.inject.aop.advice.InterceptorRef


/**
 * @author vkosolapov
 * @since
 */
interface AspectDefinition {
    val advice: InterceptorRef
    val pointcut: Pointcut
}