package io.github.tacascer

import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * Represents a chain of [XmlFilter] instances. Is itself a [XmlFilter].
 * It applies the filters in the order they are added to the chain.
 *
 * @property filters The list of [XmlFilter] instances that make up the chain.
 */
class XmlFilterChain(private val filters: List<XmlFilter>) : XmlFilter {

    /**
     * Secondary constructor that accepts a variable number of [XmlFilter] instances.
     *
     * @param filters The [XmlFilter] instances that make up the chain.
     */
    constructor(vararg filters: XmlFilter) : this(filters.toList())

    /**
     * Applies all the filters in the chain to the input string.
     * The filters are applied in the order they are added to the chain.
     *
     * @param input The input string to be processed.
     * @return The processed string after all filters have been applied.
     */
    override fun apply(input: String): String {
        return filters.fold(input) { acc, filter -> filter.apply(acc) }
    }

    /**
     * Reads the content of the input file, applies all the filters in the chain to the content,
     * and returns the processed content as a string.
     * The filters are applied in the order they are added to the chain.
     *
     * @param input The path to the input file to be processed.
     * @return The processed content of the file after all filters have been applied.
     */
    override fun process(input: Path): String {
        return filters.fold(input.readText()) { acc, filter -> filter.apply(acc) }
    }

    /**
     * Reads the content of the input file, applies all the filters in the chain to the content,
     * and writes the processed content to the output file.
     * The filters are applied in the order they are added to the chain.
     * If the output file already exists, it will be replaced.
     *
     * @param input The path to the input file to be processed.
     * @param output The path to the output file where the processed content will be written.
     */
    override fun process(input: Path, output: Path) {
        val content = process(input)
        output.writeText(content)
    }
}
