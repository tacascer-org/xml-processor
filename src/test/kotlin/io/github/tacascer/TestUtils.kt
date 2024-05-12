package io.github.tacascer

infix fun String.shouldBeSameCompactedAs(other: String): Boolean {
    val thisLines = this.lines().map { it.trim() }
    val otherLines = other.lines().map { it.trim() }
    return thisLines == otherLines
}

infix fun String.shouldBeSamePrettyPrintedAs(other: String): Boolean {
    return this.replace("\r\n", "\n") == other.replace("\r\n", "\n")
}
