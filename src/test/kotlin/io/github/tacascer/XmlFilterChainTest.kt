package io.github.tacascer

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import java.nio.file.Path

class XmlFilterChainTest : FunSpec({
    val filterOne = object : XmlFilter {
        override fun apply(input: String): String {
            return input.replace("a", "b")
        }

        override fun process(input: Path): String {
            return apply(input.toFile().readText())
        }

        override fun process(input: Path, output: Path) {
            output.toFile().writeText(process(input))
        }
    }

    val filterTwo = object : XmlFilter {
        override fun apply(input: String): String {
            return input.replace("b", "c")
        }

        override fun process(input: Path): String {
            return apply(input.toFile().readText())
        }

        override fun process(input: Path, output: Path) {
            output.toFile().writeText(process(input))
        }
    }

    val filterChain = XmlFilterChain(filterOne, filterTwo)

    test("apply converts 'a' to 'b' and 'b' to 'c'") {
        filterChain.apply("abc") shouldBe "ccc"
    }

    test("process(Path) converts 'a' to 'b' and 'b' to 'c'") {
        val inputFile = tempfile()
        inputFile.writeText("abc")

        filterChain.process(inputFile.toPath()) shouldBe "ccc"
    }

    test("process(Path, Path) converts 'a' to 'b' and 'b' to 'c'") {
        val inputFile = tempfile()
        inputFile.writeText("abc")
        val outputFile = tempfile()

        filterChain.process(inputFile.toPath(), outputFile.toPath())

        outputFile.readText() shouldBe "ccc"
    }
})
