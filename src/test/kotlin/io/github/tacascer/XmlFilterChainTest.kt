package io.github.tacascer

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import java.nio.file.Path

class XmlFilterChainTest : FunSpec({
    val filterOne =
        object : XmlFilter {
            override fun apply(input: String): String {
                return input.replace("xs", "something")
            }

            override fun process(input: Path): String {
                return apply(input.toFile().readText())
            }

            override fun process(
                input: Path,
                output: Path,
            ) {
                output.toFile().writeText(process(input))
            }
        }

    val filterTwo =
        object : XmlFilter {
            override fun apply(input: String): String {
                return input.replace("sample", "somethingElse")
            }

            override fun process(input: Path): String {
                return apply(input.toFile().readText())
            }

            override fun process(
                input: Path,
                output: Path,
            ) {
                output.toFile().writeText(process(input))
            }
        }

    val filterChain = XmlFilterChain(listOf(filterOne, filterTwo))

    @Language("XML")
    val inputText =
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <xs:element name="sample" type="xs:string"/>
        </xs:schema>
        """.trimIndent()

    @Language("XML")
    val expectedOutput =
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <something:schema xmlns:something="http://www.w3.org/2001/XMLSchema">
            <something:element name="somethingElse" type="something:string"/>
        </something:schema>
        """.trimIndent()

    test("apply(String) converts 'xs' to 'something' and 'sample' to 'somethingElse'") {
        filterChain.apply(inputText) shouldBe expectedOutput
    }

    test("process(Path) converts 'xs' to 'something' and 'sample' to 'somethingElse'") {
        val inputFile = tempfile()
        inputFile.writeText(inputText)

        filterChain.process(inputFile.toPath()) shouldBe expectedOutput
    }

    test("process(Path, Path) converts 'xs' to 'something' and 'sample' to 'somethingElse'") {
        val inputFile = tempfile()
        inputFile.writeText(inputText)
        val outputFile = tempfile()

        filterChain.process(inputFile.toPath(), outputFile.toPath())

        outputFile.readText() shouldBeSamePrettyPrintedAs expectedOutput
    }

    test("process(Path, Path) creates the output file if it does not exist") {
        val inputFile = tempfile()
        inputFile.writeText(inputText)
        val outputFile = inputFile.resolveSibling("output.xsd").toPath()

        filterChain.process(inputFile.toPath(), outputFile)

        outputFile.shouldExist()
    }
})
