package io.github.tacascer.flatten

import io.github.tacascer.AbstractXmlFilter
import org.jdom2.Document
import org.jdom2.filter.Filters

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

    private fun Document.inline(): Document {
        val inlinedDocument = process(this)
        val output = inlinedDocument.clone()
        return output
    }

    override fun toString(): String {
        return "ImportFlattener()"
    }
}

