package io.github.tacascer

import org.jdom2.output.Format
import org.jdom2.output.LineSeparator

internal object Formatters {
    val PRETTY: Format = Format.getPrettyFormat().setIndent(" ".repeat(4)).setLineSeparator(LineSeparator.SYSTEM)
    val COMPACT: Format = Format.getCompactFormat().setLineSeparator(LineSeparator.SYSTEM)
}
