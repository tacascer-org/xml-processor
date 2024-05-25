package io.github.tacascer.namespace

import io.github.tacascer.shouldBeSameCompactedAs
import io.github.tacascer.shouldBeSamePrettyPrintedAs
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import org.intellij.lang.annotations.Language
import kotlin.io.path.readText
import kotlin.io.path.writeText

class NamespaceRemoverTest : FunSpec({
    context("given an XML with namespaces") {
        @Language("XML")
        val input =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://www.sample.com" xmlns:jaxb='http://java.sun.com/xml/ns/jaxb' targetNamespace="http://www.sample.com">
                <xs:complexType name="complex">
                    <xs:sequence>
                        <xs:element name="simple" type="xs:string" />
                    </xs:sequence>
                </xs:complexType>
                <xs:element name="sample" type="xs:string" />
                <xs:element name="complex" type="tns:complex"/>
                <jaxb:bindings>
                    <jaxb:globalBindings>
                    </jaxb:globalBindings>
                </jaxb:bindings>
            </xs:schema>
            """.trimIndent()

        context("fully formatted XML result") {
            @Language("XML")
            val expected =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                    <xs:complexType name="complex">
                        <xs:sequence>
                            <xs:element name="simple" type="xs:string" />
                        </xs:sequence>
                    </xs:complexType>
                    <xs:element name="sample" type="xs:string" />
                    <xs:element name="complex" type="complex" />
                    <bindings>
                        <globalBindings />
                    </bindings>
                </xs:schema>
                
                """.trimIndent()

            val inputPath = tempfile().toPath()
            inputPath.writeText(input)

            test("apply(String) should remove all namespaces") {
                NamespaceRemover().process(inputPath) shouldBeSamePrettyPrintedAs expected
            }

            test("process(Path) should remove all namespaces") {
                val outputPath = tempfile().toPath()

                NamespaceRemover().process(inputPath, outputPath)

                outputPath.readText() shouldBeSamePrettyPrintedAs expected
            }
        }

        context("compacted XML result") {
            @Language("XML")
            val expected =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com"><xs:complexType name="complex"><xs:sequence><xs:element name="simple" type="xs:string" /></xs:sequence></xs:complexType><xs:element name="sample" type="xs:string" /><xs:element name="complex" type="complex" /><bindings><globalBindings /></bindings></xs:schema>
                """.trimIndent()

            test("apply(String) should remove all namespaces") {
                NamespaceRemover().apply(input) shouldBeSameCompactedAs expected
            }
        }
    }
})
