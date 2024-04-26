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
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://www.sample.com" targetNamespace="http://www.sample.com">
                <xs:complexType name="complex">
                    <xs:sequence>
                        <xs:element name="simple" type="xs:string" />
                    </xs:sequence>
                </xs:complexType>
                <xs:element name="sample" type="xs:string" />
                <xs:element name="complex" type="tns:complex"/>
            </xs:schema>
            """.trimIndent()

        context("fully formatted XML result") {
            @Language("XML") val expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                    <xs:complexType name="complex">
                        <xs:sequence>
                            <xs:element name="simple" type="xs:string" />
                        </xs:sequence>
                    </xs:complexType>
                    <xs:element name="sample" type="xs:string" />
                    <xs:element name="complex" type="complex" />
                </xs:schema>
                
            """.trimIndent()

            val inputPath = tempfile().toPath()
            inputPath.writeText(input)

            test("apply(String) should remove all namespaces") {
                NamespaceRemover().process(inputPath).replace("\r\n", "\n") shouldBe expected
            }

            test("process(Path) should remove all namespaces") {
                val outputPath = tempfile().toPath()

                NamespaceRemover().process(inputPath, outputPath)

                outputPath.readText().lines().joinToString("\n") shouldBe expected
            }
        }

        context("compacted XML result") {
            @Language("XML") val expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com"><xs:complexType name="complex"><xs:sequence><xs:element name="simple" type="xs:string" /></xs:sequence></xs:complexType><xs:element name="sample" type="xs:string" /><xs:element name="complex" type="complex" /></xs:schema>
                
            """.trimIndent()

            test("apply(String) should remove all namespaces") {
                NamespaceRemover().apply(input).lines().joinToString("\n") shouldBe expected
            }
        }
    }

    context("non functional tests") {
        test("toString() should return the class name") {
            NamespaceRemover().toString() shouldBe "NamespaceRemover()"
        }
    }
})
