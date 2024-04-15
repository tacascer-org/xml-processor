package io.github.tacascer.namespace

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jdom2.Element
import org.jdom2.Namespace


class NamespaceStrategiesTest : FunSpec({
    context("NamespaceStrategies") {
        test("StripNamespace should remove namespace from element") {
            val element = Element("test", "http://example.com")
            NamespaceStrategies.StripNamespace.process(element)

            element.namespace shouldBe Namespace.NO_NAMESPACE
        }

        test("KeepNamespace should not change namespace of element") {
            val element = Element("element", "example", "http://example.com")
            NamespaceStrategies.KeepNamespace.process(element)

            element.namespacePrefix shouldBe "example"
            element.namespaceURI shouldBe "http://example.com"
        }
    }
})
