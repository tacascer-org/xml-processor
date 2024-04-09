package io.github.tacascer

import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.StringWriter
import java.nio.file.Path
import kotlin.io.path.inputStream

class XmlFlattener(private val input: Path) {
    fun process(): String {
        val builder = SAXBuilder()
        val document = builder.build(input.inputStream())
        val output = document.clone()
        val outputter = XMLOutputter(Format.getPrettyFormat())
        StringWriter().use {
            outputter.output(output, it)
            return it.toString()
        }
    }
}
