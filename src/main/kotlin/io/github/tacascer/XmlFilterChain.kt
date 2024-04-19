package io.github.tacascer

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.readText

class XmlFilterChain(private val filters: List<XmlFilter>) : XmlFilter {

    constructor(vararg filters: XmlFilter) : this(filters.toList())

    override fun apply(input: String): String {
        return filters.fold(input) { acc, filter -> filter.apply(acc) }
    }

    override fun process(input: Path): String {
        return filters.fold(input.readText()) { acc, filter -> filter.apply(acc) }
    }

    override fun process(input: Path, output: Path) {
        val content = process(input)
        Files.copy(content.byteInputStream(), output, StandardCopyOption.REPLACE_EXISTING)
    }
}
