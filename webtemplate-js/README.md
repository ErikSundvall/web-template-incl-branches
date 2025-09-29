# WebTemplate JavaScript Implementation

A vanilla JavaScript implementation of the WebTemplate functionality for converting between flat and structured healthcare data formats, with full openEHR canonical JSON support.

## Overview

This implementation provides a framework-free, browser-ready solution for healthcare data conversion without requiring NPM, bundlers, or external dependencies. It follows the Phase 1 specifications from the JavaScript Conversion Guide.

## Features

- **Zero Dependencies**: Pure vanilla JavaScript with no external libraries
- **Multiple Format Support**: Converts between Flat, Structured, and Raw openEHR JSON formats
- **Real-time Validation**: Comprehensive healthcare data validation with errors and warnings
- **Browser Ready**: Works directly in browsers via script tag or ES modules
- **JSDoc Annotations**: Full documentation for developer ergonomics
- **TypeScript Compatible**: Includes type definitions via JSDoc

## Quick Start

### Browser Usage (Script Tag)

```html
<!DOCTYPE html>
<html>
<head>
    <title>WebTemplate Demo</title>
</head>
<body>
    <script src="webtemplate.js"></script>
    <script>
        // Create a demo template
        const webTemplate = WebTemplateJS.createDemoTemplate();
        
        // Sample flat data (form input)
        const flatData = {
            'vitals/temperature|magnitude': '37.5',
            'vitals/temperature|unit': '°C',
            'ctx/language': 'en',
            'ctx/composer_name': 'Dr. Smith'
        };
        
        // Convert to structured JSON
        const structured = webTemplate.convertFromFlatToStructured(flatData);
        console.log('Structured:', structured);
        
        // Convert to raw openEHR JSON
        const rawOpenEHR = webTemplate.convertToRawOpenEHR(flatData);
        console.log('Raw openEHR:', rawOpenEHR);
        
        // Validate data
        const validation = webTemplate.validateFlatData(flatData);
        console.log('Valid:', validation.valid);
    </script>
</body>
</html>
```

### ES Module Usage

```javascript
import WebTemplateJS from './webtemplate.js';

const webTemplate = WebTemplateJS.createDemoTemplate();
// ... rest of the code
```

### Node.js Usage

```javascript
const WebTemplateJS = require('./webtemplate.js');

const webTemplate = WebTemplateJS.createDemoTemplate();
// ... rest of the code
```

## Core API Reference

### WebTemplateJS Class

#### Constructor

```javascript
new WebTemplateJS(templateId, tree, defaultLanguage)
```

- `templateId` (string): Unique identifier for the template
- `tree` (WebTemplateNode): Template node hierarchy
- `defaultLanguage` (string): Default language code (default: 'en')

#### Static Methods

##### `WebTemplateJS.createDemoTemplate()`

Creates a demo template for testing purposes.

**Returns:** `WebTemplateJS` instance

**Example:**
```javascript
const webTemplate = WebTemplateJS.createDemoTemplate();
```

##### `WebTemplateJS.fromJsonTemplate(templateJson)`

Creates a WebTemplateJS instance from a JSON template definition.

**Parameters:**
- `templateJson` (string|Object): JSON template definition

**Returns:** `WebTemplateJS` instance

**Example:**
```javascript
const templateJson = {
    "templateId": "vital_signs_v1",
    "defaultLanguage": "en",
    "tree": {
        "jsonId": "vital_signs_encounter",
        "name": "Vital Signs Encounter",
        "rmType": "COMPOSITION",
        "path": "/",
        "children": [...]
    }
};

const webTemplate = WebTemplateJS.fromJsonTemplate(templateJson);
```

#### Instance Methods

##### `convertFromFlatToStructured(flatData, context)`

Converts flat key-value data to structured JSON format.

**Parameters:**
- `flatData` (Record<string, any>): Flat key-value pairs
- `context` (ConversionContext, optional): Conversion context

**Returns:** `string` - Structured JSON string

**Example:**
```javascript
const flatData = {
    'vitals/temperature|magnitude': '37.5',
    'vitals/temperature|unit': '°C',
    'ctx/language': 'en'
};

const context = {
    language: 'en',
    territory: 'US',
    composerName: 'Dr. Smith'
};

const structured = webTemplate.convertFromFlatToStructured(flatData, context);
```

##### `convertFromStructuredToFlat(structuredJson)`

Converts structured JSON back to flat format.

**Parameters:**
- `structuredJson` (string|Object): Structured JSON as string or object

**Returns:** `Record<string, string>` - Flat key-value pairs

**Example:**
```javascript
const structured = {
    "vitals": {
        "temperature": {
            "magnitude": 37.5,
            "unit": "°C"
        }
    }
};

const flat = webTemplate.convertFromStructuredToFlat(structured);
// Result: {'vitals/temperature|magnitude': '37.5', 'vitals/temperature|unit': '°C'}
```

##### `validateFlatData(flatData)`

Validates flat data against template constraints.

**Parameters:**
- `flatData` (Record<string, any>): Flat key-value pairs to validate

**Returns:** `ValidationResult` - Validation result with errors and warnings

**Example:**
```javascript
const validation = webTemplate.validateFlatData({
    'vitals/temperature|magnitude': '37.5',
    'vitals/temperature|unit': '°C'
});

if (!validation.valid) {
    console.log('Errors:', validation.errors);
    console.log('Warnings:', validation.warnings);
}
```

##### `convertToRawOpenEHR(flatData, context)`

Converts flat data to raw openEHR canonical JSON format.

**Parameters:**
- `flatData` (Record<string, any>): Flat data to convert
- `context` (ConversionContext, optional): Conversion context

**Returns:** `string` - Raw openEHR JSON string

**Example:**
```javascript
const rawOpenEHR = webTemplate.convertToRawOpenEHR(flatData, {
    language: 'en',
    territory: 'US',
    composerName: 'Dr. Smith'
});
```

##### `findNode(path)`

Finds template node by path.

**Parameters:**
- `path` (string): Path to search for

**Returns:** `WebTemplateNode|null` - Found node or null

## Data Format Examples

### Flat Format (Form Data)
```
vitals/temperature|magnitude = 37.5
vitals/temperature|unit = °C
ctx/language = en
ctx/setting = emergency care
ctx/composer_name = Dr. Johnson
```

### Structured JSON Format
```json
{
  "vitals": {
    "temperature": {
      "magnitude": 37.5,
      "unit": "°C"
    }
  },
  "_context": {
    "language": "en",
    "setting": "emergency care",
    "composer_name": "Dr. Johnson"
  }
}
```

### Raw openEHR Canonical JSON
```json
{
  "@type": "COMPOSITION",
  "archetype_node_id": "demo-vitals-v1",
  "name": {
    "@type": "DV_TEXT",
    "value": "Health Data"
  },
  "language": {
    "@type": "CODE_PHRASE",
    "terminology_id": {
      "@type": "TERMINOLOGY_ID",
      "value": "ISO_639-1"
    },
    "code_string": "en"
  },
  "territory": {
    "@type": "CODE_PHRASE",
    "terminology_id": {
      "@type": "TERMINOLOGY_ID",
      "value": "ISO_3166-1"
    },
    "code_string": "US"
  },
  "composer": {
    "@type": "PARTY_IDENTIFIED",
    "name": "Dr. Johnson"
  },
  "content": [...]
}
```

## Type Definitions

### ConversionContext
```typescript
interface ConversionContext {
    language: string;      // Language code (e.g., 'en', 'sl', 'de')
    territory: string;     // Territory code (e.g., 'US', 'SI', 'DE')
    composerName: string;  // Name of the person creating the data
}
```

### ValidationResult
```typescript
interface ValidationResult {
    valid: boolean;        // Whether the data is valid
    errors: string[];      // Array of error messages
    warnings: string[];    // Array of warning messages
}
```

### WebTemplateNode
```typescript
interface WebTemplateNode {
    jsonId: string;            // JSON identifier for the node
    name: string;              // Human-readable name
    rmType: string;            // openEHR reference model type
    path: string;              // Path within the template
    children: WebTemplateNode[]; // Child nodes
}
```

## Validation Rules

The implementation includes built-in validation for common healthcare data:

- **Temperature Values**: Must be numeric, warns if outside 30-45°C range
- **Temperature Units**: Must be °C, °F, or K
- **Language Codes**: Should be 2-character ISO codes
- **Required Fields**: Validates presence of mandatory data

### Custom Validation

You can extend validation by subclassing WebTemplateJS:

```javascript
class CustomWebTemplate extends WebTemplateJS {
    validateFlatData(flatData) {
        const result = super.validateFlatData(flatData);
        
        // Add custom validation rules
        Object.entries(flatData).forEach(([path, value]) => {
            if (path.includes('blood_pressure|systolic')) {
                const systolic = parseFloat(value);
                if (systolic > 180) {
                    result.warnings.push('High blood pressure detected');
                }
            }
        });
        
        return result;
    }
}
```

## Error Handling

All methods include comprehensive error handling:

```javascript
try {
    const result = webTemplate.convertFromFlatToStructured(flatData);
    console.log('Success:', result);
} catch (error) {
    console.error('Conversion failed:', error.message);
}
```

## Browser Compatibility

- **Modern Browsers**: Chrome 60+, Firefox 55+, Safari 12+, Edge 79+
- **IE Support**: Not supported (uses ES6+ features)
- **Mobile**: iOS Safari 12+, Chrome Mobile 60+

## Performance Considerations

- **Memory Usage**: Minimal overhead, no external dependencies
- **Processing Speed**: Optimized for real-time form validation
- **File Size**: ~17KB uncompressed, ~5KB gzipped

## Security Notes

- **Input Sanitization**: All user inputs are properly sanitized
- **XSS Prevention**: No innerHTML usage, safe DOM manipulation
- **Data Privacy**: All processing happens client-side

## License

This implementation follows the same license as the parent WebTemplate project.

## Contributing

See the main project's CONTRIBUTING.md for guidelines on contributing to this JavaScript implementation.