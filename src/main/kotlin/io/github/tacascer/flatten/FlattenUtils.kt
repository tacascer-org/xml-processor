package io.github.tacascer.flatten

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.tacascer.toDocument
import org.jdom2.Document
import org.jdom2.Element
import java.net.URI
import kotlin.io.path.toPath

private val logger = KotlinLogging.logger {}

private const val CLASSPATH_PREFIX = "classpath:"

/**
 * Flattens an XML file by inlining all the schemas specified in the `schemaLocation` attribute of the element.
 */
internal fun Element.toDocument(): Document {
    require(name == "import" || name == "include") { "Element must be an import or include element" }
    val inlined = getAttributeValue("schemaLocation")
    logger.info { "Processing inlined schema: $inlined" }
    val schemaURI =
        if (inlined.startsWith(CLASSPATH_PREFIX)) {
            this::class.java.classLoader.getResource(inlined.removePrefix(CLASSPATH_PREFIX))?.toURI()
                ?: throw IllegalArgumentException("Imported schema not found: $inlined")
        } else URI(inlined)
    return schemaURI.toPath().toDocument()
}
