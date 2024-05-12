package io.github.tacascer.flatten

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import kotlin.io.path.createFile
import kotlin.io.path.toPath
import kotlin.io.path.writeText

class IncludeFlattenerTest : FunSpec({
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

            context("fully formatted xsd result") {
                @Language("XML") val expectedText = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                    <xs:element name="sample" type="xs:string" />
                    <xs:element name="sampleOne" type="xs:string" />
                </xs:schema>
                """.trimIndent()
                test("should return a single xsd with all elements") {
                    IncludeFlattener().process(includingFile).trimIndent() shouldBe expectedText
                }
                test("process(Path) should write out a single xsd with all elements") {
                    val outputFile = tempfile("output", ".xsd")
                    IncludeFlattener().process(includingFile, outputFile.toPath())
                    outputFile.readText().trimIndent() shouldBe expectedText
                }
            }

            context("compacted xsd result") {
                test("apply(String) should return a compacted xsd with all elements") {
                    @Language("XML")
                    val expectedText = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com"><xs:element name="sample" type="xs:string" /><xs:element name="sampleOne" type="xs:string" /></xs:schema>
                """
                    IncludeFlattener().apply(includingFileText).trimIndent() shouldBe expectedText.trimIndent()
                }
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
                IncludeFlattener().process(includingFile).trimIndent() shouldBe expectedText
            }
        }
    }

    context("classpath includes") {
        test("given one xsd that includes another xsd that doesn't exist on the classpath, then should throw IllegalArgumentException") {
            val includingFile =
                this::class.java.classLoader.getResource("io/github/tacascer/flatten/include/sample_invalid_include.xsd")!!.toURI().toPath()

            shouldThrow<IllegalArgumentException> {
                IncludeFlattener().process(includingFile)
            }
        }
        test("given one xsd that includes another xsd through classpath, then should return a single xsd with all elements") {
            val includingFile = this::class.java.classLoader.getResource("io/github/tacascer/flatten/include/sample.xsd")!!.toURI().toPath()

            @Language("XML")
            val expectedText = """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:element name="sample" type="xs:string" />
                <xs:element name="sampleOne" type="xs:string" />
            </xs:schema>
            """.trimIndent()

            IncludeFlattener().process(includingFile).trimIndent() shouldBe expectedText
        }
    }

    test("given one xsd that includes another xsd that includes another, then should return a single xsd with all elements") {
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
        IncludeFlattener().process(includingFile).trimIndent() shouldBe expectedText
    }
})
