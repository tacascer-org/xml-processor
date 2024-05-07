package io.github.tacascer.flatten

import io.github.tacascer.AbstractXmlFilter
import org.jdom2.Document
import org.jdom2.filter.Filters

/**
 * ImportFlattener is a class that represents a utility to process XML files that import other XML files.
 * It inlines all the imported files into the main XML file.
 *
 * @constructor Creates an instance of ImportFlattener.
 *
 * [XML Imports](https://www.w3schools.com/xml/el_import.asp)
 */
class ImportFlattener : AbstractXmlFilter() {
    override fun process(input: Document): Document {
        val output = input.clone()
        val importElements = output.getDescendants(Filters.element()).filter { it.name == "import" }
        importElements.forEach { it.detach() }
        importElements
            .map { it.toDocument().inline() }
            .forEach { includedDocument ->
                output.rootElement.addContent(
                    includedDocument.rootElement.getDescendants(Filters.element()).map { it.clone() }
                )
            }
        return output
    }

    /**
     * Inlines the imported files into the input XML document.
     *
     * @return The processed XML document with all imported files inlined.
     */
    private fun Document.inline(): Document {
        val inlinedDocument = process(this)
        val output = inlinedDocument.clone()
        return output
    }

    override fun toString(): String {
        return "ImportFlattener()"
    }
}
