package io.github.tacascer

import org.jdom2.Document
import org.jdom2.input.SAXBuilder
import org.jdom2.output.XMLOutputter
import java.io.StringWriter
import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.inputStream

fun Path.toDocument(): Document = SAXBuilder().build(this.inputStream())

/**
 * Abstract implementation of [XmlFilter] that provides default implementations for processing XML content.
 */
abstract class AbstractXmlFilter : XmlFilter {

    /**
     * Applies the filter to the input XML content and returns the filtered content.
     *
     * Must call [process] to process the input XML content.
     *
     * @param input the XML content to process
     * @return the filtered XML content
     */
    override fun apply(input: String): String {
        val output = process(SAXBuilder().build(input.byteInputStream()))
        return StringWriter().use {
            XMLOutputter(Formatters.COMPACT).output(output, it)
            it.toString()
        }
    }

    /**
     * Processes the input XML content and returns the filtered content.
     *
     * Must call [process] to process the input XML content.
     *
     * @param input the XML content to process
     * @return the filtered XML content
     */
    override fun process(input: Path): String {
        val output = process(input.toDocument())
        return StringWriter().use {
            XMLOutputter(Formatters.PRETTY).output(output, it)
            it.toString()
        }
    }

    /**
     * Processes the input XML content and writes the filtered content to the output file.
     * Creates the output file if it does not exist.
     *
     * Must call [process] to process the input XML content.
     *
     * @param input the XML content to process
     * @param output the file to write the filtered content to
     */
    override fun process(input: Path, output: Path) {
        output.createFileSafely()
        output.bufferedWriter().use {
            XMLOutputter(Formatters.PRETTY).output(process(input.toDocument()), it)
        }
    }

    /**
     * Processes the input XML content and returns the filtered content.
     *
     * @param input the XML content to process
     * @return the filtered XML content
     *
     */
    protected abstract fun process(input: Document): Document
}
