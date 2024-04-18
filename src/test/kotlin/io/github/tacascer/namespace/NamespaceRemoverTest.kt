package io.github.tacascer.namespace

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import kotlin.io.path.readText
import kotlin.io.path.writeText

class NamespaceRemoverTest : FunSpec({
    context("given an XML with namespaces") {
        @Language("XML") val input = """
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                <xs:element name="sample" type="xs:string"/>
            </xs:schema>
        """.trimIndent()

        context("fully formatted XML result") {
            @Language("XML") val expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <schema targetNamespace="http://www.sample.com">
                    <element name="sample" type="string" />
                </schema>
                
            """.trimIndent()

            val inputPath = tempfile().toPath()
            inputPath.writeText(input)

            test("apply(String) should remove all namespaces") {
                NamespaceRemover().process(inputPath).replace("\r\n", "\n") shouldBe expected
            }

            test("process(Path) should remove all namespaces") {
                val outputPath = tempfile().toPath()

                NamespaceRemover().process(inputPath, outputPath)

                outputPath.readText().replace("\r\n", "\n") shouldBe expected
            }
        }

        context("compacted XML result") {
            @Language("XML") val expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <schema targetNamespace="http://www.sample.com"><element name="sample" type="string" /></schema>
                
            """.trimIndent()

            test("apply(String) should remove all namespaces") {
                NamespaceRemover().apply(input).replace("\r\n", "\n") shouldBe expected
            }
        }
    }
})