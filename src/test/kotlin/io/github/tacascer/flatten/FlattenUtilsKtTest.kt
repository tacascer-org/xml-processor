package io.github.tacascer.flatten

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import org.jdom2.Element

class FlattenUtilsKtTest : FunSpec({
    test("given an element that is not an import or include element, when toDocument is called, then it should throw an exception") {
        // Given
        val element = Element("element")

        // When, Then
        shouldThrow<IllegalArgumentException> {
            element.toDocument()
        }
    }
})
