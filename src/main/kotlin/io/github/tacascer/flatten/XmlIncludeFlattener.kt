package io.github.tacascer.flatten

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.tacascer.Formatters
import io.github.tacascer.XmlFilter
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.filter.Filters
import org.jdom2.input.SAXBuilder
import org.jdom2.output.XMLOutputter
import java.io.FileWriter
import java.io.StringWriter
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.inputStream
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
class XmlIncludeFlattener() : XmlFilter {
    private val saxBuilder = SAXBuilder()

    /**
     * Flattens the XML content by inlining all the schemas specified in `include` elements.
     */
    override fun apply(input: String): String {
        val output = process(saxBuilder.build(input.byteInputStream()))
        return StringWriter().use {
            XMLOutputter(Formatters.COMPACT).output(output, it)
            it.toString()
        }
    }

    /**
     * Processes the XML file and returns the flattened content.
     * @param input the path to the XML file to process
     * @return the flattened XML content
     * @throws Exception if an error occurs during processing
     */
    override fun process(input: Path): String {
        val output = process(input.toDocument())
        return StringWriter().use {
            XMLOutputter(Formatters.PRETTY).output(output, it)
            it.toString()
        }
    }

    /**
     * Processes the XML file and writes the flattened content to the specified output path.
     * @param input the path to the XML file to process
     * @param output the path to the output file
     * @throws Exception if an error occurs during processing
     */
    override fun process(input: Path, output: Path) {
        return FileWriter(output.toFile()).use {
            XMLOutputter(Formatters.PRETTY).output(process(input.toDocument()), it)
        }
    }

    private fun process(inputDocument: Document): Document {
        val output = inputDocument.clone()
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

    private fun Path.toDocument(): Document = saxBuilder.build(inputStream())

    private fun Document.inline(): Document {
        val inlinedDocument = process(this)
        val output = inlinedDocument.clone()
        return output
    }
}