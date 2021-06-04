package com.wald.mainject.config.property.source

import com.wald.mainject.config.property.resolver.PlainPropertyResolver
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 * @author vkosolapov
 * @since
 */
class ResolvablePropertiesTest {
    @Test
    fun `test resolves String objects`() {
        val propertySource = PlainProperties(
            "property1" to "resolved1",
            "property2" to STRING_REF
        ).resolveWith(PROPERTY_RESOLVER)

        assertEquals("resolved1", propertySource["property1"])
        assertEquals(RESOLVED_STRING, propertySource["property2"])
    }

    @Test
    fun `test resolves Iterable objects`() {
        val propertySource = PlainProperties(
            "property1" to "resolved1",
            "property2" to RAW_LIST
        ).resolveWith(PROPERTY_RESOLVER)

        assertEquals("resolved1", propertySource["property1"])
        assertEquals(RESOLVED_LIST, propertySource["property2"])
    }

    @Test
    fun `test resolves Map objects`() {
        val propertySource = PlainProperties(
            "property1" to "resolved1",
            "property2" to mapOf(
                "key1" to STRING_REF,
                "key2" to RAW_LIST,
                "key3" to "value3"
            )
        ).resolveWith(PROPERTY_RESOLVER)

        val expectedMap = mapOf(
            "key1" to RESOLVED_STRING,
            "key2" to RESOLVED_LIST,
            "key3" to "value3"
        )

        assertEquals("resolved1", propertySource["property1"])
        assertEquals(expectedMap, propertySource["property2"])
    }


    companion object {
        private const val STRING_REF = "stringKey"
        private const val RESOLVED_STRING = "string value"

        private const val INTEGER_REF = "integerKey"
        private const val RESOLVED_INTEGER = 500

        private val RESOLVED_LIST = listOf(RESOLVED_STRING, RESOLVED_INTEGER, "value2", "value3", "value4")
        private val RAW_LIST = listOf(STRING_REF, INTEGER_REF, "value2", "value3", "value4")

        private val bindings = mapOf<String, Any?>(
            STRING_REF to RESOLVED_STRING,
            INTEGER_REF to RESOLVED_INTEGER
        )

        private val PROPERTY_RESOLVER = PlainPropertyResolver(bindings)
    }
}