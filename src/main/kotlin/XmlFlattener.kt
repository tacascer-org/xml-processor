package io.github.tacascer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jdom2.Element
import org.jdom2.filter.Filters
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.StringWriter
import java.nio.file.Path
import kotlin.io.path.inputStream

private val logger = KotlinLogging.logger {}

/**
 * Flattens an XML file by inlining all the included schemas.
 * @param input the path to the XML file to process
 */
class XmlFlattener(private val input: Path) {
    private val saxBuilder: SAXBuilder = SAXBuilder()
    private val document = saxBuilder.build(input.inputStream())

    /**
     * Processes the XML file and returns the flattened content.
     * @return the flattened XML content
     * @throws Exception if an error occurs during processing
     */
    fun process(): String {
        val includes = document.getDescendants(Filters.element()).filter { it.name == "include" }

        for (include in includes) {
            include.detach()
            inlineInclude(include)
        }

        val outputter = XMLOutputter(Format.getPrettyFormat().setIndent(" ".repeat(4)))
        StringWriter().use {
            outputter.output(document, it)
            return it.toString()
        }
    }

    private fun inlineInclude(include: Element) {
        val includedSchema = include.getAttributeValue("schemaLocation")
        logger.info { "Processing include: $includedSchema" }
        val includedDocument = saxBuilder.build(input.parent.resolve(includedSchema).inputStream())
        val includedElements = includedDocument.rootElement.getDescendants(Filters.element()).toList()
        for (element in includedElements) {
            element.detach()
            document.rootElement.addContent(element)
        }
    }
}
