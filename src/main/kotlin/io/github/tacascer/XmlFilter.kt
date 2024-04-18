package io.github.tacascer

fun interface XmlFilter {
    fun apply(input: String): String
}
