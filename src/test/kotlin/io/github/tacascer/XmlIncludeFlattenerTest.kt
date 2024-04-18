package io.github.tacascer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import kotlin.io.path.createFile
import kotlin.io.path.toPath
import kotlin.io.path.writeText

class XmlIncludeFlattenerTest : FunSpec({
    context("given two xsd files") {
        val testDir = tempdir()
        val includingFile = testDir.toPath().resolve("sample.xsd").createFile()
        val includedFile = testDir.toPath().resolve("sample_1.xsd").createFile()


        context("given one xsd that includes another xsd through URI") {
            @Language("XML") val includingFileText = """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                <xs:include schemaLocation="${testDir.toPath().resolve("sample_1.xsd").toUri()}"/>
                <xs:element name="sample" type="xs:string"/>
            </xs:schema>
            """.trimIndent()
            includingFile.writeText(includingFileText)
            @Language("XML") val includedFileText = """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:element name="sampleOne" type="xs:string"/>
            </xs:schema>
            """.trimIndent()
            includedFile.writeText(includedFileText)
            @Language("XML") val expectedText = """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                <xs:element name="sample" type="xs:string" />
                <xs:element name="sampleOne" type="xs:string" />
            </xs:schema>
            """.trimIndent()

            test("should return a single xsd with all elements") {
                XmlIncludeFlattener().process(includingFile).trimIndent() shouldBe expectedText
            }

            test("process(Path) should write out a single xsd with all elements") {
                val outputFile = tempfile("output", ".xsd")
                XmlIncludeFlattener().process(includingFile, outputFile.toPath())
                outputFile.readText().trimIndent() shouldBe expectedText
            }

        }

        context("given xsds that have different namespaces") {
            @Language("XML") val includingFileText = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                    <xs:include schemaLocation="${testDir.toPath().resolve("sample_1.xsd").toUri()}"/>
                    <xs:element name="sample" type="xs:string"/>
                </xs:schema>
                """.trimIndent()
            includingFile.writeText(
                includingFileText
            )
            @Language("XML") val includedFileText = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample1.com">
                    <xs:element name="sampleOne" type="xs:string"/>
                </xs:schema>
                """.trimIndent()
            includedFile.writeText(includedFileText)
            @Language("XML") val expectedText = """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                <xs:element name="sample" type="xs:string" />
                <xs:element name="sampleOne" type="xs:string" />
            </xs:schema>
            """.trimIndent()

            test("should return a single xsd with all elements whose targetNamespace is the input xsd") {
                XmlIncludeFlattener().process(includingFile).trimIndent() shouldBe expectedText
            }
        }

        context("given xsds whose elements have different prefixes") {
            @Language("XML") val includingFileText = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                    <xs:include schemaLocation="${testDir.toPath().resolve("sample_1.xsd").toUri()}"/>
                    <xs:element name="sample" type="xs:string"/>
                </xs:schema>
                """.trimIndent()
            includingFile.writeText(
                includingFileText
            )
            @Language("XML") val includedFileText = """
                <?xml version="1.0" encoding="UTF-8"?>
                <schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                    <xsd:element xsd:name="sampleOne" type="xsd:string"/>
                </schema>
                """.trimIndent()
            includedFile.writeText(includedFileText)

            test("given stripNamespace is true, should return a single xsd with all elements whose namespaces are stripped") {
                @Language("XML") val expectedText = """
                <?xml version="1.0" encoding="UTF-8"?>
                <schema targetNamespace="http://www.sample.com">
                    <element name="sample" type="string" />
                    <element name="sampleOne" type="string" />
                </schema>
                """.trimIndent()
                XmlIncludeFlattener(stripNamespace = true).process(includingFile).trimIndent() shouldBe expectedText
            }
        }
    }

    context("classpath includes") {
        test("given one xsd that includes another xsd that doesn't exist on the classpath, then should throw IllegalArgumentException") {
            val includingFile = this::class.java.classLoader.getResource("sample_invalid_include.xsd")!!.toURI().toPath()

            shouldThrow<IllegalArgumentException> {
                XmlIncludeFlattener().process(includingFile)
            }
        }
        test("given one xsd that includes another xsd through classpath, then should return a single xsd with all elements") {
            val includingFile = this::class.java.classLoader.getResource("sample.xsd")!!.toURI().toPath()

            @Language("XML")
            val expectedText = """
            <?xml version="1.0" encoding="UTF-8"?>
            <schema xmlns="http://www.w3.org/2001/XMLSchema">
                <element name="sample" type="string" />
                <element name="sampleOne" type="string" />
            </schema>
            """.trimIndent()

            XmlIncludeFlattener().process(includingFile).trimIndent() shouldBe expectedText
        }
    }

    test("given one xsd that includes another xsd that includes another using relative path, then should return a single xsd with all elements") {
        val testDir = tempdir()
        val includingFile = testDir.toPath().resolve("sample.xsd").createFile()
        val includedFile = testDir.toPath().resolve("sample_1.xsd").createFile()
        val includedFile2 = testDir.toPath().resolve("sample_2.xsd").createFile()
        @Language("XML") val includingFileText = """
        <?xml version="1.0" encoding="UTF-8"?>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
            <xs:include schemaLocation="${testDir.toPath().resolve("sample_1.xsd").toUri()}"/>
            <xs:element name="sample" type="xs:string"/>
        </xs:schema>
        """.trimIndent()
        includingFile.writeText(
            includingFileText
        )
        @Language("XML") val includedFileText = """
        <?xml version="1.0" encoding="UTF-8"?>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <xs:include schemaLocation="${testDir.toPath().resolve("sample_2.xsd").toUri()}"/>
            <xs:element name="sampleOne" type="xs:string"/>
        </xs:schema>
        """.trimIndent()
        includedFile.writeText(includedFileText)
        @Language("XML") val includedFile2Text = """
        <?xml version="1.0" encoding="UTF-8"?>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <xs:element name="sampleTwo" type="xs:string"/>
        </xs:schema>
        """.trimIndent()
        includedFile2.writeText(includedFile2Text)

        @Language("XML") val expectedText = """
        <?xml version="1.0" encoding="UTF-8"?>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
            <xs:element name="sample" type="xs:string" />
            <xs:element name="sampleOne" type="xs:string" />
            <xs:element name="sampleTwo" type="xs:string" />
        </xs:schema>
        """.trimIndent()
        XmlIncludeFlattener().process(includingFile).trimIndent() shouldBe expectedText
    }
})
