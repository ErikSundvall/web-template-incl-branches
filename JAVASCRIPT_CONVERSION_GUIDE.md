# JavaScript Conversion Guide

This document provides a comprehensive guide for converting the WebTemplate Kotlin library to JavaScript/TypeScript for browser-based healthcare applications.

## Overview

The WebTemplate library can be converted to JavaScript using several approaches:

1. **Kotlin/JS Compilation** - Direct compilation from Kotlin to JavaScript
2. **Manual TypeScript Port** - Rewriting core functionality in TypeScript
3. **Hybrid Approach** - Combining both methods for optimal results

## Demo Implementation

A working demonstration is available in the `kotlin-js-demo/` directory, showing:
- Core conversion functionality in Kotlin/JS
- Browser integration with HTML form
- Real-time data validation
- Interactive conversion between formats

Runnable from https://raw.githack.com/ErikSundvall/web-template-incl-branches/refs/heads/master/kotlin-js-demo/src/main/resources/index.html
...but note that this is mostly incomplete stubs with a hardcoded form.

## Kotlin/JS Compilation Approach

### Setup

The `kotlin-js-demo/build.gradle.kts` shows the required configuration:

```kotlin
plugins {
    kotlin("js") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        binaries.executable()
    }
}
```

### Core Classes Converted

#### WebTemplateJS
```kotlin
@JsExport
class WebTemplateJS(
    private val templateId: String,
    private val tree: WebTemplateNodeJS,
    private val defaultLanguage: String = "en"
) {
    fun convertFromFlatToStructured(
        flatData: Map<String, String>,
        context: ConversionContextJS = ConversionContextJS()
    ): String
    
    fun convertFromStructuredToFlat(structuredJson: String): Map<String, String>
    
    fun validateFlatData(flatData: Map<String, String>): ValidationResult
}
```

#### ConversionContextJS
```kotlin
@Serializable
data class ConversionContextJS(
    val language: String = "en",
    val territory: String = "US", 
    val composerName: String = "Web User"
)
```

#### ValidationResult
```kotlin
@Serializable
@JsExport
data class ValidationResult(
    val valid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)
```

### Key Features Implemented

1. **Data Format Conversion**
   - Flat key-value pairs to structured JSON
   - Structured JSON back to flat format
   - Proper handling of OpenEHR path conventions

2. **Medical Data Validation**
   - Temperature range validation
   - Unit validation
   - Language code validation
   - Extensible validation framework

3. **Browser Integration**
   - Direct JavaScript API
   - No server-side dependencies
   - Real-time validation feedback

## Manual TypeScript Port

For teams preferring pure TypeScript/JavaScript:

### Core Interface Definition

```typescript
interface WebTemplateConfig {
    language: string;
    territory: string;
    composerName: string;
}

interface ValidationResult {
    valid: boolean;
    errors: string[];
    warnings: string[];
}

class WebTemplateTS {
    constructor(
        private templateId: string,
        private tree: TemplateNode
    ) {}
    
    convertFromFlatToStructured(
        flatData: Record<string, any>,
        config: WebTemplateConfig
    ): any {
        // Implementation details
    }
    
    validateFlatData(flatData: Record<string, any>): ValidationResult {
        // Validation logic
    }
}
```

### Required Dependencies

```json
{
  "dependencies": {
    "lodash": "^4.17.21",
    "date-fns": "^2.29.3",
    "uuid": "^9.0.0",
    "ajv": "^8.12.0"
  },
  "devDependencies": {
    "@types/lodash": "^4.14.191",
    "typescript": "^4.9.5"
  }
}
```

## Browser Integration Examples

### React Component Integration

```jsx
import React, { useState, useEffect } from 'react';
import { WebTemplateJS } from './webtemplate-js';

const HealthDataForm = ({ template }) => {
    const [webTemplate, setWebTemplate] = useState(null);
    const [formData, setFormData] = useState({});
    const [validationResult, setValidationResult] = useState(null);
    
    useEffect(() => {
        // Initialize WebTemplate from template data
        const wt = WebTemplateJS.fromJsonTemplate(template);
        setWebTemplate(wt);
    }, [template]);
    
    const handleFormChange = (field, value) => {
        const newFormData = { ...formData, [field]: value };
        setFormData(newFormData);
        
        // Real-time validation
        if (webTemplate) {
            const validation = webTemplate.validateFlatData(newFormData);
            setValidationResult(validation);
        }
    };
    
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (webTemplate && validationResult.valid) {
            const structuredData = webTemplate.convertFromFlatToStructured(
                formData,
                { language: 'en', territory: 'US', composerName: 'Web User' }
            );
            
            // Send to server or process locally
            await submitHealthData(structuredData);
        }
    };
    
    return (
        <form onSubmit={handleSubmit}>
            <input
                type="number"
                placeholder="Temperature"
                onChange={(e) => handleFormChange('vitals/temperature|magnitude', e.target.value)}
            />
            
            <select onChange={(e) => handleFormChange('vitals/temperature|unit', e.target.value)}>
                <option value="°C">Celsius</option>
                <option value="°F">Fahrenheit</option>
            </select>
            
            {validationResult && !validationResult.valid && (
                <div className="validation-errors">
                    {validationResult.errors.map((error, index) => (
                        <div key={index} className="error">{error}</div>
                    ))}
                </div>
            )}
            
            <button type="submit" disabled={!validationResult?.valid}>
                Submit Health Data
            </button>
        </form>
    );
};
```

### Vue.js Integration

```vue
<template>
  <form @submit.prevent="submitForm">
    <div class="form-group">
      <label>Body Temperature:</label>
      <input
        v-model="formData.temperature"
        type="number"
        step="0.1"
        @input="validateData"
      />
    </div>
    
    <div class="form-group">
      <label>Unit:</label>
      <select v-model="formData.unit" @change="validateData">
        <option value="°C">Celsius</option>
        <option value="°F">Fahrenheit</option>
      </select>
    </div>
    
    <div v-if="validation.errors.length" class="errors">
      <div v-for="error in validation.errors" :key="error">
        {{ error }}
      </div>
    </div>
    
    <button type="submit" :disabled="!validation.valid">
      Convert Data
    </button>
  </form>
</template>

<script>
import { WebTemplateJS } from './webtemplate-js';

export default {
  props: ['template'],
  data() {
    return {
      webTemplate: null,
      formData: {
        temperature: '',
        unit: '°C'
      },
      validation: { valid: true, errors: [], warnings: [] }
    };
  },
  
  mounted() {
    this.webTemplate = WebTemplateJS.fromJsonTemplate(this.template);
  },
  
  methods: {
    validateData() {
      if (this.webTemplate) {
        const flatData = {
          'vitals/temperature|magnitude': this.formData.temperature,
          'vitals/temperature|unit': this.formData.unit
        };
        
        this.validation = this.webTemplate.validateFlatData(flatData);
      }
    },
    
    submitForm() {
      if (this.validation.valid) {
        const flatData = {
          'vitals/temperature|magnitude': this.formData.temperature,
          'vitals/temperature|unit': this.formData.unit
        };
        
        const structured = this.webTemplate.convertFromFlatToStructured(flatData);
        this.$emit('data-converted', structured);
      }
    }
  }
};
</script>
```

### Web Workers for Performance

```javascript
// web-worker.js
importScripts('./webtemplate-js.js');

let webTemplate = null;

self.onmessage = function(e) {
    const { type, data } = e.data;
    
    switch (type) {
        case 'initialize':
            webTemplate = WebTemplateJS.fromJsonTemplate(data.template);
            self.postMessage({ type: 'ready' });
            break;
            
        case 'convert':
            if (webTemplate) {
                const result = webTemplate.convertFromFlatToStructured(
                    data.flatData,
                    data.context
                );
                self.postMessage({ type: 'result', data: result });
            }
            break;
            
        case 'validate':
            if (webTemplate) {
                const validation = webTemplate.validateFlatData(data.flatData);
                self.postMessage({ type: 'validation', data: validation });
            }
            break;
    }
};

// main.js - Using the worker
const worker = new Worker('./web-worker.js');

worker.onmessage = function(e) {
    const { type, data } = e.data;
    
    switch (type) {
        case 'ready':
            console.log('WebTemplate worker ready');
            break;
            
        case 'result':
            handleConversionResult(data);
            break;
            
        case 'validation':
            handleValidationResult(data);
            break;
    }
};

// Initialize worker with template
worker.postMessage({
    type: 'initialize',
    data: { template: templateJson }
});
```

## Performance Considerations

### Bundle Size Optimization

```javascript
// webpack.config.js
module.exports = {
    optimization: {
        splitChunks: {
            chunks: 'all',
            cacheGroups: {
                webtemplate: {
                    test: /[\\/]webtemplate[\\/]/,
                    name: 'webtemplate',
                    chunks: 'all',
                }
            }
        }
    }
};
```

### Lazy Loading

```javascript
// Lazy load WebTemplate for better initial page load
async function loadWebTemplate() {
    const { WebTemplateJS } = await import('./webtemplate-js');
    return WebTemplateJS;
}

// Use in component
const initializeWebTemplate = async () => {
    const WebTemplateJS = await loadWebTemplate();
    return WebTemplateJS.fromJsonTemplate(templateData);
};
```

### Caching Strategies

```javascript
// Service Worker caching for templates
const CACHE_NAME = 'webtemplate-v1';
const TEMPLATE_CACHE = 'templates-v1';

self.addEventListener('install', event => {
    event.waitUntil(
        Promise.all([
            caches.open(CACHE_NAME).then(cache => 
                cache.addAll(['/webtemplate-js.js'])
            ),
            caches.open(TEMPLATE_CACHE).then(cache => 
                cache.addAll(['/templates/'])
            )
        ])
    );
});

// Template caching in application
class TemplateCache {
    constructor() {
        this.cache = new Map();
    }
    
    async getWebTemplate(templateId) {
        if (this.cache.has(templateId)) {
            return this.cache.get(templateId);
        }
        
        const template = await this.loadTemplate(templateId);
        const webTemplate = WebTemplateJS.fromJsonTemplate(template);
        this.cache.set(templateId, webTemplate);
        
        return webTemplate;
    }
}
```

## Testing Strategies

### Unit Tests

```javascript
// webtemplate.test.js
import { WebTemplateJS, ValidationResult } from './webtemplate-js';

describe('WebTemplateJS', () => {
    let webTemplate;
    
    beforeEach(() => {
        webTemplate = WebTemplateJS.createDemoTemplate();
    });
    
    test('converts flat to structured format', () => {
        const flatData = {
            'vitals/temperature|magnitude': '37.5',
            'vitals/temperature|unit': '°C'
        };
        
        const result = webTemplate.convertFromFlatToStructured(flatData);
        const parsed = JSON.parse(result);
        
        expect(parsed.vitals.temperature.magnitude).toBe(37.5);
        expect(parsed.vitals.temperature.unit).toBe('°C');
    });
    
    test('validates temperature data', () => {
        const invalidData = {
            'vitals/temperature|magnitude': 'not-a-number',
            'vitals/temperature|unit': 'invalid-unit'
        };
        
        const validation = webTemplate.validateFlatData(invalidData);
        
        expect(validation.valid).toBe(false);
        expect(validation.errors).toHaveLength(2);
    });
});
```

### Integration Tests

```javascript
// integration.test.js
import { render, fireEvent, waitFor } from '@testing-library/react';
import HealthDataForm from './HealthDataForm';

test('form validates and converts data', async () => {
    const onDataConverted = jest.fn();
    const { getByPlaceholderText, getByText } = render(
        <HealthDataForm onDataConverted={onDataConverted} />
    );
    
    const temperatureInput = getByPlaceholderText('Temperature');
    fireEvent.change(temperatureInput, { target: { value: '37.5' } });
    
    const submitButton = getByText('Submit Health Data');
    fireEvent.click(submitButton);
    
    await waitFor(() => {
        expect(onDataConverted).toHaveBeenCalledWith(
            expect.objectContaining({
                vitals: expect.objectContaining({
                    temperature: expect.objectContaining({
                        magnitude: 37.5,
                        unit: '°C'
                    })
                })
            })
        );
    });
});
```

## Deployment Guide

### NPM Package

```json
{
  "name": "@better-care/webtemplate-js",
  "version": "1.0.0",
  "description": "JavaScript implementation of WebTemplate for OpenEHR",
  "main": "dist/webtemplate.js",
  "module": "dist/webtemplate.esm.js",
  "types": "dist/webtemplate.d.ts",
  "files": [
    "dist/"
  ],
  "scripts": {
    "build": "rollup -c",
    "test": "jest",
    "prepublishOnly": "npm run build && npm test"
  }
}
```

### CDN Distribution

```html
<!-- Direct CDN usage -->
<script src="https://cdn.jsdelivr.net/npm/@better-care/webtemplate-js@1.0.0/dist/webtemplate.min.js"></script>

<script>
    const webTemplate = WebTemplateJS.createDemoTemplate();
    
    const flatData = {
        'vitals/temperature|magnitude': '37.0',
        'vitals/temperature|unit': '°C'
    };
    
    const structured = webTemplate.convertFromFlatToStructured(flatData);
    console.log('Converted data:', structured);
</script>
```

## Migration Strategy

### Phase 1: Proof of Concept (2-3 weeks)
1. Set up Kotlin/JS build configuration and add "manual" reimplementation if needed for any parts.
2. Port or reimplement core conversion classes. Note:
** Use pure vanilla javascript without any frameworks (so no React or NPM etc) but annontated with jsdoc.
** It is ok to use well established javascript libraries under permissive open source licenses such as MIT or Apache 2, if they are available and can be imported from CDN and used in web pages (thus not requiring NPM setup etc)
4. Create usage documentation for the Javascript port in a separate .md page explaining how to call the different functions (inspired by the documentation for the Kotlin version of this project)
5. Create basic browser demo that can take any openEHR instance data and a template file and converte to/from all of:
** Flat
** Structured
** Raw (canonical openEHR JSON)  

### Phase 2: Core Implementation (4-6 weeks)
1. Implement full conversion functionality
2. Add comprehensive validation
3. Create React/Vue component examples
4. Set up automated testing

### Phase 3: Production Ready (4-6 weeks)
1. Performance optimization
2. Browser compatibility testing
3. Documentation and examples
4. NPM package preparation

### Phase 4: Advanced Features (2-4 weeks)
1. Web Worker integration
2. Service Worker caching
3. Advanced validation rules
4. Framework-specific optimizations

## Conclusion

Converting WebTemplate to JavaScript opens up numerous possibilities for healthcare web applications:

- **Client-side Processing**: Reduce server load and improve responsiveness
- **Offline Capabilities**: Process healthcare data without internet connectivity
- **Real-time Validation**: Provide immediate feedback to healthcare providers
- **Mobile Optimization**: Better performance on mobile devices
- **Regulatory Compliance**: Keep sensitive data processing client-side

The combination of Kotlin/JS compilation and manual TypeScript implementation provides the best balance of development speed, type safety, and performance for healthcare applications requiring OpenEHR template processing in the browser.
