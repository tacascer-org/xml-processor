package io.github.tacascer.namespace

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jdom2.Element
import org.jdom2.Namespace

private val logger = KotlinLogging.logger {}

enum class NamespaceStrategies : NamespaceStrategy {
    StripNamespace {
        override fun process(element: Element) {
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
    },
    KeepNamespace {
        override fun process(element: Element) {
            return
        }
    }
}
