package io.github.tacascer

import java.nio.file.Path

/**
 * Generic interface for filtering XML content.
 */
interface XmlFilter {
    /**
     * Applies the filter to the input XML content.
     *
     * This method is meant to be used when this method is being used in a filter chain.
     * @param input the XML content to filter
     * @return the filtered XML content
     * @throws Exception if an error occurs during filtering
     */
    fun apply(input: String): String

    /**
     * Processes the XML file and returns the filtered content.
     *
     * Will pretty format the output using [org.jdom2.output.Format.getPrettyFormat].
     * @param input the path to the XML file to process
     * @return the filtered XML content
     */
    fun process(input: Path): String

    /**
     * Processes the XML file and writes the filtered content to the output file.
     *
     * Will pretty format the output using [org.jdom2.output.Format.getPrettyFormat].
     * @param input the path to the XML file to process
     * @param output the path to the output file
     */
    fun process(input: Path, output: Path)
}