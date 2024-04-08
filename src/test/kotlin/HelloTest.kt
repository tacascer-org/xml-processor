import io.github.tacascer.Hello
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class HelloTest : FunSpec({
    test("Hello returns hello") {
        Hello().hello() shouldBe "Hello, world!"
    }
})
