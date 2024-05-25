package io.github.tacascer

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.file.shouldContainFile
import org.intellij.lang.annotations.Language
import org.jdom2.Document

class AbstractXmlFilterTest : FunSpec({
    test("given an output file that does not exist, when process(Path, Path) is called, then it should create the file") {
        // Given
        val filter =
            object : AbstractXmlFilter() {
                override fun process(input: Document): Document {
                    return input
                }
            }

        @Language("XML")
        val inputText =
            """
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:element name="sample" type="xs:string"/> 
            </xs:schema>
            """.trimIndent()
        val inputFile = tempfile().also { it.writeText(inputText) }

        val testDir = tempdir()

        // When
        filter.process(inputFile.toPath(), testDir.resolve("does_not_exist.xsd").toPath())

        // Then
        testDir.shouldContainFile("does_not_exist.xsd")
    }
})
