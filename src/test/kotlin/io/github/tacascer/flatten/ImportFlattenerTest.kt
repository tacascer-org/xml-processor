package io.github.tacascer.flatten

import io.github.tacascer.shouldBeSameCompactedAs
import io.github.tacascer.shouldBeSamePrettyPrintedAs
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.engine.spec.tempfile
import org.intellij.lang.annotations.Language
import kotlin.io.path.createFile
import kotlin.io.path.toPath
import kotlin.io.path.writeText

class ImportFlattenerTest : FunSpec({
    context("given two xsd files") {
        val testDir = tempdir()
        val importingFile = testDir.toPath().resolve("sample.xsd").createFile()
        val importedFile = testDir.toPath().resolve("sample_1.xsd").createFile()
        context("given one xsd that imports another xsd through URI") {
            @Language("XML")
            val importingFileText =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                    <xs:import schemaLocation="${testDir.toPath().resolve("sample_1.xsd").toUri()}"/>
                    <xs:element name="sample" type="xs:string"/>
                </xs:schema>
                """.trimIndent()
            importingFile.writeText(importingFileText)
            @Language("XML")
            val importedFileText =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                    <xs:element name="sampleOne" type="xs:string"/>
                </xs:schema>
                """.trimIndent()
            importedFile.writeText(importedFileText)

            context("fully formatted xsd result") {
                @Language("XML")
                val expectedText =
                    """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                        <xs:element name="sample" type="xs:string" />
                        <xs:element name="sampleOne" type="xs:string" />
                    </xs:schema>
                    """.trimIndent()
                test("should return a single xsd with all elements") {
                    ImportFlattener().process(importingFile) shouldBeSamePrettyPrintedAs expectedText
                }
                test("process(Path) should write out a single xsd with all elements") {
                    val outputFile = tempfile("output", ".xsd")
                    ImportFlattener().process(importingFile, outputFile.toPath())
                    outputFile.readText() shouldBeSamePrettyPrintedAs expectedText
                }
            }

            context("compacted xsd result") {
                test("apply(String) should return a compacted xsd with all elements") {
                    @Language("XML")
                    val expectedText = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com"><xs:element name="sample" type="xs:string" /><xs:element name="sampleOne" type="xs:string" /></xs:schema>
                """
                    ImportFlattener().apply(importingFileText) shouldBeSameCompactedAs expectedText.trimIndent()
                }
            }
        }

        context("given xsds that have different namespaces") {
            @Language("XML")
            val importingFileText =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                    <xs:import schemaLocation="${testDir.toPath().resolve("sample_1.xsd").toUri()}"/>
                    <xs:element name="sample" type="xs:string"/>
                </xs:schema>
                """.trimIndent()
            importingFile.writeText(
                importingFileText,
            )
            @Language("XML")
            val importedFileText =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample1.com">
                    <xs:element name="sampleOne" type="xs:string"/>
                </xs:schema>
                """.trimIndent()
            importedFile.writeText(importedFileText)
            @Language("XML")
            val expectedText =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                    <xs:element name="sample" type="xs:string" />
                    <xs:element name="sampleOne" type="xs:string" />
                </xs:schema>
                """.trimIndent()

            test("should return a single xsd with all elements whose targetNamespace is the input xsd") {
                ImportFlattener().process(importingFile) shouldBeSamePrettyPrintedAs expectedText
            }
        }
    }

    context("classpath includes") {
        test("given one xsd that includes another xsd that doesn't exist on the classpath, then should throw IllegalArgumentException") {
            val importingFile =
                this::class.java.classLoader.getResource("io/github/tacascer/flatten/import/sample_invalid_import.xsd")!!
                    .toURI().toPath()

            shouldThrow<IllegalArgumentException> {
                ImportFlattener().process(importingFile)
            }
        }
        test("given one xsd that includes another xsd through classpath, then should return a single xsd with all elements") {
            val importingFile =
                this::class.java.classLoader.getResource("io/github/tacascer/flatten/import/sample.xsd")!!.toURI()
                    .toPath()

            @Language("XML")
            val expectedText =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                    <xs:element name="sample" type="xs:string" />
                    <xs:element name="sampleOne" type="xs:string" />
                </xs:schema>
                """.trimIndent()

            ImportFlattener().process(importingFile) shouldBeSamePrettyPrintedAs expectedText
        }
    }

    test("given one xsd that includes another xsd that includes another, then should return a single xsd with all elements") {
        val testDir = tempdir()
        val importingFile = testDir.toPath().resolve("sample.xsd").createFile()
        val importedFile = testDir.toPath().resolve("sample_1.xsd").createFile()
        val importedFile2 = testDir.toPath().resolve("sample_2.xsd").createFile()

        @Language("XML")
        val importingFileText =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                <xs:import schemaLocation="${testDir.toPath().resolve("sample_1.xsd").toUri()}"/>
                <xs:element name="sample" type="xs:string"/>
            </xs:schema>
            """.trimIndent()
        importingFile.writeText(
            importingFileText,
        )
        @Language("XML")
        val importedFileText =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:import schemaLocation="${testDir.toPath().resolve("sample_2.xsd").toUri()}"/>
                <xs:element name="sampleOne" type="xs:string"/>
            </xs:schema>
            """.trimIndent()
        importedFile.writeText(importedFileText)
        @Language("XML")
        val importedFile2Text =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:element name="sampleTwo" type="xs:string"/>
            </xs:schema>
            """.trimIndent()
        importedFile2.writeText(importedFile2Text)

        @Language("XML")
        val expectedText =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
                <xs:element name="sample" type="xs:string" />
                <xs:element name="sampleOne" type="xs:string" />
                <xs:element name="sampleTwo" type="xs:string" />
            </xs:schema>
            """.trimIndent()
        ImportFlattener().process(importingFile) shouldBeSamePrettyPrintedAs expectedText
    }
})
