package io.github.tacascer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.filter.Filters
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.StringWriter
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.toPath

private val logger = KotlinLogging.logger {}

/**
 * Flattens an XML file by inlining all the schemas specified in `include` elements.
 * @param input the path to the XML file to process
 * @param format the format to use for the output
 *
 * **Note:** The included schemas must be specified using the `include` element with the `schemaLocation` attribute.
 * The `schemaLocation` attribute must contain a valid URI to the included schema.
 *
 * @see [XML Inclusions](https://www.w3schools.com/xml/el_include.asp)
 */
class XmlIncludeFlattener(private val input: Path, format: Format = Format.getPrettyFormat().setIndent(" ".repeat(4))) {
    private val outputter = XMLOutputter(format)
    private val saxBuilder = SAXBuilder()
    private val inputDocument = saxBuilder.build(input.inputStream())

    /**
     * Processes the XML file and returns the flattened content.
     * @return the flattened XML content
     * @throws Exception if an error occurs during processing
     */
    fun process(): String {
        val output = process(inputDocument)
        return StringWriter().use {
            outputter.output(output, it)
            it.toString()
        }
    }

    private fun process(inputDocument: Document): Document {
        val output = inputDocument.clone()
        val includeElements = output.getDescendants(Filters.element()).filter { it.name == "include" }
        includeElements.forEach(Element::detach)
        includeElements.forEach { inline(it, output) }
        return output
    }

    private fun inline(include: Element, output: Document) {
        val includedSchema = include.getAttributeValue("schemaLocation")
        logger.info { "Processing included schema: $includedSchema" }
        val includedDocument = saxBuilder.build(URI(includedSchema).toPath().inputStream())
        val inlinedDocument = process(includedDocument)
        val inlinedElements = inlinedDocument.rootElement.getDescendants(Filters.element()).toList()
        inlinedElements.forEach(Element::detach)
        inlinedElements.forEach<Element?>(output.rootElement::addContent)
    }
}
