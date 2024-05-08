package io.github.tacascer.flatten

import io.github.tacascer.AbstractXmlFilter
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.filter.Filters

/**
 * Flattens an XML file by inlining all the schemas specified in `include` elements.
 *
 * **Note:** The included schemas must be specified using the `include` element with the `schemaLocation` attribute.
 *
 * [XML Inclusions](https://www.w3schools.com/xml/el_include.asp)
 */
class IncludeFlattener() : AbstractXmlFilter() {

    override fun process(input: Document): Document {
        val includeElements = input.getDescendants(Filters.element()).filter { it.name == "include" }
        includeElements.forEach {
            val includedDocument = process(it.toDocument())
            input.rootElement.addContent(
                includedDocument.rootElement.getDescendants(Filters.element()).map(Element::clone)
            )
            it.detach()
        }
        return input
    }

    override fun toString(): String {
        return "IncludeFlattener()"
    }
}
