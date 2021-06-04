package com.wald.mainject.inject.definition

import com.wald.mainject.config.ClassAndHashNamingService
import org.jetbrains.kotlin.cfg.pseudocode.instructions.eval.AccessTarget
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.instanceParameter
import kotlin.test.Test


/**
 * Tests of Kotlin features related to provided reflection
 *
 * @author vkosolapov
 */
class DbReflectionTest {
    @Test
    fun `test method reflection api`() {
        val classVar = SomeClass()

        val callable1 = SomeClass::someFunVoid1
        val callable2 = classVar::someFunVoid1

        val namingService = ClassAndHashNamingService()
        val cahnsClass = namingService::class
        val cahnsJavaKotlinClass = namingService::class.java
        val cahnsJavaClass = namingService.javaClass

        cahnsClass.constructors

        val methods = SomeClass::class.declaredFunctions.forEach {
            print(it)
            println(it.returnType)
            println(it.parameters)
            println(it.typeParameters)

            println()
            it.runCatching {
                call(classVar)
            }.getOrThrow().apply { println(this) }
        }



        callable2.instanceParameter

        fun e() {}
    }
}


class SomeClass {
    fun someFunVoid1() {}

    fun <T> someFunInt2(): Int { return 6 }

    fun someFunInt3(vararg declaration: AccessTarget.Declaration) = 4
}