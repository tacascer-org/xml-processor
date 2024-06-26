# xml-processor

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.tacascer/xml-processor?style=for-the-badge&logo=apache%20maven)](https://central.sonatype.com/artifact/io.github.tacascer/xml-processor)
[![javadoc](https://javadoc.io/badge2/io.github.tacascer/xml-processor/javadoc.svg?style=for-the-badge)](https://javadoc.io/doc/io.github.tacascer/xml-processor)

![Build](https://github.com/tacascer-org/xml-processor/actions/workflows/build.yml/badge.svg?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tacascer-org_xml-processor&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tacascer-org_xml-processor)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=tacascer-org_xml-processor&metric=coverage)](https://sonarcloud.io/summary/new_code?id=tacascer-org_xml-processor)

A set of utilities to process XML files.

## Flatten `include`

The `IncludeFlattener` class is designed to handle XML files that include other
XML files from different namespaces. It inlines all the included files into the
main XML file, regardless of their original namespaces.

### Usage

To use the `IncludeFlattener`, you need to create an instance of the class and
use one of the APIs according to your needs. Here is a basic example:

#### Flattening Includes from a String

Given the following XML string:

```xml

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:include schemaLocation="sample_1.xsd"/>
    <xs:element name="sample" type="xs:string"/>
</xs:schema>
```

```kotlin
val flattener = IncludeFlattener()
val result = flattener.apply(xmlString)
```

In this example, `result` will be a `String` that contains the XML content of
the processed string with all included files inlined. The `sample_1.xsd` file
should be in the same directory as your running application.

The input XML string includes another XML file `sample_1.xsd` which is located
in the same directory as your running application. The `IncludeFlattener` will
inline the content of `sample_1.xsd` into the main XML string.

Assuming `sample_1.xsd` contains:

```xml

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.different.com">
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

The expected output will be:

```xml

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:element name="sample" type="xs:string"/>
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

### Handling Different Namespaces

It's important to note that the final namespace of the processed XML file will
be that of the input file.

Here's an example:

Consider the following XML files:

`sampleDir/sample.xsd`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:include schemaLocation="sample_1.xsd"/>
    <xs:element name="sample" type="xs:string"/>
</xs:schema>
```

`sampleDir/sample_1.xsd`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.different.com">
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

When processed, the result will be:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:element name="sample" type="xs:string"/>
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

### Classpath Parsing

This feature allows you to include XML files that are located in the classpath
of your application.

#### Usage

To include an XML file from the classpath, you need to use the `classpath:`
prefix in the `schemaLocation` attribute of
the `xs:include` tag. Here is an example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:include schemaLocation="classpath:sample_1.xsd"/>
    <xs:element name="sample" type="xs:string"/>
</xs:schema>
```

In this example, `sample_1.xsd` is located in the classpath of the application.
The `IncludeFlattener` class will
correctly resolve this path and include the content of `sample_1.xsd` in the
processed XML file.

## Flatten `import`

Use the `ImportFlattener` class.

### Usage

Given the following XML string:

```xml

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:import namespace="http://www.different.com"
               schemaLocation="sample_1.xsd"/>
    <xs:element name="sample" type="xs:string"/>
</xs:schema>
```

Then use the `ImportFlattener` class like so

```kotlin
val flattener = ImportFlattener()
val result = flattener.apply(xmlString)
```

The result will be a `String` that contains the XML content of the processed
string with all imported
files inlined.

Assuming `sample_1.xsd` contains:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.different.com">
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

The expected output will be:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:element name="sample" type="xs:string"/>
    <xs:element name="sample_1" type="xs:string"/>
</xs:schema>
```

### Handling Different Namespaces

It's important to note that the final namespace of the processed XML file will
be that of the input file.

### Classpath Parsing

You can import XML files from the classpath of your application by using
the `classpath:` prefix in the `schemaLocation`

#### Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:import namespace="http://www.different.com"
               schemaLocation="classpath:sample_1.xsd"/>
    <xs:element name="sample" type="xs:string"/>
</xs:schema>
```

## Remove Namespaces

The `NamespaceRemover` class is designed to remove all namespaces from an XML
document. This includes defined namespaces
and namespace prefixes in attributes.

### Usage

To use the `NamespaceRemover`, you need to create an instance of the class and
call the appropriate method based on your
needs. Here is an example:

Assume the input file is

```xml

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:tns="http://www.sample.com"
           targetNamespace="http://www.sample.com">
    <xs:complexType name="complex">
        <xs:sequence>
            <xs:element name="simple" type="xs:string"/>
        </xs:sequence>
    </xs:complexType>
    <xs:element name="sample" type="xs:string"/>
    <xs:element name="complex" type="tns:complex"/>
</xs:schema>
```

When processed, the result will be:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:complexType name="complex">
        <xs:sequence>
            <xs:element name="simple" type="xs:string"/>
        </xs:sequence>
    </xs:complexType>
    <xs:element name="sample" type="xs:string"/>
    <xs:element name="complex" type="complex"/>
</xs:schema>
```

Notice that the `tns` namespace prefix has been removed from the `complex`
element type.

**NOTE** The `NamespaceRemover` class does not remove the `xs` namespace prefix
because it is a conventional namespace
prefix for XML Schema.

## Chaining Xml Filters

The `XmlFilterChain` class is designed to handle a chain of `XmlFilter`
instances and apply them in order to an XML
input. The filters are applied in the order they are added to the chain.

### Usage

To use the `XmlFilterChain`, you need to create an instance of the class, add
your filters, and then call the
appropriate method based on your needs.

#### Applying a Chain of Filters to a String

Here's an example of how to use `XmlFilterChain` with `IncludeFlattener`
and `NamespaceRemover` filters:

```kotlin
import io.github.tacascer.XmlFilterChain
import io.github.tacascer.flatten.IncludeFlattener
import io.github.tacascer.namespace.NamespaceRemover

val filterChain = XmlFilterChain(listOf(IncludeFlattener(), NamespaceRemover()))
val xmlString = """
    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.sample.com">
        <xs:include schemaLocation="sample_1.xsd"/>
        <xs:element name="sample" type="xs:string"/>
    </xs:schema>
"""
val result = filterChain.apply(xmlString)
```

In this example, `result` will be a `String` that contains the XML content of
the processed string with all included
files inlined and all namespaces removed.

Assuming `sample_1.xsd` contains:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:tns="http://www.different.com"
           targetNamespace="http://www.different.com">
    <xs:complexType name="complex">
        <xs:sequence>
            <xs:element name="simple" type="xs:string"/>
        </xs:sequence>
    </xs:complexType>
    <xs:element name="complex" type="tns:complex"/>
</xs:schema>
```

The expected output will be:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.sample.com">
    <xs:complexType name="complex">
        <xs:sequence>
            <xs:element name="simple" type="xs:string"/>
        </xs:sequence>
    </xs:complexType>
    <xs:element name="complex" type="complex"/>
    <xs:element name="sample" type="xs:string"/>
</xs:schema>
```

In the output, you can see that the `sample_1` element from the included
file `sample_1.xsd` has been inlined into the
main XML string and all namespaces have been removed.

**Note**: The `XmlFilterChain` is itself an `XmlFilter`, so you can chain
multiple `XmlFilterChain` instances together
