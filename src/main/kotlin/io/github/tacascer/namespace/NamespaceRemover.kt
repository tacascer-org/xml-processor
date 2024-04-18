package io.github.tacascer.namespace

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.tacascer.Formatters
import io.github.tacascer.XmlFilter
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import org.jdom2.filter.Filters
import org.jdom2.input.SAXBuilder
import org.jdom2.output.XMLOutputter
import java.io.StringWriter
import java.nio.file.Path
import kotlin.io.path.inputStream

private val logger = KotlinLogging.logger {}

/**
 * Removes all namespaces from an XML file. This includes defined namespaces and namespace prefixes in attributes.
 */
class NamespaceRemover : XmlFilter {
    private val saxBuilder = SAXBuilder()

    /**
     * Removes all namespaces from the XML content.
     *
     * @param input the XML content to process
     * @return the XML content with all namespaces removed
     */
    override fun apply(input: String): String {
        val output = process(saxBuilder.build(input.byteInputStream()))
        return StringWriter().use {
            XMLOutputter(Formatters.COMPACT).output(output, it)
            it.toString()
        }
    }

    /**
     * Processes the XML file and returns the content with all namespaces removed.
     *
     * @param input the path to the XML file to process
     * @return the XML content with all namespaces removed
     */
    override fun process(input: Path): String {
        val output = process(input.toDocument())
        return StringWriter().use {
            XMLOutputter(Formatters.PRETTY).output(output, it)
            it.toString()
        }
    }

    /**
     * Processes the XML file and writes the content with all namespaces removed to the specified output path.
     *
     * @param input the path to the XML file to process
     * @param output the path to the output file
     */
    override fun process(input: Path, output: Path) {
        val outputDocument = process(input.toDocument())
        output.toFile().bufferedWriter().use {
            XMLOutputter(Formatters.PRETTY).output(outputDocument, it)
        }
    }

    private fun process(input: Document): Document {
        process(input.rootElement)
        input.rootElement.getDescendants(Filters.element()).map { process(it) }
        return input
    }

    private fun process(element: Element) {
        logger.info { "Stripping namespace from element: $element" }
        val namespaces = element.additionalNamespaces.toList()
        namespaces.forEach { element.removeNamespaceDeclaration(it) }
        element.namespace = Namespace.NO_NAMESPACE
        element.attributes.forEach {
            logger.info { "Stripping namespace from attribute: $it" }
            it.namespace = Namespace.NO_NAMESPACE
            if (it.name == "type") {
                it.value = it.value.replaceBefore(":", "").removePrefix(":")
            }
        }
    }

    private fun Path.toDocument(): Document = saxBuilder.build(inputStream())
}
