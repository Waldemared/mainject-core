package com.wald.mainject.config.property.source

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


/**
 * @author vkosolapov
 */
class SystemPropertiesTest {
    @Test
    fun `test add and get new property`() {
        val propertyName = "some.property"
        assertNull(System.getProperty(propertyName), "Property $propertyName already set")

        val propertyValue = "some value"
        System.setProperty(propertyName, propertyValue)

        val actualValue = RuntimeSystemProperties[propertyName]
        assertEquals(propertyValue, actualValue)

        removeProperty(propertyName)
    }

    @Test
    fun `test check missing property`() {
        val propertyName = "some.property"
        assertTrue(propertyName !in System.getProperties())

        assertNull(RuntimeSystemProperties[propertyName])
    }

    @Test
    fun `test get maven property`() {
        val propertyName = "test.property"
        assertTrue(propertyName in RuntimeSystemProperties)

        val expectedValue = "test-value"
        val value = RuntimeSystemProperties[propertyName]
        assertEquals(expectedValue, value)
    }

    private fun removeProperty(propertyName: String) {
        System.getProperties().remove(propertyName)
    }
}