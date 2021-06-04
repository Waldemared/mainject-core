package com.wald.mainject.utils

import com.wald.mainject.inject.utils.resolveTemplates
import org.apache.tools.zip.ZipUtil
import org.junit.jupiter.api.Test
import javax.inject.Provider


/**
 * @author vkosolapov
 * @since
 */
class ClassesUtilsTest {
    @Test
    fun `testResolveTemplates`() {
        val provider2 = Provider2()

        Providere<Int, Long>()::class.resolveTemplates(Provider::class).also(::println)
        Provideo::class.resolveTemplates(Provider::class).also(::println)
        Provije::class.resolveTemplates(Provider::class).also(::println)
        Provider2::class.resolveTemplates(Provider1::class).also(::println)
    }
}

abstract class Provider1 : Provider<Integer>

class Provider2 : Provider1() {
    override fun get(): Integer {
        TODO("Not yet implemented")
    }
}
open class Providere<A : Any, B : Any> : Provider<B> {
    override fun get(): B {
        TODO("Not yet implemented")
    }
}

class Provideo<N : Any, P : Any> : Providere<Double, N>()

class Provije<R : Any> : Providere<R, ZipUtil>()