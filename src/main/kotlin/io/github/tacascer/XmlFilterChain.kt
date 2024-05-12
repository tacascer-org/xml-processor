package io.github.tacascer

import dev.drewhamilton.poko.Poko
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * Represents a chain of [XmlFilter] instances. Is itself a [XmlFilter].
 * It applies the filters in the order they are added to the chain.
 *
 * @constructor Creates an instance of XmlFilterChain with the given list of [filters].
 */
@Poko
class XmlFilterChain(private val filters: List<XmlFilter>) : XmlFilter {

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
     * Creates the output file if it does not exist.
     *
     * @param input The path to the input file to be processed.
     * @param output The path to the output file where the processed content will be written.
     */
    override fun process(input: Path, output: Path) {
        if (!output.exists()) output.createFile()
        val content = process(input)
        output.writeText(content)
    }
}
