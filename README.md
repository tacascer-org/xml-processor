[ ![Maven  Central Version](https://img.shields.io/maven-central/v/io.github.tacascer/xml-processor?style=for-the-badge&logo=apache%20maven)](https://central.sonatype.com/artifact/io.github.tacascer/xml-processor)
[![javadoc](https://javadoc.io/badge2/io.github.tacascer/xml-processor/javadoc.svg?style=for-the-badge)](https://javadoc.io/doc/io.github.tacascer/xml-processor)

![Build](https://github.com/tacascer-org/xml-processor/actions/workflows/build.yml/badge.svg?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tacascer-org_xml-processor&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tacascer-org_xml-processor)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=tacascer-org_xml-processor&metric=coverage)](https://sonarcloud.io/summary/new_code?id=tacascer-org_xml-processor)

# xml-processor

A set of utilities to process XML files.

## Flatten `include`

The `XMLIncludeFlattener` is a utility class in the `xml-processor` project. It is designed to recursively process XML
files that include other XML files using the `xs:include` tag. The class reads an XML file and inlines all the included
files into the main XML file.

### Usage

To use the `XMLIncludeFlattener`, you need to create an instance of the class and call the `process` method. The
constructor of the class takes a `Path` object that points to the XML file you want to process.

Here is a basic example:

```kotlin
val xmlFile = Paths.get("path/to/your/xml/file.xml")
val flattener = XmlIncludeFlattener(xmlFile)
val result = flattener.process()
```

Consult the JavaDoc for more information on the `XMLIncludeFlattener` APIs.

In this example, `result` will be a `String` that contains the XML content of the processed file with all included files
inlined.

### Example

Consider the following XML files:

`sampleDir/sample.xsd`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
    <xs:include schemaLocation="sample_1.xsd"/>
    <xs:element name="sample" type="xs:string"/>
</xs:schema>
```

`sampleDir/sample_1.xsd`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

When processed, the result will be:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
    <xs:element name="sample" type="xs:string"/>
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

As you can see, the `xs:include` tag has been replaced with the content of the included file.

### Handling Different Namespaces

The `XMLIncludeFlattener` class is designed to handle XML files that include other XML files from different namespaces.
When processing, it inlines all the included files into the main XML file, regardless of their original namespaces.

However, it's important to note that the final namespace of the processed XML file will be that of the input file. This
means that all the elements from the included files will be brought into the namespace of the input file.

Here's an example:

Consider the following XML files:

`sampleDir/sample.xsd`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
    <xs:include schemaLocation="sample_1.xsd"/>
    <xs:element name="sample" type="xs:string"/>
</xs:schema>
```

`sampleDir/sample_1.xsd`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.different.com">
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

When processed, the result will be:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
    <xs:element name="sample" type="xs:string"/>
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

### Classpath Parsing

This feature allows you to include XML files that are located in the classpath of your application.

#### Usage

To include an XML file from the classpath, you need to use the `classpath:` prefix in the `schemaLocation` attribute of
the `xs:include` tag. Here is an example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
    <xs:include schemaLocation="classpath:sample_1.xsd"/>
    <xs:element name="sample" type="xs:string"/>
</xs:schema>
```

In this example, `sample_1.xsd` is located in the classpath of the application. The `XMLIncludeFlattener` class will
correctly resolve this path and include the content of `sample_1.xsd` in the processed XML file.

## Namespace Remover

The `NamespaceRemover` class is designed to remove all namespaces from an XML document. This includes defined namespaces
and namespace prefixes in attributes.

### Usage

To use the `NamespaceRemover`, you need to create an instance of the class and call the appropriate method based on your
needs. Here are some basic examples:

#### Removing Namespaces from a String

```kotlin
val namespaceRemover = NamespaceRemover()
val xmlString = "<root xmlns=\"http://www.example.com\"><child>text</child></root>"
val result = namespaceRemover.apply(xmlString)
```

In this example, `result` will be a `String` that contains the XML content of the processed string with all namespaces
removed.

#### Removing Namespaces from a File

```kotlin
val namespaceRemover = NamespaceRemover()
val xmlFile = Paths.get("path/to/your/xml/file.xml")
val result = namespaceRemover.process(xmlFile)
```

In this example, `result` will be a `String` that contains the XML content of the processed file with all namespaces
removed.

#### Removing Namespaces from a File and Writing to Another File

```kotlin
val namespaceRemover = NamespaceRemover()
val inputFile = Paths.get("path/to/your/input/xml/file.xml")
val outputFile = Paths.get("path/to/your/output/xml/file.xml")
namespaceRemover.process(inputFile, outputFile)
```

In this example, the input file will be processed and the result (with all namespaces removed) will be written to the
output file.
