package io.github.tacascer

import org.jdom2.Document
import org.jdom2.input.SAXBuilder
import org.jdom2.output.XMLOutputter
import java.io.FileWriter
import java.io.StringWriter
import java.nio.file.Path
import kotlin.io.path.inputStream

fun Path.toDocument(): Document = SAXBuilder().build(this.inputStream())

/**
 * Abstract implementation of [XmlFilter] that provides default implementations for processing XML content.
 */
abstract class AbstractXmlFilter : XmlFilter {
    private val saxBuilder = SAXBuilder()

    /**
     * Calls [process] with the input XML content and returns the filtered content.
     */
    override fun apply(input: String): String {
        val output = process(saxBuilder.build(input.byteInputStream()))
        return StringWriter().use {
            XMLOutputter(Formatters.COMPACT).output(output, it)
            it.toString()
        }
    }

    /**
     * Calls [process] with the input XML content and returns the filtered content.
     */
    override fun process(input: Path): String {
        val output = process(input.toDocument())
        return StringWriter().use {
            XMLOutputter(Formatters.PRETTY).output(output, it)
            it.toString()
        }
    }

    /**
     * Calls [process] with the input XML content and writes the filtered content to the output file.
     */
    override fun process(input: Path, output: Path) {
        return FileWriter(output.toFile()).use {
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
