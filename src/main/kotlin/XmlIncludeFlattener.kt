package io.github.tacascer

import io.github.oshai.kotlinlogging.KotlinLogging
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
        doProcess()
        return StringWriter().use {
            outputter.output(inputDocument, it)
            it.toString()
        }
    }

    private fun doProcess() {
        inputDocument.getDescendants(Filters.element()).filter { it.name == "include" }.forEach {
            it.detach()
            inlineInclude(it)
        }
    }

    private fun inlineInclude(include: Element) {
        val includedSchema = include.getAttributeValue("schemaLocation")
        logger.info { "Processing included schema: $includedSchema" }
        val includedDocument = saxBuilder.build(URI(includedSchema).toPath().inputStream())
        includedDocument.rootElement.getDescendants(Filters.element()).toList().forEach {
            it.detach()
            inputDocument.rootElement.addContent(it)
        }
    }
}
