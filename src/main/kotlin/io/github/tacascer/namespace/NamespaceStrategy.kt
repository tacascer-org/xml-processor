package io.github.tacascer.namespace

import org.jdom2.Element

fun interface NamespaceStrategy {
    fun process(element: Element)
}
