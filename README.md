![Maven Central Version](https://img.shields.io/maven-central/v/io.github.tacascer/xml-processor?style=for-the-badge&logo=apache%20maven)
[![javadoc](https://javadoc.io/badge2/io.github.tacascer/xml-processor/javadoc.svg?style=for-the-badge)](https://javadoc.io/doc/io.github.tacascer/xml-processor)

![Build](https://github.com/tacascer-org/xml-processor/actions/workflows/build.yml/badge.svg?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tacascer-org_xml-processor&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tacascer-org_xml-processor)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=tacascer-org_xml-processor&metric=coverage)](https://sonarcloud.io/summary/new_code?id=tacascer-org_xml-processor)

# xml-processor
A set of utilities to process XML files.

## Flatten `xs:include`s

The `XMLIncludeFlattener` is a utility class in the `xml-processor` project. It is designed to recursively process XML files that include other XML files using the `xs:include` tag. The class reads an XML file and inlines all the included files into the main XML file.

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

### Handling Different Namespaces

The `XMLIncludeFlattener` class is designed to handle XML files that include other XML files from different namespaces. When processing, it inlines all the included files into the main XML file, regardless of their original namespaces.

However, it's important to note that the final namespace of the processed XML file will be that of the input file. This means that all the elements from the included files will be brought into the namespace of the input file.

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

If you use `XMLIncludeFlattener` to process `sample.xsd`, the result will be:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
    <xs:element name="sample" type="xs:string"/>
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

As you can see, even though `sample_1.xsd` was in a different namespace (`http://www.different.com`), after processing, the `sample_1` element is in the same namespace as the input file (`http://www.sample.com`). This is because the `XMLIncludeFlattener` brings all included elements into the namespace of the input file.
