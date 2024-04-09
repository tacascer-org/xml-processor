
import io.github.tacascer.XmlIncludeFlattener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import kotlin.io.path.createFile
import kotlin.io.path.writeText

class XmlIncludeFlattenerTest : FunSpec({
    test("given one xsd that includes another xsd, should return a single xsd with all elements") {
        val testDir = tempdir()
        val includingFile = testDir.toPath().resolve("sample.xsd").createFile()
        val includedFile = testDir.toPath().resolve("sample_1.xsd").createFile()
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
        XmlIncludeFlattener(includingFile).process().trimIndent() shouldBe  expectedText
    }
})
