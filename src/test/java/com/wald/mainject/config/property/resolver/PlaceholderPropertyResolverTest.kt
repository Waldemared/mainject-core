package com.wald.mainject.config.property.resolver

import com.wald.mainject.config.property.asResolvable
import com.wald.mainject.config.property.source.MixedPropertySource
import com.wald.mainject.config.property.source.PlainProperties
import com.wald.mainject.config.property.source.PropertiesFile
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals


/**
 * @author vkosolapov
 * @since
 */
class PlaceholderPropertyResolverTest {
    @Test
    fun `External property source`() {
        val propertySource = PlainProperties(
            "password_key" to "321"
        )
        val resolver = PlaceholderPropertyResolver("\${", "}", propertySource)

        val unresolved = PlainProperties(
            "login" to "123",
            "password" to "\${password_key}"
        )

        val resolved = unresolved.asResolvable {
            resolveWith(resolver)
        }

        val expected = mapOf<String, Any>(
            "login" to "123",
            "password" to "321"
        )

        assertEquals(expected, resolved.properties)
    }

    @Test
    fun `Missing required property`() {
        val resolver = PlaceholderPropertyResolver("\${", "}")

        val unresolved = PlainProperties(
            "login" to "123",
            "use_cache" to "\${use_cache_k}"
        )

        val resolved = unresolved.asResolvable {
            resolveWith(resolver)
        }

        assertEquals(unresolved.properties, resolved.properties)
    }

    @Test
    fun `Cyclic resolving different placeholder bounds`() {

        val resolverA = PlaceholderPropertyResolver("\${", "}")
        val resolverB = PlaceholderPropertyResolver("\$[", "]")


        val unresolvedPropertiesB = PlainProperties(
            "b_key" to "\$[c_key]"
        )

        val unresolvedPropertiesA = PlainProperties(
            "A_key" to "\${b_key}",
            "c_key" to "123"
        )

        val unresolvedSource = MixedPropertySource(unresolvedPropertiesA, unresolvedPropertiesB)


        // TODO implement if it is possible
    }

    @Test
    fun `Several levels resolving with different placeholder bounds`() {
        val unresolvedLevelA = PlainProperties("A_key" to "\${B_key} two")
        val unresolvedLevelB = PlainProperties("B_key" to "\$[C_key] one")
        val resolvedLevelC = PlainProperties("C_key" to "THE_VALUE")

        val resolvedLevelB = unresolvedLevelB.asResolvable {
            resolveWith(PlaceholderPropertyResolver("\$[", "]", resolvedLevelC))
        }
        val resolvedLevelA = unresolvedLevelA.asResolvable {
            resolveWith(PlaceholderPropertyResolver("\${", "}", resolvedLevelB))
        }

        val actualPropertiesLevelA = resolvedLevelA
        assertEquals("THE_VALUE one two", actualPropertiesLevelA["A_key"])

        val actualPropertiesLevelB = resolvedLevelB
        assertEquals("THE_VALUE one", actualPropertiesLevelB["B_key"])
    }

    @Test
    fun `Several levels resolving with same placeholder bounds`() {
        val unresolvedLevelA = PlainProperties("A_key" to "\${B_key}")
        val unresolvedLevelB = PlainProperties("B_key" to "\${C_key}")
        val resolvedLevelC = PlainProperties("C_key" to "THE_VALUE")

        val resolvedLevelB = unresolvedLevelB.asResolvable {
            resolveWith(PlaceholderPropertyResolver("\${", "}", resolvedLevelC))
        }
        val resolvedLevelA = unresolvedLevelA.asResolvable {
            resolveWith(PlaceholderPropertyResolver("\${", "}", resolvedLevelB))
        }

        val actualPropertiesLevelA = resolvedLevelA
        assertEquals("THE_VALUE", actualPropertiesLevelA["A_key"])

        val actualPropertiesLevelB = resolvedLevelB
        assertEquals("THE_VALUE", actualPropertiesLevelB["B_key"])
    }


    @Test
    fun `Self resolving multiple placeholders`() {
        val rawProperties = PlainProperties(
            "url" to "\${schema}://\${host}:\${port}/\${path}",
            "schema" to "https",
            "port" to 6223,
            "host" to "domain.com",
            "path_tail" to "8/7",
            "path_head" to "1/2",
            "path" to "\${path_head}/\${path_tail}"
        )

        val resolved = rawProperties.asResolvable {
            resolveWith(PlaceholderPropertyResolver("\${", "}"))
        }

        val urlToResolve = "https://domain.com:6223/1/2/8/7"

        assertEquals(urlToResolve, resolved["url"])
    }

    @Test
    fun `Multiple placeholders`() {
        val rawMultiProperties = PlainProperties("url" to "\${schema}://\${host}:\${port}/\${path}")

        val sourceProperties = PlainProperties(
            "schema" to "https",
            "port" to 6223,
            "host" to "domain.com",
            "path_tail" to "8/7",
            "path_head" to "1/2",
            "path" to "\${path_head}/\${path_tail}"
        )

        val resolved = rawMultiProperties.asResolvable {
            resolveWith(PlaceholderPropertyResolver("\${", "}", sourceProperties))
        }

        val urlToResolve = "https://domain.com:6223/1/2/8/7"

        assertEquals(urlToResolve, resolved["url"])
    }

    @Test
    fun `File data with multiple placeholders`() {
        val rawMultiProperties = PlainProperties("url" to "\${schema}://\${host}:\${port}/\${path}")

        val sourceProperties = PropertiesFile(classpathFile("property/url_data_only.properties"))

        val resolved = rawMultiProperties.asResolvable {
            resolveWith(PlaceholderPropertyResolver("\${", "}", sourceProperties))
        }

        val urlToResolve = "https://domain.com:6223/1/2/8/7"

        assertEquals(urlToResolve, resolved["url"])
    }

    @Test
    fun `File data with self resolving multiple placeholders`() {
        val rawProperties = PropertiesFile(classpathFile("property/url_with_data.properties"))

        val resolved = rawProperties.asResolvable {
            resolveWith(PlaceholderPropertyResolver("\${", "}"))
        }

        val urlToResolve = "https://domain.com:6223/1/2/8/7"

        assertEquals(urlToResolve, resolved["url"])
    }

    private fun classpathFile(path: String): File {
        val fileUrl = javaClass.classLoader.getResource(path)
        return File(fileUrl!!.toURI())
    }
}