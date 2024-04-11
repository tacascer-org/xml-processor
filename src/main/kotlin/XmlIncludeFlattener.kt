package io.github.tacascer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.filter.Filters
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.FileWriter
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

    /**
     * Processes the XML file and writes the flattened content to the specified output path.
     * @param outputPath the path to the output file
     * @throws Exception if an error occurs during processing
     */
    fun process(outputPath: Path) {
        val output = process(inputDocument)
        return FileWriter(outputPath.toFile()).use { outputter.output(output, it) }
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
        val includedDocument = saxBuilder.build(URI(includedSchema).toPath().inputStream())
        return includedDocument
    }

    private fun Document.inline(): Document {
        val inlinedDocument = process(this)
        val output = inlinedDocument.clone()
        return output
    }
}
