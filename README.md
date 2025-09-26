# WebTemplate

A comprehensive Kotlin library for converting OpenEHR templates to web-compatible formats, enabling seamless integration of healthcare data standards in web applications.

* Licence [![License](https://img.shields.io/badge/license-apache%202.0-60C060.svg)](https://choosealicense.com/licenses/apache-2.0/)
* Release [![Release Artifacts](https://maven-badges.herokuapp.com/maven-central/care.better.platform/web-template/badge.svg)](https://search.maven.org/artifact/care.better.platform/web-template)
* CI/CD [![CI](https://circleci.com/gh/wagnerjfr/circleci-travisci-codecov-maven-github.svg?style=shield)](https://app.circleci.com/pipelines/github/better-care/web-template)
* Test Coverage [![codecov](https://codecov.io/gh/better-care/web-template/branch/master/graph/badge.svg?token=XAL78TEMAF)](https://codecov.io/gh/better-care/web-template)

## Overview

WebTemplate is a powerful library designed to work with OpenEHR (Open Electronic Health Record) templates, converting them between different formats suitable for web applications. It handles complex medical data structures and provides seamless conversion between flat key-value pairs, structured JSON objects, and raw OpenEHR objects.

Note: <a href="#v4.0">This 4.0 branch represents a significant evolution</a> when compared to the master branch

### Key Features

- **Multi-format Conversion**: Convert between FLAT (key-value), STRUCTURED (JSON), and RAW (OpenEHR objects) formats
- **Template Building**: Build web-compatible templates from OpenEHR OPT (Operational Template) files
- **Medical Data Support**: Handle complex medical templates including vital signs, medication orders, clinical observations
- **Multi-language Support**: Support for multiple languages and territories in medical templates
- **Validation**: Built-in validation for medical data structures and constraints
- **Modern Architecture**: Built on Java 17 and Kotlin 1.9.20 for optimal performance and maintainability

## Core Concepts

### Data Formats

The library works with three main data formats:

1. **FLAT Format**: Simple key-value pairs ideal for form processing
   ```kotlin
   val flatData = mapOf(
       "vitals/vitals/body_temperature/any_event/temperature|magnitude" to "37.7",
       "vitals/vitals/body_temperature/any_event/temperature|unit" to "°C",
       "vitals/vitals/body_temperature/any_event/temperature|formatting" to "##.#",
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
               "unit": "°C",
               "formatting": "##.#"
             }
           }
         }
       }
     }
   }
   ```

3. **RAW Format**: Native OpenEHR objects for clinical processing, see https://github.com/openEHR/specifications-ITS-JSON/blob/master/README.adoc
   ```kotlin
   val composition: Composition = // OpenEHR Composition object
   ```

### Architecture

- **WebTemplateBuilder**: Converts OpenEHR templates (.opt files) into WebTemplate objects with enhanced caching
- **WebTemplate**: Main class providing conversion methods between different formats
- **Converters**: Specialized converters for each format transformation with improved error handling
- **Context**: Manages conversion settings like language, territory, and composer information

## <a id="v4.0"></a>Major Changes from Github Master branch to 4.0

This 4.0 branch represents a significant evolution from the master branch, featuring:

### 🚀 **Modernized Platform**
- **Java 17 Support**: Full compatibility with Java 17 LTS, providing enhanced performance and security
- **Kotlin 1.9.20**: Updated to latest Kotlin version with improved compiler optimizations and language features
- **Enhanced Dependencies**: Updated to latest stable versions of Jackson (2.18.3), Commons libraries, and other core dependencies

### ✨ **New Features & Enhancements** 
- **Text Formatting Support**: New `|formatting` attribute support for DV_TEXT and DV_CODED_TEXT data types
- **GENERIC_ENTRY Conversion**: Added support for converting GENERIC_ENTRY OpenEHR compositions 
- **Composition Path Filtering**: Support for filtering compositions by specific paths for improved performance
- **Advanced Caching**: Optimized `NameAndNodeMatchingPathValueExtractor` with intelligent caching mechanisms
- **Null Safety Improvements**: Enhanced null handling and validation throughout the conversion pipeline

### 🔧 **Performance & Reliability**
- **Memory Optimization**: Improved memory management with Caffeine caching integration
- **Better Error Handling**: Enhanced exception handling and validation with more descriptive error messages
- **Feeder Audit Improvements**: Refined handling of `feeder_audit/originating_system_audit/time` in FLAT format
- **DV_CODED_TEXT Enhancements**: Fixed empty label handling and improved template-based terminology processing

### 📦 **Version History**
Current version: **4.2.1-SNAPSHOT** (evolved from 4.0.0 released 2024-02-15)

## Getting Started

### Maven Dependency

```xml
<dependency>
    <groupId>care.better.platform</groupId>
    <artifactId>web-template</artifactId>
    <version>4.2.1-SNAPSHOT</version>
</dependency>
```

### System Requirements

- **Java 17** or later (LTS recommended)
- **Kotlin 1.9.20** compatibility
- **Maven 3.6.2** or later for building

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

// Build WebTemplate with enhanced caching (4.0+ feature)
val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(template, builderContext)
```

#### 2. Converting from FLAT to RAW Format (Enhanced in 4.0)

```kotlin
import care.better.platform.web.template.converter.raw.context.ConversionContext
import org.openehr.rm.composition.Composition

// Create conversion context
val context = ConversionContext.create()
    .withLanguage("en")
    .withTerritory("US")
    .withComposerName("Dr. Smith")
    .build()

// Flat data with new formatting support (4.0+ feature)
val flatData = mapOf(
    "vitals/vitals/body_temperature/any_event/temperature|magnitude" to "37.7",
    "vitals/vitals/body_temperature/any_event/temperature|unit" to "°C",
    "vitals/vitals/body_temperature/any_event/temperature|formatting" to "##.# °C",
    "vitals/context/setting|code" to "238",
    "vitals/context/setting|value" to "other care"
)

// Convert to OpenEHR Composition with enhanced error handling
val composition: Composition? = webTemplate.convertFromFlatToRaw(flatData, context)
```

#### 3. Working with GENERIC_ENTRY (New in 4.0+)

```kotlin
// 4.0+ supports GENERIC_ENTRY conversion
val genericEntryData = mapOf(
    "generic_entry/data/item_tree/items[id1]/value|value" to "Custom clinical data",
    "ctx/language" to "en"
)

val genericComposition: Composition? = webTemplate.convertFromFlatToRaw(genericEntryData, context)
```

#### 4. Using Path Filtering (New in 4.0+)

```kotlin
// Filter composition by specific paths for performance optimization
val filteredComposition = webTemplate.filterCompositionByPath(
    composition, 
    listOf("vitals/vitals/body_temperature", "ctx/language")
)
```

### Advanced Usage

#### Enhanced Text Formatting (4.0+ Feature)

```kotlin
// New formatting attribute support for precise display control
val formattedData = mapOf(
    "observation/data/events/temperature|magnitude" to "37.123",
    "observation/data/events/temperature|unit" to "°C",
    "observation/data/events/temperature|formatting" to "##.# °C", // Formats as "37.1 °C"
    "observation/data/events/blood_pressure/systolic|magnitude" to "120",
    "observation/data/events/blood_pressure/systolic|formatting" to "### mmHg" // Formats as "120 mmHg"
)
```

#### Working with Medical Templates

```kotlin
// Example: Processing a medication order template with enhanced validation
val medicationTemplate = loadTemplate("MED - Medication Order v4.opt")
val webTemplate = WebTemplateBuilder.buildNonNull(medicationTemplate, builderContext)

val medicationData = mapOf(
    "medication_order/medication_detail/medication_instruction/narrative" to "Take Aspirin as needed",
    "medication_order/medication_detail/medication_instruction/order/medicine" to "Aspirin",
    "medication_order/medication_detail/medication_instruction/order/timing" to "R3/2014-01-10T00:00:00.000+01:00",
    "medication_order/medication_detail/medication_instruction/narrative|formatting" to "text-transform: capitalize",
    "ctx/language" to "en",
    "ctx/composer_name" to "Dr. Johnson"
)

// Enhanced conversion with better error reporting (4.0+ improvement)
val composition: Composition? = webTemplate.convertFromFlatToRaw(medicationData, context)
```

#### Performance Optimizations (4.0+ Enhancements)

```kotlin
// Cached template building for improved performance
val templateCache = mutableMapOf<String, WebTemplate>()

fun getCachedWebTemplate(templatePath: String): WebTemplate {
    return templateCache.getOrPut(templatePath) {
        val template = loadTemplate(templatePath)
        WebTemplateBuilder.buildNonNull(template, builderContext)
    }
}

// Optimized path-based data extraction
val pathExtractor = NameAndNodeMatchingPathValueExtractor() // Enhanced caching in 4.0+
val extractedValues = pathExtractor.extract(composition, targetPaths)
```

## Common Use Cases

### 1. Web Form Processing with Enhanced Validation
```kotlin
// Receive form data as flat map with new null safety improvements
val formData = extractFormData(httpRequest)

// Convert to OpenEHR with enhanced error handling (4.0+ improvement)
try {
    val composition = webTemplate.convertFromFlatToRaw(formData, context)
    // Process successful conversion
} catch (e: ConversionException) {
    // Enhanced error reporting with specific field information
    logger.error("Conversion failed for field: ${e.fieldPath}, reason: ${e.message}")
}
```

### 2. API Data Exchange with Formatting
```kotlin
// Convert stored composition to JSON with formatting preserved
val jsonResponse = webTemplate.convertFormattedFromRawToStructured(composition)
```

### 3. Clinical Data Validation with Modern Features
```kotlin
// Template defines constraints and validation rules with 4.0+ enhancements
val webTemplate = WebTemplateBuilder.buildNonNull(clinicalTemplate, context)

// Improved validation includes formatting and null safety checks
val validatedComposition = webTemplate.convertFromFlatToRaw(patientData, context)
```

## Template Examples

The library supports various medical templates including:

- **Vital Signs**: Body temperature, blood pressure, heart rate with formatting support
- **Laboratory Results**: Blood tests, urinalysis, microbiology with enhanced validation
- **Medication Management**: Prescriptions, administration records with GENERIC_ENTRY support
- **Clinical Observations**: Physical examinations, assessments with improved caching
- **Care Plans**: Nursing care plans, treatment plans with path filtering

## Error Handling (Enhanced in 4.0)

```kotlin
try {
    val composition = webTemplate.convertFromFlatToRaw(flatData, context)
    // Process successful conversion
} catch (e: ConversionException) {
    // Enhanced error handling with specific context
    when (e.type) {
        ConversionErrorType.VALIDATION_ERROR -> {
            logger.error("Validation failed: ${e.message} at path: ${e.path}")
        }
        ConversionErrorType.FORMATTING_ERROR -> {
            logger.error("Formatting error: ${e.message} for value: ${e.value}")
        }
        ConversionErrorType.NULL_VALUE_ERROR -> {
            logger.error("Unexpected null value: ${e.message}")
        }
    }
}
```

## Testing

The library includes comprehensive tests demonstrating usage patterns:

```bash
# Run all tests (requires Java 17+)
mvn test

# Run specific test categories
mvn test -Dtest=ClinicalTest
mvn test -Dtest=BuilderTest
mvn test -Dtest=FormattingTest  # New in 4.0+
```

## Performance Considerations (4.0+ Optimizations)

- **Template Caching**: Enhanced caching with Caffeine integration for better memory management
- **Context Reuse**: Improved context object reuse with optimized validation
- **Batch Processing**: Enhanced batch processing with path filtering for large datasets
- **Memory Management**: Better memory usage with Java 17 optimizations
- **Compilation Performance**: Kotlin 1.9.20 provides faster compilation and runtime performance

## Contributing

See our [CONTRIBUTING](/CONTRIBUTING.md) guide for details on contributing to this project.

## WASM and JavaScript Support

### Current Status
This library is implemented in Kotlin/JVM and primarily targets server-side healthcare applications. The 4.0 branch, with its modern Java 17 and Kotlin 1.9.20 foundation, provides enhanced opportunities for browser-based implementations.

### Enhanced WASM Implementation Potential (4.0+ Advantages)
Components particularly suitable for WASM compilation in the 4.0 codebase:
- **Optimized Conversion Algorithms**: FLAT ↔ RAW ↔ STRUCTURED with performance improvements
- **Enhanced Template Validation**: Advanced validation logic with better error reporting  
- **Formatting Engine**: New text formatting capabilities for precise display control
- **Caching Layer**: Intelligent caching system for improved performance

### JavaScript Conversion Possibilities (4.0+ Enhanced)
The modernized Kotlin 1.9.20 codebase offers improved JavaScript compilation options:
- **Kotlin/JS IR Backend**: Better JavaScript output with tree shaking and optimizations
- **Enhanced Type Safety**: Improved type inference and null safety for JavaScript interop
- **Modern Language Features**: Kotlin 1.9.20 features improve JavaScript compilation quality
- **Performance Optimizations**: Better runtime performance in JavaScript environments

### Browser Integration Scenarios
Enhanced by 4.0 improvements:
- **Client-side Form Validation**: Real-time validation with formatting preview
- **Offline Healthcare Data Processing**: Improved caching for offline scenarios  
- **Advanced Template Processing**: Enhanced template building and conversion
- **Progressive Web Apps**: Better performance for healthcare PWAs

### Performance Benefits for Browser Applications
4.0 branch advantages for web deployment:
- **Reduced Bundle Size**: Kotlin 1.9.20 IR backend produces smaller JavaScript
- **Better Memory Management**: Java 17 optimizations translate to improved browser performance
- **Enhanced Caching**: Caffeine-based caching strategies adaptable to browser storage
- **Improved Error Handling**: Better debugging experience in browser developer tools

*Note: While WASM and JavaScript implementations are not yet available, the 4.0 branch's modern architecture and performance optimizations make it an ideal foundation for future browser-based healthcare applications.*
