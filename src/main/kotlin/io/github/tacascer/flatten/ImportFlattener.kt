package io.github.tacascer.flatten

import io.github.tacascer.AbstractXmlFilter
import org.jdom2.Document
import org.jdom2.Element
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
        val importElements = input.getDescendants(Filters.element()).filter { it.name == "import" }
        importElements.forEach {
            val importedDocument = process(it.toDocument())
            input.rootElement.addContent(importedDocument.rootElement.getDescendants(Filters.element()).map(Element::clone))
            it.detach()
        }
        return input
    }

    override fun toString(): String {
        return "ImportFlattener()"
    }
}
