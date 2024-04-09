
import io.github.tacascer.XmlFlattener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class XmlFlattenerTest : FunSpec({
    test("given two files on the same directory, the XmlFlattener should merge them into a single file") {
        val includingFile = tempfile("sample", ".xsd")
        val includedFile = tempfile("sample_1", ".xsd")
        @Language("XML") val includingFileText = """
        <?xml version="1.0" encoding="UTF-8" ?>
        <xs:schema targetNamespace="http://www.sample.com" xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <xs:include schemaLocation="sample_1.xsd"/>
            <xs:element name="sample" type="xs:string"/>
        </xs:schema>
        """.trimIndent()
        includingFile.writeText(
            includingFileText
        )
        @Language("XML") val includedFileText = """
        <?xml version="1.0" encoding="UTF-8" ?>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <xs:element name="sampleOne" type="xs:string"/>
        </xs:schema>
        """.trimIndent()
        includedFile.writeText(includedFileText)

        @Language("XML") val expectedText = """
        <?xml version="1.0" encoding="UTF-8" ?>
        <xs:schema targetNamespace="http://www.sample.com" xmlns:xs="http://www.w3.org/2001/XMLSchema" >
            <xs:element name="sampleOne" type="xs:string"/>
            <xs:element name="sample" type="xs:string"/>
        </xs:schema>
        """.trimIndent()
        XmlFlattener(includingFile.toPath()).process() shouldBe expectedText
    }
//    test("can flatten xml files on the classpath") {
//        XmlFlattener(Path(this::class.java.getClassLoader().getResource("sample.xsd")!!.path)).process() shouldBe """
//        <?xml version="1.0" encoding="UTF-8" ?>
//        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
//            <xs:element name="root" type="xs:string"/>
//        </xs:schema>
//        """.trimIndent()
//    }
})
