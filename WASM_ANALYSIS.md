# WebTemplate WASM & JavaScript Analysis

## Executive Summary

This document analyzes the feasibility of compiling the WebTemplate Kotlin library to WebAssembly (WASM) and/or converting it to pure JavaScript for browser-based healthcare applications.

## 1. WASM Compilation Analysis

### 1.1 Suitable Components for WASM

Based on the codebase analysis, the following components are well-suited for WASM compilation:

#### Core Conversion Logic
- **Flat ↔ Raw ↔ Structured Converters**
  - `AbstractRawToFlatConverter` and implementations
  - `FlatToStructuredConverter`
  - `RawToStructuredConverter`
  - `StructuredToRawConverter`

#### Data Transformation Utilities
- **Value Mappers**: All `*ToFlatMapper` classes in `converter/flat/mapper/`
- **Path Utilities**: `WebTemplatePath` and related path handling
- **Data Type Handlers**: `DvCount`, `DvBoolean`, `DvProportion` factories

#### Template Processing
- **WebTemplate Core**: Main conversion methods
- **Node Navigation**: `findWebTemplateNode` and path resolution
- **Validation Logic**: Built-in constraint validation

### 1.2 Components Requiring Adaptation

#### Dependencies on JVM Libraries
- **Jackson JSON Processing**: Needs WASM-compatible JSON library
- **JAXB XML Processing**: Requires alternative XML handling
- **OpenEHR RM Objects**: Core reference model classes
- **Joda Time**: Needs JavaScript Date/time equivalent

#### File System Dependencies
- Template loading from `.opt` files would need browser-compatible alternatives

### 1.3 WASM Compilation Approach

#### Option 1: Kotlin/Native to WASM
```kotlin
// Kotlin/Native configuration for WASM target
kotlin {
    wasm32 {
        binaries {
            executable {
                entryPoint = "care.better.platform.web.template.wasm.main"
            }
        }
    }
}
```

#### Option 2: Kotlin/JS with WASM Backend
```kotlin
// Kotlin/JS targeting WASM
kotlin {
    js(IR) {
        browser {
            webpackTask {
                output.libraryType = "umd"
            }
        }
        binaries.executable()
    }
}
```

### 1.4 WASM Module Interface Design

```javascript
// Proposed WASM module interface
class WebTemplateWasm {
    constructor(wasmModule) {
        this.module = wasmModule;
    }
    
    // Convert flat data to structured format
    convertFlatToStructured(flatData, templateData, context) {
        const flatPtr = this.module.allocateString(JSON.stringify(flatData));
        const templatePtr = this.module.allocateString(templateData);
        const contextPtr = this.module.allocateString(JSON.stringify(context));
        
        const resultsPtr = this.module.convertFlatToStructured(flatPtr, templatePtr, contextPtr);
        return JSON.parse(this.module.getString(resultsPtr));
    }
    
    // Convert structured to flat format
    convertStructuredToFlat(structuredData, templateData, context) {
        // Similar implementation
    }
    
    // Validate data against template
    validateData(data, templateData) {
        // Template validation logic
    }
}
```

## 2. Pure JavaScript Conversion Analysis

### 2.1 Kotlin/JS Compilation

#### Advantages
- **Direct Compilation**: Kotlin/JS can compile most of the codebase directly
- **Type Safety**: Maintains Kotlin's type system
- **Interoperability**: Easy integration with existing JavaScript applications

#### Current Kotlin/JS Configuration
```kotlin
// build.gradle.kts
kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        nodejs()
        binaries.executable()
    }
}
```

### 2.2 Manual JavaScript/TypeScript Port

#### Core Conversion Logic Port
```typescript
// TypeScript equivalent of WebTemplate conversion
interface WebTemplateConfig {
    language: string;
    territory: string;
    composerName: string;
}

class WebTemplateJS {
    constructor(private templateData: any) {}
    
    convertFromFlatToStructured(
        flatData: Record<string, any>, 
        config: WebTemplateConfig
    ): any {
        // Port of conversion logic
        return this.processConversion(flatData, config);
    }
    
    private processConversion(data: any, config: WebTemplateConfig): any {
        // Implementation of core conversion algorithms
    }
}
```

#### Required JavaScript Libraries
```json
{
  "dependencies": {
    "lodash": "^4.17.21",
    "date-fns": "^2.29.3",
    "uuid": "^9.0.0",
    "ajv": "^8.12.0"
  }
}
```

### 2.3 Hybrid Approach

Combine WASM for performance-critical operations with JavaScript for UI integration:

```javascript
// Hybrid WASM + JavaScript implementation
class HybridWebTemplate {
    constructor() {
        this.wasmModule = null;
        this.jsImplementation = new WebTemplateJS();
    }
    
    async initialize() {
        try {
            this.wasmModule = await loadWebTemplateWasm();
        } catch (error) {
            console.warn('WASM fallback to JavaScript:', error);
        }
    }
    
    convertFlatToStructured(data, template, config) {
        if (this.wasmModule) {
            return this.wasmModule.convertFlatToStructured(data, template, config);
        } else {
            return this.jsImplementation.convertFlatToStructured(data, template, config);
        }
    }
}
```

## 3. Browser Integration Examples

### 3.1 Web Worker Implementation

```javascript
// web-worker.js - Offload processing to prevent UI blocking
import { WebTemplateWasm } from './webtemplate-wasm.js';

let webTemplate = null;

self.onmessage = function(e) {
    const { type, data, template, config } = e.data;
    
    switch (type) {
        case 'init':
            webTemplate = new WebTemplateWasm();
            self.postMessage({ type: 'ready' });
            break;
            
        case 'convert':
            const result = webTemplate.convertFlatToStructured(data, template, config);
            self.postMessage({ type: 'result', data: result });
            break;
    }
};
```

### 3.2 React Integration Example

```jsx
// React component using WebTemplate
import React, { useState, useEffect } from 'react';
import { WebTemplateWasm } from './webtemplate-wasm';

const HealthDataForm = ({ template }) => {
    const [webTemplate, setWebTemplate] = useState(null);
    const [formData, setFormData] = useState({});
    const [structuredData, setStructuredData] = useState(null);
    
    useEffect(() => {
        const initializeWebTemplate = async () => {
            const wt = new WebTemplateWasm();
            await wt.initialize();
            setWebTemplate(wt);
        };
        
        initializeWebTemplate();
    }, []);
    
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (webTemplate) {
            const config = {
                language: 'en',
                territory: 'US',
                composerName: 'Web User'
            };
            
            const result = await webTemplate.convertFlatToStructured(
                formData, 
                template, 
                config
            );
            
            setStructuredData(result);
        }
    };
    
    return (
        <form onSubmit={handleSubmit}>
            {/* Form fields based on template */}
            <input 
                name="temperature" 
                value={formData.temperature || ''} 
                onChange={(e) => setFormData({
                    ...formData, 
                    'vitals/temperature|magnitude': e.target.value
                })}
            />
            <button type="submit">Convert Data</button>
            
            {structuredData && (
                <pre>{JSON.stringify(structuredData, null, 2)}</pre>
            )}
        </form>
    );
};
```

### 3.3 Service Worker for Offline Processing

```javascript
// service-worker.js - Enable offline healthcare data processing
const CACHE_NAME = 'webtemplate-v1';
const TEMPLATES_CACHE = 'templates-v1';

self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll([
                '/webtemplate-wasm.wasm',
                '/webtemplate-wasm.js',
                '/templates/'
            ]))
    );
});

self.addEventListener('message', event => {
    if (event.data && event.data.type === 'PROCESS_HEALTH_DATA') {
        const { data, templateId } = event.data;
        
        // Process health data offline using cached WASM module
        processHealthDataOffline(data, templateId)
            .then(result => {
                event.ports[0].postMessage({ success: true, data: result });
            })
            .catch(error => {
                event.ports[0].postMessage({ success: false, error: error.message });
            });
    }
});
```

## 4. Performance Comparison

### 4.1 Expected Performance Characteristics

| Approach | Compilation Time | Runtime Performance | Bundle Size | Browser Support |
|----------|------------------|-------------------|-------------|-----------------|
| **Kotlin/JS** | Fast | Good | Medium | Excellent |
| **WASM (Kotlin/Native)** | Slow | Excellent | Small | Good (Modern) |
| **Manual JS/TS** | N/A | Good | Small | Excellent |
| **Hybrid** | Medium | Excellent | Medium | Good |

### 4.2 Benchmarking Plan

```javascript
// Performance testing framework
async function benchmarkWebTemplate() {
    const testData = generateTestData();
    const template = loadTestTemplate();
    
    const approaches = [
        { name: 'Kotlin/JS', impl: kotlinJsImpl },
        { name: 'WASM', impl: wasmImpl },
        { name: 'Pure JS', impl: pureJsImpl }
    ];
    
    for (const approach of approaches) {
        const start = performance.now();
        
        for (let i = 0; i < 1000; i++) {
            await approach.impl.convertFlatToStructured(testData, template);
        }
        
        const end = performance.now();
        console.log(`${approach.name}: ${end - start}ms`);
    }
}
```

## 5. Implementation Roadmap

### Phase 1: Proof of Concept (4-6 weeks)
- [ ] Set up Kotlin/JS build configuration
- [ ] Port core conversion algorithms
- [ ] Create minimal browser demo
- [ ] Basic performance testing

### Phase 2: WASM Implementation (6-8 weeks)
- [ ] Research Kotlin/Native WASM compilation
- [ ] Implement WASM module with C-style API
- [ ] Create JavaScript bindings
- [ ] Performance optimization

### Phase 3: Production Ready (8-10 weeks)
- [ ] Complete test coverage
- [ ] Error handling and validation
- [ ] Documentation and examples
- [ ] NPM package distribution

### Phase 4: Advanced Features (4-6 weeks)
- [ ] Web Worker integration
- [ ] Service Worker offline support
- [ ] React/Vue.js component library
- [ ] Performance monitoring

## 6. Browser Compatibility

### Minimum Requirements
- **WASM Support**: Chrome 57+, Firefox 52+, Safari 11+, Edge 16+
- **ES6 Modules**: Chrome 61+, Firefox 60+, Safari 11+, Edge 16+
- **Web Workers**: All modern browsers
- **Service Workers**: Chrome 40+, Firefox 44+, Safari 11.1+, Edge 17+

### Polyfill Strategy
```javascript
// Feature detection and polyfills
async function initializeWebTemplate() {
    const hasWasm = typeof WebAssembly === 'object';
    const hasWorkers = typeof Worker !== 'undefined';
    
    if (hasWasm) {
        return await loadWebTemplateWasm();
    } else {
        console.warn('WASM not supported, falling back to JavaScript');
        return new WebTemplateJS();
    }
}
```

## 7. Conclusion

### Recommended Approach

**Hybrid Implementation** combining:
1. **Kotlin/JS** for rapid prototyping and development
2. **WASM** for performance-critical operations
3. **Progressive Enhancement** for maximum browser compatibility

### Next Steps

1. **Start with Kotlin/JS** to validate the concept
2. **Implement WASM** for performance improvements
3. **Create comprehensive browser integration examples**
4. **Develop tooling** for easy adoption by healthcare web applications

This approach provides the best balance of development speed, runtime performance, and browser compatibility for healthcare web applications requiring OpenEHR template processing.