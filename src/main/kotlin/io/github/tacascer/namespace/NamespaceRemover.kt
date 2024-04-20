package io.github.tacascer.namespace

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.tacascer.AbstractXmlFilter
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import org.jdom2.filter.Filters

private val logger = KotlinLogging.logger {}

/**
 * Removes all namespaces from an XML file. This includes defined namespaces and namespace prefixes in attributes.
 */
class NamespaceRemover : AbstractXmlFilter() {
    /**
     * Removes all namespaces from the input XML content.
     * @param input the XML content to process
     * @return the XML content with all namespaces removed
     */
    override fun process(input: Document): Document {
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

    override fun toString(): String {
        return "NamespaceRemover()"
    }
}
