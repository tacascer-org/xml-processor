package io.github.tacascer.flatten

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.tacascer.AbstractXmlFilter
import io.github.tacascer.toDocument
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.filter.Filters
import java.net.URI
import kotlin.io.path.toPath

private val logger = KotlinLogging.logger {}

private const val CLASSPATH_PREFIX = "classpath:"

/**
 * Flattens an XML file by inlining all the schemas specified in `include` elements.
 *
 * **Note:** The included schemas must be specified using the `include` element with the `schemaLocation` attribute.
 *
 * @see [XML Inclusions](https://www.w3schools.com/xml/el_include.asp)
 */
class IncludeFlattener() : AbstractXmlFilter() {

    override fun process(input: Document): Document {
        val output = input.clone()
        val includeElements = output.getDescendants(Filters.element()).filter { it.name == "include" }
        includeElements.forEach(Element::detach)
        includeElements
            .map { it.toDocument().inline() }
            .forEach { includedDocument ->
                output.rootElement.addContent(
                    includedDocument.rootElement.getDescendants(Filters.element()).map(Element::clone)
                )
            }
        return output
    }

    private fun Element.toDocument(): Document {
        val includedSchema = getAttributeValue("schemaLocation")
        logger.info { "Processing included schema: $includedSchema" }
        val schemaURI =
            if (includedSchema.startsWith(CLASSPATH_PREFIX)) {
                this::class.java.classLoader.getResource(includedSchema.removePrefix(CLASSPATH_PREFIX))?.toURI()
                    ?: throw IllegalArgumentException("Included schema not found: $includedSchema")
            } else URI(includedSchema)
        return schemaURI.toPath().toDocument()
    }

    private fun Document.inline(): Document {
        val inlinedDocument = process(this)
        val output = inlinedDocument.clone()
        return output
    }

    override fun toString(): String {
        return "IncludeFlattener()"
    }
}
