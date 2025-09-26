# WebTemplate

A comprehensive Kotlin library for converting OpenEHR templates to web-compatible formats, enabling seamless integration of healthcare data standards in web applications.

## Licence
[![License](https://img.shields.io/badge/license-apache%202.0-60C060.svg)](https://choosealicense.com/licenses/apache-2.0/)

## Release 
[![Release Artifacts](https://maven-badges.herokuapp.com/maven-central/care.better.platform/web-template/badge.svg)](https://search.maven.org/artifact/care.better.platform/web-template)

## CI/CD
[![CI](https://circleci.com/gh/wagnerjfr/circleci-travisci-codecov-maven-github.svg?style=shield)](https://app.circleci.com/pipelines/github/better-care/web-template)

## Test Coverage
[![codecov](https://codecov.io/gh/better-care/web-template/branch/master/graph/badge.svg?token=XAL78TEMAF)](https://codecov.io/gh/better-care/web-template)

## Overview

WebTemplate is a powerful library designed to work with OpenEHR (Open Electronic Health Record) templates, converting them between different formats suitable for web applications. It handles complex medical data structures and provides seamless conversion between flat key-value pairs, structured JSON objects, and raw OpenEHR objects.

### Key Features

- **Multi-format Conversion**: Convert between FLAT (key-value), STRUCTURED (JSON), and RAW (OpenEHR objects) formats
- **Template Building**: Build web-compatible templates from OpenEHR OPT (Operational Template) files
- **Medical Data Support**: Handle complex medical templates including vital signs, medication orders, clinical observations
- **Multi-language Support**: Support for multiple languages and territories in medical templates
- **Validation**: Built-in validation for medical data structures and constraints

## Core Concepts

### Data Formats

The library works with three main data formats:

1. **FLAT Format**: Simple key-value pairs ideal for form processing
   ```kotlin
   val flatData = mapOf(
       "vitals/vitals/body_temperature/any_event/temperature|magnitude" to "37.7",
       "vitals/vitals/body_temperature/any_event/temperature|unit" to "°C",
       "ctx/language" to "en",
       "ctx/territory" to "US"
   )
   ```

2. **STRUCTURED Format**: JSON-like hierarchical structure for API consumption
   ```json
   {
     "vitals": {
       "vitals": {
         "body_temperature": {
           "any_event": {
             "temperature": {
               "magnitude": 37.7,
               "unit": "°C"
             }
           }
         }
       }
     }
   }
   ```

3. **RAW Format**: Native OpenEHR objects for clinical processing
   ```kotlin
   val composition: Composition = // OpenEHR Composition object
   ```

### Architecture

- **WebTemplateBuilder**: Converts OpenEHR templates (.opt files) into WebTemplate objects
- **WebTemplate**: Main class providing conversion methods between different formats
- **Converters**: Specialized converters for each format transformation
- **Context**: Manages conversion settings like language, territory, and composer information

## Getting Started

### Maven Dependency

```xml
<dependency>
    <groupId>care.better.platform</groupId>
    <artifactId>web-template</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

#### 1. Building a WebTemplate from OpenEHR Template

```kotlin
import care.better.platform.web.template.WebTemplate
import care.better.platform.web.template.builder.WebTemplateBuilder
import care.better.platform.web.template.builder.context.WebTemplateBuilderContext

// Load an OpenEHR template (.opt file)
val template = loadTemplate("path/to/template.opt")

// Create builder context with language support
val builderContext = WebTemplateBuilderContext("en", setOf("en", "sl"))

// Build WebTemplate
val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(template, builderContext)
```

#### 2. Converting from FLAT to RAW Format

```kotlin
import care.better.platform.web.template.converter.raw.context.ConversionContext
import org.openehr.rm.composition.Composition

// Create conversion context
val context = ConversionContext.create()
    .withLanguage("en")
    .withTerritory("US")
    .withComposerName("Dr. Smith")
    .build()

// Flat data (typically from web forms)
val flatData = mapOf(
    "vitals/vitals/body_temperature/any_event/temperature|magnitude" to "37.7",
    "vitals/vitals/body_temperature/any_event/temperature|unit" to "°C",
    "vitals/context/setting|code" to "238",
    "vitals/context/setting|value" to "other care"
)

// Convert to OpenEHR Composition
val composition: Composition? = webTemplate.convertFromFlatToRaw(flatData, context)
```

#### 3. Converting from RAW to STRUCTURED Format

```kotlin
import care.better.platform.web.template.converter.FromRawConversion
import com.fasterxml.jackson.databind.JsonNode

// Convert OpenEHR object to structured JSON
val structuredData: JsonNode? = webTemplate.convertFromRawToStructured(
    composition, 
    FromRawConversion.create()
)
```

#### 4. Converting from RAW to FLAT Format

```kotlin
// Convert back to flat format
val flatResult: Map<String, Any> = webTemplate.convertFromRawToFlat(
    composition,
    FromRawConversion.create()
)
```

### Advanced Usage

#### Working with Medical Templates

```kotlin
// Example: Processing a medication order template
val medicationTemplate = loadTemplate("MED - Medication Order.opt")
val webTemplate = WebTemplateBuilder.buildNonNull(medicationTemplate, builderContext)

val medicationData = mapOf(
    "medication_order/medication_detail/medication_instruction/narrative" to "Take Aspirin as needed",
    "medication_order/medication_detail/medication_instruction/order/medicine" to "Aspirin",
    "medication_order/medication_detail/medication_instruction/order/timing" to "R3/2014-01-10T00:00:00.000+01:00",
    "ctx/language" to "en",
    "ctx/composer_name" to "Dr. Johnson"
)

val composition: Composition? = webTemplate.convertFromFlatToRaw(medicationData, context)
```

#### Finding WebTemplate Nodes

```kotlin
// Navigate to specific template nodes
val temperatureNode = webTemplate.findWebTemplateNode(
    "vitals/vitals/body_temperature/any_event/temperature"
)

// Check node properties
println("Node type: ${temperatureNode.rmType}")
println("Node path: ${temperatureNode.path}")
```

#### Formatted Output

```kotlin
// Get formatted string values
val formattedFlat: Map<String, String> = webTemplate.convertFormattedFromRawToFlat(
    composition,
    FromRawConversion.create()
)

// Get formatted structured data
val formattedStructured: JsonNode? = webTemplate.convertFormattedFromRawToStructured(
    composition,
    FromRawConversion.create()
)
```

## Common Use Cases

### 1. Web Form Processing
Convert form data to OpenEHR compositions:
```kotlin
// Receive form data as flat map
val formData = extractFormData(httpRequest)

// Convert to OpenEHR for storage
val composition = webTemplate.convertFromFlatToRaw(formData, context)
```

### 2. API Data Exchange
Convert OpenEHR data to JSON for APIs:
```kotlin
// Convert stored composition to JSON for API response
val jsonResponse = webTemplate.convertFromRawToStructured(composition)
```

### 3. Clinical Data Validation
Validate clinical data against templates:
```kotlin
// Template defines constraints and validation rules
val webTemplate = WebTemplateBuilder.buildNonNull(clinicalTemplate, context)

// Conversion will validate data against template constraints
val validatedComposition = webTemplate.convertFromFlatToRaw(patientData, context)
```

## Template Examples

The library supports various medical templates including:

- **Vital Signs**: Body temperature, blood pressure, heart rate
- **Laboratory Results**: Blood tests, urinalysis, microbiology
- **Medication Management**: Prescriptions, administration records
- **Clinical Observations**: Physical examinations, assessments
- **Care Plans**: Nursing care plans, treatment plans

## Error Handling

```kotlin
try {
    val composition = webTemplate.convertFromFlatToRaw(flatData, context)
    // Process successful conversion
} catch (e: ConversionException) {
    // Handle conversion errors
    logger.error("Conversion failed: ${e.message}")
}
```

## Testing

The library includes comprehensive tests demonstrating usage patterns:

```bash
# Run all tests
mvn test

# Run specific test categories
mvn test -Dtest=ClinicalTest
mvn test -Dtest=BuilderTest
```

## Performance Considerations

- **Template Caching**: Cache WebTemplate instances for repeated use
- **Context Reuse**: Reuse ConversionContext objects when possible
- **Batch Processing**: Process multiple conversions in batches for better performance

## Contributing

See our [CONTRIBUTING](/CONTRIBUTING.md) guide for details on contributing to this project.

## WASM and JavaScript Support

### Current Status
This library is implemented in Kotlin/JVM and primarily targets server-side healthcare applications. For browser-based applications, there are several potential approaches:

### Potential WASM Implementation
Components suitable for WASM compilation:
- Core conversion algorithms (FLAT ↔ RAW ↔ STRUCTURED)
- Template validation logic
- Data transformation utilities

### JavaScript Conversion Possibilities
The Kotlin codebase could potentially be converted to JavaScript using:
- **Kotlin/JS**: Direct compilation to JavaScript
- **Manual Port**: Rewrite core algorithms in TypeScript/JavaScript

### Browser Integration Scenarios
- Client-side form validation against OpenEHR templates
- Real-time data conversion in web applications
- Offline healthcare data processing

*Note: WASM and JavaScript implementations are not yet available but represent future enhancement opportunities.*
