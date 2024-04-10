[![Maven Central](https://img.shields.io/maven-central/v/io.github.tacascer/xml-processor.svg?label=Maven%20Central&logo=apachemaven)](https://central.sonatype.com/artifact/io.github.tacascer/xml-processor/0.1.0/overview)

![Build](https://github.com/tacascer-org/xml-processor/actions/workflows/build.yml/badge.svg?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tacascer-org_xml-processor&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tacascer-org_xml-processor)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=tacascer-org_xml-processor&metric=coverage)](https://sonarcloud.io/summary/new_code?id=tacascer-org_xml-processor)

# xml-processor
A set of utilities to process XML files.

## Flatten `xs:includes`

The `XMLIncludeFlattener` is a utility class in the `xml-processor` project. It is designed to process XML files that include other XML files using the `xs:include` tag. The class reads an XML file and inlines all the included files into the main XML file.

### Usage

To use the `XMLIncludeFlattener`, you need to create an instance of the class and call the `process` method. The constructor of the class takes a `Path` object that points to the XML file you want to process.

Here is a basic example:

```kotlin
val xmlFile = Paths.get("path/to/your/xml/file.xml")
val flattener = XmlIncludeFlattener(xmlFile)
val result = flattener.process()
```

In this example, `result` will be a `String` that contains the XML content of the processed file with all included files inlined.

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

If you use `XMLIncludeFlattener` to process `sample.xsd`, the result will be:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
    <xs:element name="sample" type="xs:string"/>
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

As you can see, the `xs:include` tag has been replaced with the content of the included file.

