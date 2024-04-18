package io.github.tacascer

/**
 * Generic interface for filtering XML content.
 */
fun interface XmlFilter {
    /**
     * Applies the filter to the input XML content.
     * @param input the XML content to filter
     * @return the filtered XML content
     * @throws Exception if an error occurs during filtering
     */
    fun apply(input: String): String
}
