package com.wald.mainject.utils

import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger


/**
 * @author vkosolapov
 * @since
 */
class TestLogger(val delegate: Logger) : Logger by delegate {
    override fun debug(msg: String?) {
        println(msg)
    }
}

fun testLogger() = TestLogger(NOPLogger.NOP_LOGGER)