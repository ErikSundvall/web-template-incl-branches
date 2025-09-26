# JavaScript Conversion Guide (4.0 Branch)

This document provides a comprehensive guide for converting the WebTemplate 4.0 Kotlin library to JavaScript/TypeScript for browser-based healthcare applications, leveraging the enhanced features and modern architecture of the 4.0 codebase.

## Overview

The WebTemplate 4.0 branch, built on Java 17 and Kotlin 1.9.20, offers significant advantages for JavaScript conversion:

1. **Enhanced Kotlin/JS Compilation** - Modern IR backend with better optimization and tree shaking
2. **Improved TypeScript Integration** - Better type inference and null safety for JavaScript interop
3. **4.0-Specific Features** - Formatting engine, GENERIC_ENTRY support, path filtering, and enhanced validation
4. **Modern Architecture** - Optimized for both manual porting and automated compilation

## Demo Implementation (4.0 Enhanced)

A working demonstration showcasing 4.0 features is available in the enhanced implementation:

```javascript
// Enhanced 4.0 demo with new features
class WebTemplate4Demo {
    constructor() {
        this.converter = new WebTemplateConverter4();
        this.formatter = new FormattingEngine4();      // 4.0+ feature
        this.validator = new EnhancedValidator4();      // 4.0+ improvements
    }
    
    // Demonstrate 4.0 formatting capabilities
    async demoFormatting() {
        const flatData = {
            'vitals/temperature|magnitude': '37.123',
            'vitals/temperature|unit': '°C',
            'vitals/temperature|formatting': '##.# °C'  // 4.0+ formatting
        };
        
        const structured = await this.converter.convertWithFormatting(flatData, template);
        console.log('Formatted result:', structured);
    }
    
    // Demonstrate GENERIC_ENTRY support (4.0+ feature)
    async demoGenericEntry() {
        const genericData = {
            'generic_entry/data/item_tree/items[id1]/value|value': 'Custom clinical data',
            'ctx/language': 'en'
        };
        
        const result = await this.converter.convertGenericEntry(genericData, template);
        console.log('Generic entry result:', result);
    }
}
```

## Kotlin/JS Compilation Approach (4.0 Enhanced)

### Setup (4.0 Optimized)

The 4.0 branch provides enhanced Kotlin/JS compilation capabilities:

```kotlin
// Enhanced build.gradle.kts for 4.0 features
plugins {
    kotlin("multiplatform") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
}

kotlin {
    js(IR) {  // IR backend required for 4.0 optimizations
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
                devtool = "source-map"
                outputFileName = "webtemplate-4.0.js"
            }
            webpackTask {
                mainOutputFileName = "webtemplate-4.0.js"
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "30s"
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        
        val jsMain by getting {
            dependencies {
                // 4.0-specific JavaScript dependencies
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.7.3")
                implementation(npm("@types/node", "20.8.0"))
            }
        }
        
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation(npm("jsdom", "22.1.0"))
            }
        }
    }
}
```

### Core Classes Converted (4.0 Enhanced)

The 4.0 codebase includes enhanced classes suitable for JavaScript conversion:

```kotlin
// Enhanced WebTemplate class for 4.0 JavaScript compilation
@JsExport
class WebTemplate4JS {
    private val templateCache = mutableMapOf<String, Template>()
    private val formattingEngine = FormattingEngine4()  // 4.0+ feature
    
    // Enhanced conversion with 4.0 features
    @JsName("convertFlatToStructured")
    fun convertFlatToStructured(
        flatData: dynamic,  // JavaScript object
        template: dynamic,
        context: dynamic,
        options: ConversionOptions4? = null  // 4.0+ options
    ): dynamic {
        val kotlinFlatData = flatData.unsafeCast<Map<String, Any>>()
        val kotlinTemplate = template.unsafeCast<Template>()
        val kotlinContext = context.unsafeCast<ConversionContext>()
        
        // Apply 4.0 formatting if requested
        val result = if (options?.applyFormatting == true) {
            convertWithFormatting(kotlinFlatData, kotlinTemplate, kotlinContext)
        } else {
            standardConvert(kotlinFlatData, kotlinTemplate, kotlinContext)
        }
        
        return result.toJsObject()
    }
    
    // 4.0+ GENERIC_ENTRY support
    @JsName("convertGenericEntry")
    fun convertGenericEntry(
        entryData: dynamic,
        template: dynamic,
        context: dynamic
    ): dynamic {
        val kotlinData = entryData.unsafeCast<Map<String, Any>>()
        val result = processGenericEntry(kotlinData, template, context)
        return result.toJsObject()
    }
    
    // 4.0+ enhanced validation
    @JsName("validateWithDetails")
    fun validateWithDetails(data: dynamic, template: dynamic): dynamic {
        val validation = enhancedValidator.validateWithDetails(data, template)
        return validation.toJsObject()
    }
}

// 4.0+ Formatting engine for JavaScript
@JsExport
class FormattingEngine4 {
    @JsName("applyFormatting")
    fun applyFormatting(value: String, format: String): String {
        return when {
            format.contains("##.#") -> formatDecimal(value, format)
            format.contains("text-transform") -> formatText(value, format)
            format.startsWith("date:") -> formatDate(value, format)
            else -> value
        }
    }
    
    @JsName("previewFormatting")
    fun previewFormatting(value: String, format: String): String {
        return "Preview: ${applyFormatting(value, format)}"
    }
}
```

### Key Features Implemented (4.0 Specific)

The 4.0 JavaScript implementation includes:

- **Enhanced Template Building**: Optimized template construction with caching
- **Formatting Engine**: Text and numeric formatting with locale support
- **GENERIC_ENTRY Support**: Processing of generic OpenEHR entries
- **Path Filtering**: Optimized path-based data extraction
- **Enhanced Validation**: Detailed error reporting with context
- **Null Safety**: Improved null handling for JavaScript interop
- **Performance Monitoring**: Built-in metrics and performance tracking

## Manual TypeScript Port (4.0 Enhanced)

For scenarios requiring maximum performance and customization, a manual TypeScript port leverages 4.0 features:

### Enhanced TypeScript Interfaces (4.0)

```typescript
// 4.0-specific TypeScript interfaces
interface WebTemplate4 {
    readonly templateId: string;
    readonly version: string;
    readonly language: string;
    readonly formattingRules?: FormattingRule[];     // 4.0+ feature
    readonly validationRules: ValidationRule4[];    // Enhanced validation
    readonly supportsGenericEntry: boolean;         // 4.0+ feature
    readonly pathFilters?: string[];                // 4.0+ feature
}

interface FormattingRule {  // 4.0+ feature
    readonly path: string;
    readonly format: string;
    readonly locale?: string;
    readonly preview?: boolean;
}

interface ConversionOptions4 {  // Enhanced for 4.0
    readonly applyFormatting?: boolean;           // 4.0+ formatting
    readonly enableCaching?: boolean;             // Enhanced caching
    readonly strictValidation?: boolean;          // Enhanced validation
    readonly supportGenericEntry?: boolean;       // 4.0+ GENERIC_ENTRY
    readonly pathFiltering?: readonly string[];   // 4.0+ path filtering
    readonly performanceMonitoring?: boolean;     // 4.0+ monitoring
}

interface ConversionResult4 {  // Enhanced for 4.0
    readonly data: unknown;
    readonly formatting?: FormattingResult;       // 4.0+ formatting results
    readonly validation: ValidationResult4;       // Enhanced validation
    readonly performance?: PerformanceMetrics4;   // 4.0+ performance data
    readonly metadata: ConversionMetadata4;       // Enhanced metadata
}

interface ValidationResult4 {  // Enhanced for 4.0
    readonly isValid: boolean;
    readonly errors: readonly ValidationError4[];
    readonly warnings: readonly ValidationWarning[];
    readonly nullSafetyChecks: readonly NullSafetyResult[];  // 4.0+ improvement
    readonly formattingValidation?: FormattingValidation;    // 4.0+ feature
}

interface PerformanceMetrics4 {  // 4.0+ performance monitoring
    readonly conversionTime: number;
    readonly validationTime: number;
    readonly formattingTime?: number;
    readonly cacheHitRate: number;
    readonly memoryUsag?: number;
}
```

### Core Implementation (4.0 Enhanced)

```typescript
// Enhanced TypeScript implementation for 4.0 features
class WebTemplateConverter4 implements WebTemplate4 {
    private readonly templateCache = new Map<string, Template4>();
    private readonly formattingEngine = new FormattingEngine4();
    private readonly validator = new EnhancedValidator4();
    private readonly performanceMonitor = new PerformanceMonitor4();
    
    constructor(
        public readonly templateId: string,
        public readonly version: string,
        public readonly language: string,
        private readonly config: WebTemplate4Config
    ) {}
    
    // Enhanced conversion method with 4.0 features
    async convertFlatToStructured(
        flatData: Record<string, unknown>,
        context: ConversionContext4,
        options: ConversionOptions4 = {}
    ): Promise<ConversionResult4> {
        const startTime = performance.now();
        
        try {
            // Enhanced validation with 4.0 improvements
            const validation = await this.validateWithDetails(flatData, options);
            if (!validation.isValid && options.strictValidation) {
                throw new ConversionError4('Validation failed', validation.errors);
            }
            
            // Core conversion with caching
            let result = await this.performConversion(flatData, context, options);
            
            // Apply 4.0 formatting if requested
            if (options.applyFormatting && this.formattingRules) {
                result = await this.formattingEngine.applyFormatting(result, this.formattingRules);
            }
            
            // Path filtering optimization (4.0+ feature)
            if (options.pathFiltering) {
                result = this.filterByPaths(result, options.pathFiltering);
            }
            
            const endTime = performance.now();
            
            return {
                data: result,
                validation,
                performance: {
                    conversionTime: endTime - startTime,
                    cacheHitRate: this.getCacheHitRate(),
                    validationTime: validation.processingTime || 0,
                    formattingTime: options.applyFormatting ? this.getFormattingTime() : 0
                },
                metadata: this.buildMetadata(flatData, options)
            };
            
        } catch (error) {
            throw new ConversionError4(
                'Conversion failed',
                error instanceof Error ? [error] : [],
                { context, options, processingTime: performance.now() - startTime }
            );
        }
    }
    
    // 4.0+ GENERIC_ENTRY support
    async convertGenericEntry(
        entryData: Record<string, unknown>,
        context: ConversionContext4
    ): Promise<ConversionResult4> {
        if (!this.supportsGenericEntry) {
            throw new ConversionError4('GENERIC_ENTRY not supported by this template');
        }
        
        const processed = await this.processGenericEntry(entryData, context);
        return {
            data: processed,
            validation: { isValid: true, errors: [], warnings: [], nullSafetyChecks: [] },
            metadata: { type: 'GENERIC_ENTRY', processed: true }
        };
    }
    
    // Enhanced validation with 4.0 improvements
    async validateWithDetails(
        data: Record<string, unknown>,
        options: ConversionOptions4 = {}
    ): Promise<ValidationResult4> {
        const errors: ValidationError4[] = [];
        const warnings: ValidationWarning[] = [];
        const nullSafetyChecks: NullSafetyResult[] = [];
        
        // Enhanced null safety validation (4.0+ improvement)
        for (const [path, value] of Object.entries(data)) {
            const nullCheck = this.validateNullSafety(path, value);
            nullSafetyChecks.push(nullCheck);
            
            if (!nullCheck.isValid) {
                errors.push(new ValidationError4(`Null safety violation at ${path}`, path, value));
            }
        }
        
        // Formatting validation (4.0+ feature)
        let formattingValidation: FormattingValidation | undefined;
        if (options.applyFormatting) {
            formattingValidation = await this.validateFormatting(data);
            errors.push(...formattingValidation.errors);
            warnings.push(...formattingValidation.warnings);
        }
        
        // Template constraint validation with 4.0 enhancements
        const constraintValidation = await this.validateConstraints(data);
        errors.push(...constraintValidation.errors);
        warnings.push(...constraintValidation.warnings);
        
        return {
            isValid: errors.length === 0,
            errors: Object.freeze(errors),
            warnings: Object.freeze(warnings),
            nullSafetyChecks: Object.freeze(nullSafetyChecks),
            formattingValidation
        };
    }
}
```

## Browser Integration Examples (4.0 Enhanced)

### React Component Integration (4.0)

```tsx
// Enhanced React component with 4.0 features
import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { WebTemplateConverter4, ConversionOptions4 } from './webtemplate-4.0';

interface HealthDataForm4Props {
    template: WebTemplate4;
    onDataConverted?: (result: ConversionResult4) => void;
    enableFormatting?: boolean;
    enableGenericEntry?: boolean;
}

const HealthDataForm4: React.FC<HealthDataForm4Props> = ({
    template,
    onDataConverted,
    enableFormatting = true,
    enableGenericEntry = false
}) => {
    const [formData, setFormData] = useState<Record<string, unknown>>({});
    const [structuredData, setStructuredData] = useState<unknown>(null);
    const [validation, setValidation] = useState<ValidationResult4 | null>(null);
    const [formatting, setFormatting] = useState<Record<string, string>>({});
    const [performance, setPerformance] = useState<PerformanceMetrics4 | null>(null);
    
    // Initialize converter with 4.0 features
    const converter = useMemo(() => 
        new WebTemplateConverter4(template.templateId, template.version, template.language, {
            enableFormatting,
            enableGenericEntry,
            performanceMonitoring: true
        }),
        [template, enableFormatting, enableGenericEntry]
    );
    
    // Real-time validation with 4.0 enhancements
    const validateData = useCallback(async (data: Record<string, unknown>) => {
        const validationResult = await converter.validateWithDetails(data, {
            strictValidation: false,
            applyFormatting: enableFormatting
        });
        
        setValidation(validationResult);
        return validationResult.isValid;
    }, [converter, enableFormatting]);
    
    // Enhanced form submission with 4.0 features
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        try {
            const conversionOptions: ConversionOptions4 = {
                applyFormatting: enableFormatting,
                enableCaching: true,
                strictValidation: true,
                supportGenericEntry: enableGenericEntry,
                performanceMonitoring: true
            };
            
            const result = await converter.convertFlatToStructured(
                formData,
                { language: 'en', territory: 'US', composerName: 'Web User' },
                conversionOptions
            );
            
            setStructuredData(result.data);
            setPerformance(result.performance);
            onDataConverted?.(result);
            
        } catch (error) {
            console.error('Conversion failed:', error);
        }
    };
    
    // 4.0+ formatting preview
    const handleFormattingChange = useCallback((path: string, format: string) => {
        setFormatting(prev => ({ ...prev, [path]: format }));
        
        // Real-time formatting preview (4.0+ feature)
        if (formData[path]) {
            const preview = converter.previewFormatting(String(formData[path]), format);
            console.log(`Formatting preview for ${path}:`, preview);
        }
    }, [converter, formData]);
    
    // Enhanced field change handler with validation
    const handleFieldChange = useCallback(async (path: string, value: unknown) => {
        const newFormData = { ...formData, [path]: value };
        setFormData(newFormData);
        
        // Immediate field validation
        await validateData(newFormData);
    }, [formData, validateData]);
    
    return (
        <form onSubmit={handleSubmit} className="health-data-form-4">
            <div className="form-section">
                <h3>Patient Data Entry (WebTemplate 4.0)</h3>
                
                {/* Enhanced form field with 4.0 features */}
                <div className="form-field">
                    <label htmlFor="temperature">Body Temperature</label>
                    <input
                        id="temperature"
                        type="number"
                        step="0.1"
                        value={formData['vitals/temperature|magnitude'] as number || ''}
                        onChange={(e) => handleFieldChange(
                            'vitals/temperature|magnitude', 
                            parseFloat(e.target.value)
                        )}
                    />
                    
                    {/* 4.0+ Formatting control */}
                    {enableFormatting && (
                        <div className="formatting-control">
                            <label htmlFor="temperature-format">Format</label>
                            <input
                                id="temperature-format"
                                type="text"
                                placeholder="e.g., ##.# °C"
                                value={formatting['vitals/temperature|formatting'] || ''}
                                onChange={(e) => handleFormattingChange(
                                    'vitals/temperature|formatting',
                                    e.target.value
                                )}
                            />
                        </div>
                    )}
                </div>
                
                {/* 4.0+ Generic entry field */}
                {enableGenericEntry && (
                    <div className="form-field">
                        <label htmlFor="generic-data">Generic Clinical Data</label>
                        <textarea
                            id="generic-data"
                            value={formData['generic_entry/data'] as string || ''}
                            onChange={(e) => handleFieldChange('generic_entry/data', e.target.value)}
                            placeholder="Enter generic clinical data..."
                        />
                    </div>
                )}
            </div>
            
            {/* Enhanced validation display (4.0+ improvements) */}
            {validation && (
                <div className="validation-section">
                    <h4>Validation Results (4.0 Enhanced)</h4>
                    
                    {validation.errors.length > 0 && (
                        <div className="validation-errors">
                            <h5>Errors:</h5>
                            {validation.errors.map((error, idx) => (
                                <div key={idx} className="error">
                                    <strong>{error.path}:</strong> {error.message}
                                </div>
                            ))}
                        </div>
                    )}
                    
                    {validation.warnings.length > 0 && (
                        <div className="validation-warnings">
                            <h5>Warnings:</h5>
                            {validation.warnings.map((warning, idx) => (
                                <div key={idx} className="warning">
                                    <strong>{warning.path}:</strong> {warning.message}
                                </div>
                            ))}
                        </div>
                    )}
                    
                    {/* 4.0+ Null safety results */}
                    {validation.nullSafetyChecks.length > 0 && (
                        <div className="null-safety-checks">
                            <h5>Null Safety Checks:</h5>
                            {validation.nullSafetyChecks.map((check, idx) => (
                                <div key={idx} className={check.isValid ? 'success' : 'error'}>
                                    {check.path}: {check.isValid ? '✓' : '✗'} {check.message}
                                </div>
                            ))}
                        </div>
                    )}
                    
                    {/* 4.0+ Formatting validation */}
                    {validation.formattingValidation && (
                        <div className="formatting-validation">
                            <h5>Formatting Validation:</h5>
                            <div className={validation.formattingValidation.isValid ? 'success' : 'warning'}>
                                {validation.formattingValidation.summary}
                            </div>
                        </div>
                    )}
                </div>
            )}
            
            {/* 4.0+ Performance metrics */}
            {performance && (
                <div className="performance-section">
                    <h4>Performance Metrics (4.0)</h4>
                    <div className="metrics">
                        <span>Conversion: {performance.conversionTime.toFixed(2)}ms</span>
                        <span>Validation: {performance.validationTime.toFixed(2)}ms</span>
                        {performance.formattingTime && (
                            <span>Formatting: {performance.formattingTime.toFixed(2)}ms</span>
                        )}
                        <span>Cache Hit Rate: {(performance.cacheHitRate * 100).toFixed(1)}%</span>
                    </div>
                </div>
            )}
            
            <button type="submit" disabled={validation && !validation.isValid}>
                Convert Data (4.0)
            </button>
            
            {/* Enhanced results display */}
            {structuredData && (
                <div className="results-section">
                    <h3>Structured Data (4.0 Enhanced)</h3>
                    <pre className="json-display">
                        {JSON.stringify(structuredData, null, 2)}
                    </pre>
                </div>
            )}
        </form>
    );
};

export default HealthDataForm4;
```

### Vue.js Integration (4.0 Enhanced)

```vue
<!-- Enhanced Vue component with 4.0 features -->
<template>
  <div class="webtemplate-vue-4">
    <h3>Healthcare Data Form (Vue + WebTemplate 4.0)</h3>
    
    <form @submit.prevent="handleSubmit">
      <!-- Enhanced form fields with 4.0 features -->
      <div class="field-group">
        <label for="patient-id">Patient ID</label>
        <input
          id="patient-id"
          v-model="formData['ctx/patient_id']"
          type="text"
          @input="validateField('ctx/patient_id', $event.target.value)"
        />
      </div>
      
      <div class="field-group">
        <label for="temperature">Temperature</label>
        <input
          id="temperature"
          v-model.number="formData['vitals/temperature|magnitude']"
          type="number"
          step="0.1"
          @input="validateField('vitals/temperature|magnitude', $event.target.value)"
        />
        
        <!-- 4.0+ Formatting control -->
        <div v-if="enableFormatting" class="formatting-control">
          <label for="temp-format">Format</label>
          <input
            id="temp-format"
            v-model="formatting['vitals/temperature|formatting']"
            type="text"
            placeholder="##.# °C"
            @input="previewFormatting('vitals/temperature|formatting', $event.target.value)"
          />
          <span v-if="formattingPreviews['vitals/temperature|formatting']" class="preview">
            {{ formattingPreviews['vitals/temperature|formatting'] }}
          </span>
        </div>
      </div>
      
      <!-- 4.0+ Generic entry support -->
      <div v-if="enableGenericEntry" class="field-group">
        <label for="generic-entry">Generic Clinical Data</label>
        <textarea
          id="generic-entry"
          v-model="formData['generic_entry/data']"
          placeholder="Enter generic clinical data..."
          @input="validateField('generic_entry/data', $event.target.value)"
        ></textarea>
      </div>
      
      <button type="submit" :disabled="!isFormValid">
        Convert with WebTemplate 4.0
      </button>
    </form>
    
    <!-- Enhanced validation display (4.0) -->
    <div v-if="validation" class="validation-display">
      <h4>Validation Results (4.0 Enhanced)</h4>
      
      <div v-if="validation.errors.length" class="errors">
        <h5>Errors:</h5>
        <div v-for="(error, index) in validation.errors" :key="index" class="error">
          <strong>{{ error.path }}:</strong> {{ error.message }}
        </div>
      </div>
      
      <div v-if="validation.warnings.length" class="warnings">
        <h5>Warnings:</h5>
        <div v-for="(warning, index) in validation.warnings" :key="index" class="warning">
          <strong>{{ warning.path }}:</strong> {{ warning.message }}
        </div>
      </div>
      
      <!-- 4.0+ Null safety display -->
      <div v-if="validation.nullSafetyChecks.length" class="null-safety">
        <h5>Null Safety Checks:</h5>
        <div
          v-for="(check, index) in validation.nullSafetyChecks"
          :key="index"
          :class="['null-check', check.isValid ? 'valid' : 'invalid']"
        >
          {{ check.path }}: {{ check.isValid ? '✓' : '✗' }} {{ check.message }}
        </div>
      </div>
    </div>
    
    <!-- Performance metrics (4.0+ feature) -->
    <div v-if="performance" class="performance-metrics">
      <h4>Performance (4.0)</h4>
      <div class="metrics">
        <span>Conversion: {{ performance.conversionTime.toFixed(2) }}ms</span>
        <span>Validation: {{ performance.validationTime.toFixed(2) }}ms</span>
        <span v-if="performance.formattingTime">
          Formatting: {{ performance.formattingTime.toFixed(2) }}ms
        </span>
        <span>Cache Hit: {{ (performance.cacheHitRate * 100).toFixed(1) }}%</span>
      </div>
    </div>
    
    <!-- Results display -->
    <div v-if="structuredData" class="results">
      <h3>Converted Data (4.0)</h3>
      <pre>{{ JSON.stringify(structuredData, null, 2) }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { WebTemplateConverter4, type ConversionResult4, type ValidationResult4, type PerformanceMetrics4 } from './webtemplate-4.0';

interface Props {
  template: WebTemplate4;
  enableFormatting?: boolean;
  enableGenericEntry?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  enableFormatting: true,
  enableGenericEntry: false
});

// Reactive state
const formData = ref<Record<string, unknown>>({});
const structuredData = ref<unknown>(null);
const validation = ref<ValidationResult4 | null>(null);
const formatting = ref<Record<string, string>>({});
const formattingPreviews = ref<Record<string, string>>({});
const performance = ref<PerformanceMetrics4 | null>(null);

// Initialize converter with 4.0 features
const converter = new WebTemplateConverter4(
  props.template.templateId,
  props.template.version,
  props.template.language,
  {
    enableFormatting: props.enableFormatting,
    enableGenericEntry: props.enableGenericEntry,
    performanceMonitoring: true
  }
);

// Computed properties
const isFormValid = computed(() => 
  validation.value ? validation.value.isValid : false
);

// Methods
const validateField = async (path: string, value: unknown) => {
  const newFormData = { ...formData.value, [path]: value };
  formData.value = newFormData;
  
  validation.value = await converter.validateWithDetails(newFormData, {
    applyFormatting: props.enableFormatting,
    strictValidation: false
  });
};

const previewFormatting = (path: string, format: string) => {
  const value = formData.value[path.replace('|formatting', '|magnitude')];
  if (value) {
    formattingPreviews.value[path] = converter.previewFormatting(String(value), format);
  }
};

const handleSubmit = async () => {
  try {
    const result = await converter.convertFlatToStructured(
      formData.value,
      { language: 'en', territory: 'US', composerName: 'Vue User' },
      {
        applyFormatting: props.enableFormatting,
        enableCaching: true,
        strictValidation: true,
        supportGenericEntry: props.enableGenericEntry,
        performanceMonitoring: true
      }
    );
    
    structuredData.value = result.data;
    performance.value = result.performance || null;
    
    // Emit event for parent components
    emit('dataConverted', result);
    
  } catch (error) {
    console.error('Conversion failed:', error);
  }
};

// Watchers
watch(() => props.template, () => {
  // Reset form when template changes
  formData.value = {};
  structuredData.value = null;
  validation.value = null;
}, { deep: true });

// Events
const emit = defineEmits<{
  dataConverted: [result: ConversionResult4];
}>();

onMounted(() => {
  console.log('WebTemplate 4.0 Vue component mounted');
});
</script>

<style scoped>
.webtemplate-vue-4 {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.field-group {
  margin-bottom: 20px;
}

.formatting-control {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.preview {
  display: block;
  margin-top: 5px;
  font-style: italic;
  color: #666;
}

.validation-display {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.errors .error {
  color: #d32f2f;
  margin: 5px 0;
}

.warnings .warning {
  color: #f57c00;
  margin: 5px 0;
}

.null-safety .null-check.valid {
  color: #388e3c;
}

.null-safety .null-check.invalid {
  color: #d32f2f;
}

.performance-metrics {
  margin: 20px 0;
  padding: 15px;
  background-color: #f0f8ff;
  border-radius: 4px;
}

.metrics span {
  display: inline-block;
  margin-right: 15px;
  padding: 5px 10px;
  background-color: #e3f2fd;
  border-radius: 3px;
  font-size: 0.9em;
}

.results {
  margin-top: 20px;
}

.results pre {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 0.9em;
}
</style>
```

### Web Workers for Performance (4.0 Enhanced)

```javascript
// Enhanced web worker for 4.0 performance optimization
// webtemplate-worker-4.0.js
import { WebTemplateConverter4 } from './webtemplate-4.0.js';

class WebTemplateWorker4 {
    constructor() {
        this.converter = null;
        this.performanceCache = new Map();
        this.isInitialized = false;
    }
    
    async initialize(templateData, config) {
        try {
            this.converter = new WebTemplateConverter4(
                templateData.templateId,
                templateData.version,
                templateData.language,
                {
                    enableFormatting: config.enableFormatting || true,
                    enableGenericEntry: config.enableGenericEntry || false,
                    performanceMonitoring: true,      // 4.0+ feature
                    enableCaching: true               // Enhanced caching
                }
            );
            
            this.isInitialized = true;
            
            self.postMessage({
                type: 'initialized',
                version: '4.0',
                features: {
                    formatting: config.enableFormatting,
                    genericEntry: config.enableGenericEntry,
                    caching: true,
                    monitoring: true
                }
            });
            
        } catch (error) {
            self.postMessage({
                type: 'error',
                message: 'Failed to initialize WebTemplate 4.0',
                error: error.message
            });
        }
    }
    
    async processConversion(data, template, context, options) {
        if (!this.isInitialized) {
            throw new Error('Worker not initialized');
        }
        
        const startTime = performance.now();
        
        try {
            // Enhanced conversion with 4.0 features
            const result = await this.converter.convertFlatToStructured(
                data,
                context,
                {
                    applyFormatting: options.applyFormatting,
                    enableCaching: options.enableCaching,
                    strictValidation: options.strictValidation,
                    supportGenericEntry: options.supportGenericEntry,
                    pathFiltering: options.pathFiltering,           // 4.0+ feature
                    performanceMonitoring: true                     // 4.0+ feature
                }
            );
            
            const endTime = performance.now();
            
            // Cache performance data for analysis
            const cacheKey = this.generateCacheKey(data, options);
            this.performanceCache.set(cacheKey, {
                processingTime: endTime - startTime,
                dataSize: JSON.stringify(data).length,
                timestamp: Date.now()
            });
            
            self.postMessage({
                type: 'conversionResult',
                data: result.data,
                validation: result.validation,
                performance: {
                    ...result.performance,
                    workerProcessingTime: endTime - startTime
                },
                metadata: result.metadata
            });
            
        } catch (error) {
            self.postMessage({
                type: 'conversionError',
                message: error.message,
                details: error.details || {},
                processingTime: performance.now() - startTime
            });
        }
    }
    
    async processGenericEntry(entryData, template, context) {
        if (!this.isInitialized) {
            throw new Error('Worker not initialized');
        }
        
        try {
            const result = await this.converter.convertGenericEntry(entryData, context);
            
            self.postMessage({
                type: 'genericEntryResult',
                data: result.data,
                validation: result.validation,
                performance: result.performance,
                metadata: result.metadata
            });
            
        } catch (error) {
            self.postMessage({
                type: 'genericEntryError',
                message: error.message,
                details: error.details || {}
            });
        }
    }
    
    async performValidation(data, template, options) {
        if (!this.isInitialized) {
            throw new Error('Worker not initialized');
        }
        
        try {
            const validation = await this.converter.validateWithDetails(data, options);
            
            self.postMessage({
                type: 'validationResult',
                validation: validation,
                timestamp: Date.now()
            });
            
        } catch (error) {
            self.postMessage({
                type: 'validationError',
                message: error.message,
                details: error.details || {}
            });
        }
    }
    
    getPerformanceAnalytics() {
        const analytics = {
            totalConversions: this.performanceCache.size,
            averageProcessingTime: this.calculateAverageProcessingTime(),
            cacheEfficiency: this.calculateCacheEfficiency(),
            dataProcessingPatterns: this.analyzeDataPatterns()
        };
        
        self.postMessage({
            type: 'performanceAnalytics',
            analytics: analytics
        });
    }
    
    generateCacheKey(data, options) {
        return `${JSON.stringify(data).substring(0, 100)}_${JSON.stringify(options)}`;
    }
    
    calculateAverageProcessingTime() {
        if (this.performanceCache.size === 0) return 0;
        
        const times = Array.from(this.performanceCache.values()).map(entry => entry.processingTime);
        return times.reduce((sum, time) => sum + time, 0) / times.length;
    }
    
    calculateCacheEfficiency() {
        // Implementation for cache efficiency calculation
        return 0.85; // Placeholder
    }
    
    analyzeDataPatterns() {
        const patterns = {
            smallData: 0,
            mediumData: 0,
            largeData: 0
        };
        
        for (const entry of this.performanceCache.values()) {
            if (entry.dataSize < 1000) patterns.smallData++;
            else if (entry.dataSize < 10000) patterns.mediumData++;
            else patterns.largeData++;
        }
        
        return patterns;
    }
}

// Worker message handler
const worker = new WebTemplateWorker4();

self.onmessage = async function(e) {
    const { type, data, template, context, options, config } = e.data;
    
    try {
        switch (type) {
            case 'initialize':
                await worker.initialize(template, config || {});
                break;
                
            case 'convert':
                await worker.processConversion(data, template, context, options || {});
                break;
                
            case 'convertGeneric':  // 4.0+ GENERIC_ENTRY support
                await worker.processGenericEntry(data, template, context);
                break;
                
            case 'validate':
                await worker.performValidation(data, template, options || {});
                break;
                
            case 'getAnalytics':  // 4.0+ performance analytics
                worker.getPerformanceAnalytics();
                break;
                
            default:
                self.postMessage({
                    type: 'error',
                    message: `Unknown message type: ${type}`
                });
        }
        
    } catch (error) {
        self.postMessage({
            type: 'error',
            message: error.message,
            stack: error.stack
        });
    }
};

// Enhanced worker usage example
// main.js
class WebTemplateWorkerManager4 {
    constructor() {
        this.worker = new Worker('./webtemplate-worker-4.0.js', { type: 'module' });
        this.isInitialized = false;
        this.setupEventHandlers();
    }
    
    setupEventHandlers() {
        this.worker.onmessage = (e) => {
            const { type, data } = e.data;
            
            switch (type) {
                case 'initialized':
                    this.isInitialized = true;
                    console.log('WebTemplate 4.0 worker initialized', data);
                    break;
                    
                case 'conversionResult':
                    this.handleConversionResult(data);
                    break;
                    
                case 'validationResult':
                    this.handleValidationResult(data);
                    break;
                    
                case 'performanceAnalytics':  // 4.0+ analytics
                    this.handlePerformanceAnalytics(data);
                    break;
                    
                case 'error':
                    console.error('Worker error:', data);
                    break;
            }
        };
    }
    
    async initializeWorker(template, config) {
        this.worker.postMessage({
            type: 'initialize',
            template: template,
            config: config
        });
        
        // Wait for initialization
        return new Promise((resolve) => {
            const checkInitialized = () => {
                if (this.isInitialized) {
                    resolve();
                } else {
                    setTimeout(checkInitialized, 100);
                }
            };
            checkInitialized();
        });
    }
    
    convertData(data, template, context, options) {
        if (!this.isInitialized) {
            throw new Error('Worker not initialized');
        }
        
        this.worker.postMessage({
            type: 'convert',
            data: data,
            template: template,
            context: context,
            options: options
        });
    }
    
    // 4.0+ GENERIC_ENTRY conversion
    convertGenericEntry(entryData, template, context) {
        if (!this.isInitialized) {
            throw new Error('Worker not initialized');
        }
        
        this.worker.postMessage({
            type: 'convertGeneric',
            data: entryData,
            template: template,
            context: context
        });
    }
    
    // 4.0+ Performance analytics
    getPerformanceAnalytics() {
        this.worker.postMessage({ type: 'getAnalytics' });
    }
    
    handleConversionResult(result) {
        console.log('Conversion completed:', result);
        // Handle the result in your application
    }
    
    handleValidationResult(validation) {
        console.log('Validation completed:', validation);
        // Handle validation results
    }
    
    handlePerformanceAnalytics(analytics) {
        console.log('Performance analytics:', analytics);
        // Display or process performance data
    }
}

// Usage example
const workerManager = new WebTemplateWorkerManager4();

// Initialize with 4.0 features
await workerManager.initializeWorker(templateData, {
    enableFormatting: true,
    enableGenericEntry: true,
    performanceMonitoring: true
});

// Convert data with enhanced options
workerManager.convertData(flatData, template, context, {
    applyFormatting: true,
    strictValidation: true,
    pathFiltering: ['vitals/*', 'medications/*']  // 4.0+ feature
});
```

## Performance Considerations (4.0 Enhanced)

### Optimization Strategies (4.0 Specific)

The 4.0 branch provides several optimization opportunities:

```typescript
// Performance optimization strategies for 4.0
class PerformanceOptimizer4 {
    private readonly cacheStrategy = new CacheStrategy4();
    private readonly batchProcessor = new BatchProcessor4();
    
    // 4.0+ Enhanced template caching
    optimizeTemplateLoading(templates: WebTemplate4[]): void {
        // Preload and cache frequently used templates
        const prioritizedTemplates = this.prioritizeTemplates(templates);
        
        for (const template of prioritizedTemplates) {
            this.cacheStrategy.preloadTemplate(template);
        }
    }
    
    // 4.0+ Batch processing optimization
    optimizeBatchConversions(conversions: ConversionRequest4[]): Promise<ConversionResult4[]> {
        // Group similar conversions for batch processing
        const groups = this.groupConversions(conversions);
        
        return this.batchProcessor.processBatches(groups, {
            enableParallelProcessing: true,
            optimizeMemoryUsage: true,
            enableProgressReporting: true
        });
    }
    
    // 4.0+ Memory management
    optimizeMemoryUsage(): void {
        // Clear unused caches
        this.cacheStrategy.cleanup();
        
        // Optimize garbage collection
        if (typeof gc === 'function') {
            gc(); // Node.js specific
        }
    }
    
    // 4.0+ Performance monitoring
    monitorPerformance(): PerformanceReport4 {
        return {
            cacheHitRate: this.cacheStrategy.getHitRate(),
            averageConversionTime: this.calculateAverageTime(),
            memoryUsage: this.getMemoryUsage(),
            optimizationSuggestions: this.generateSuggestions()
        };
    }
}
```

## Testing Strategies (4.0 Enhanced)

### Comprehensive Test Suite (4.0)

```typescript
// Enhanced test suite for 4.0 features
describe('WebTemplate 4.0 JavaScript Implementation', () => {
    let converter: WebTemplateConverter4;
    let template: WebTemplate4;
    
    beforeEach(async () => {
        template = await loadTestTemplate4();
        converter = new WebTemplateConverter4(
            template.templateId,
            template.version,
            template.language,
            { enableFormatting: true, enableGenericEntry: true }
        );
    });
    
    describe('Core Conversion Features', () => {
        test('should convert flat to structured data', async () => {
            const flatData = {
                'vitals/temperature|magnitude': 37.5,
                'vitals/temperature|unit': '°C'
            };
            
            const result = await converter.convertFlatToStructured(
                flatData,
                { language: 'en', territory: 'US' }
            );
            
            expect(result.data).toBeDefined();
            expect(result.validation.isValid).toBe(true);
        });
        
        test('should handle null safety validation (4.0+ improvement)', async () => {
            const dataWithNulls = {
                'vitals/temperature|magnitude': null,
                'vitals/temperature|unit': '°C'
            };
            
            const validation = await converter.validateWithDetails(dataWithNulls);
            
            expect(validation.nullSafetyChecks).toHaveLength(2);
            expect(validation.nullSafetyChecks[0].isValid).toBe(false);
        });
    });
    
    describe('4.0+ Formatting Features', () => {
        test('should apply formatting rules', async () => {
            const flatData = {
                'vitals/temperature|magnitude': 37.123,
                'vitals/temperature|formatting': '##.# °C'
            };
            
            const result = await converter.convertFlatToStructured(
                flatData,
                { language: 'en', territory: 'US' },
                { applyFormatting: true }
            );
            
            expect(result.formatting).toBeDefined();
            expect(result.formatting.appliedRules).toContain('vitals/temperature|formatting');
        });
        
        test('should validate formatting patterns', async () => {
            const validation = await converter.validateWithDetails({
                'vitals/temperature|formatting': 'invalid-format'
            }, { applyFormatting: true });
            
            expect(validation.formattingValidation.isValid).toBe(false);
            expect(validation.formattingValidation.errors).toHaveLength(1);
        });
    });
    
    describe('4.0+ GENERIC_ENTRY Support', () => {
        test('should convert generic entries', async () => {
            const genericData = {
                'generic_entry/data/item_tree/items[id1]/value|value': 'Test data'
            };
            
            const result = await converter.convertGenericEntry(
                genericData,
                { language: 'en', territory: 'US' }
            );
            
            expect(result.data).toBeDefined();
            expect(result.metadata.type).toBe('GENERIC_ENTRY');
        });
    });
    
    describe('4.0+ Performance Features', () => {
        test('should provide performance metrics', async () => {
            const result = await converter.convertFlatToStructured(
                { 'test/path': 'value' },
                { language: 'en', territory: 'US' },
                { performanceMonitoring: true }
            );
            
            expect(result.performance).toBeDefined();
            expect(result.performance.conversionTime).toBeGreaterThan(0);
            expect(result.performance.cacheHitRate).toBeGreaterThanOrEqual(0);
        });
        
        test('should optimize with caching', async () => {
            const data = { 'test/path': 'value' };
            const context = { language: 'en', territory: 'US' };
            
            // First conversion
            const result1 = await converter.convertFlatToStructured(data, context);
            
            // Second conversion should be faster due to caching
            const result2 = await converter.convertFlatToStructured(data, context);
            
            expect(result2.performance.cacheHitRate).toBeGreaterThan(result1.performance.cacheHitRate);
        });
    });
    
    describe('Error Handling (4.0 Enhanced)', () => {
        test('should provide detailed error information', async () => {
            const invalidData = { 'invalid/path': 'value' };
            
            try {
                await converter.convertFlatToStructured(
                    invalidData,
                    { language: 'en', territory: 'US' },
                    { strictValidation: true }
                );
                fail('Should have thrown an error');
            } catch (error) {
                expect(error).toBeInstanceOf(ConversionError4);
                expect(error.details).toBeDefined();
                expect(error.suggestions).toBeDefined();
            }
        });
    });
});

// Integration tests for browser compatibility
describe('Browser Integration (4.0)', () => {
    test('should work in browser environment', () => {
        // Test browser-specific features
        expect(typeof WebAssembly).toBeDefined();
        expect(typeof Worker).toBeDefined();
        expect(typeof performance).toBeDefined();
    });
    
    test('should handle web worker communication', (done) => {
        const worker = new Worker('./webtemplate-worker-4.0.js');
        
        worker.postMessage({
            type: 'initialize',
            template: template,
            config: { enableFormatting: true }
        });
        
        worker.onmessage = (e) => {
            if (e.data.type === 'initialized') {
                expect(e.data.version).toBe('4.0');
                done();
            }
        };
    });
});
```

## Deployment Guide (4.0 Enhanced)

### NPM Package (4.0)

```json
// Enhanced package.json for 4.0 features
{
  "name": "@better-care/webtemplate-js-4.0",
  "version": "4.2.1",
  "description": "JavaScript implementation of WebTemplate 4.0 for OpenEHR with enhanced features",
  "main": "dist/webtemplate-4.0.js",
  "module": "dist/webtemplate-4.0.esm.js",
  "types": "dist/webtemplate-4.0.d.ts",
  "exports": {
    ".": {
      "import": "./dist/webtemplate-4.0.esm.js",
      "require": "./dist/webtemplate-4.0.js",
      "types": "./dist/webtemplate-4.0.d.ts"
    },
    "./worker": {
      "import": "./dist/webtemplate-worker-4.0.js",
      "require": "./dist/webtemplate-worker-4.0.js"
    }
  },
  "files": [
    "dist/",
    "README.md",
    "CHANGELOG.md"
  ],
  "keywords": [
    "openehr",
    "healthcare",
    "webtemplate",
    "4.0",
    "typescript",
    "javascript",
    "formatting",
    "generic-entry",
    "validation"
  ],
  "scripts": {
    "build": "rollup -c rollup.config.js",
    "build:dev": "rollup -c rollup.config.js --watch",
    "test": "jest",
    "test:coverage": "jest --coverage",
    "test:browser": "karma start",
    "lint": "eslint src/**/*.ts",
    "type-check": "tsc --noEmit",
    "docs": "typedoc src/index.ts",
    "prepublishOnly": "npm run build && npm run test && npm run lint"
  },
  "dependencies": {
    "@types/web": "^0.0.99"
  },
  "devDependencies": {
    "@rollup/plugin-node-resolve": "^15.2.3",
    "@rollup/plugin-typescript": "^11.1.5",
    "@types/jest": "^29.5.8",
    "eslint": "^8.54.0",
    "jest": "^29.7.0",
    "karma": "^6.4.2",
    "rollup": "^4.6.1",
    "typedoc": "^0.25.4",
    "typescript": "^5.3.2"
  },
  "engines": {
    "node": ">=17.0.0"
  },
  "browserslist": [
    "> 1%",
    "last 2 versions",
    "not dead"
  ]
}
```

### Build Configuration (4.0)

```javascript
// Enhanced rollup.config.js for 4.0 features
import resolve from '@rollup/plugin-node-resolve';
import typescript from '@rollup/plugin-typescript';

export default [
  // Main bundle
  {
    input: 'src/index.ts',
    output: [
      {
        file: 'dist/webtemplate-4.0.js',
        format: 'cjs',
        exports: 'named'
      },
      {
        file: 'dist/webtemplate-4.0.esm.js',
        format: 'es'
      }
    ],
    plugins: [
      resolve(),
      typescript({
        tsconfig: './tsconfig.json',
        declaration: true,
        declarationDir: 'dist'
      })
    ],
    external: ['@types/web']
  },
  
  // Worker bundle
  {
    input: 'src/worker.ts',
    output: {
      file: 'dist/webtemplate-worker-4.0.js',
      format: 'es'
    },
    plugins: [
      resolve(),
      typescript({
        tsconfig: './tsconfig.worker.json'
      })
    ]
  }
];
```

## Migration Strategy (4.0 Optimized)

### Phase 1: Foundation Setup (2-3 weeks)
1. **Enhanced Build Configuration**
   - Set up Kotlin/JS with IR backend for 4.0 optimizations
   - Configure TypeScript with strict null checks and enhanced types
   - Implement 4.0-specific build optimizations

2. **Core Feature Implementation**
   - Port enhanced conversion algorithms from 4.0 codebase
   - Implement formatting engine with 4.0 capabilities
   - Add GENERIC_ENTRY support
   - Create enhanced validation system

3. **Browser Integration Setup**
   - Create optimized web worker implementation
   - Set up performance monitoring infrastructure
   - Implement progressive enhancement strategies

### Phase 2: Advanced Features (4-6 weeks)
1. **Performance Optimization**
   - Implement intelligent caching strategies
   - Add batch processing capabilities
   - Optimize memory management for browser environments
   - Create performance benchmarking suite

2. **Enhanced Validation**
   - Implement detailed error reporting system
   - Add null safety validation (4.0+ improvement)
   - Create formatting validation rules
   - Build comprehensive test coverage

3. **Framework Integration**
   - Create React components with 4.0 features
   - Build Vue.js integration examples
   - Develop Angular compatibility layer
   - Test cross-framework compatibility

### Phase 3: Production Readiness (4-6 weeks)
1. **Quality Assurance**
   - Comprehensive browser compatibility testing
   - Performance regression testing
   - Security audit and validation
   - Documentation completion

2. **Deployment Preparation**
   - NPM package optimization
   - CDN distribution setup
   - Version management strategy
   - Migration guide creation

3. **Developer Tools**
   - Debug tools and utilities
   - Performance profiling tools
   - Template validation utilities
   - Development environment setup

### Phase 4: Advanced Optimizations (2-4 weeks)
1. **WASM Integration**
   - Hybrid WASM/JavaScript implementation
   - Performance-critical operation optimization
   - Fallback strategy implementation
   - Cross-platform testing

2. **Advanced Features**
   - Real-time collaboration features
   - Offline support with service workers
   - Advanced caching strategies
   - Progressive web app capabilities

## Conclusion

The WebTemplate 4.0 branch provides an excellent foundation for JavaScript/TypeScript conversion, offering:

### Key Advantages for JavaScript Implementation:

- **Modern Architecture**: Java 17 and Kotlin 1.9.20 provide enhanced compilation targets and optimization opportunities
- **Enhanced Features**: Formatting engine, GENERIC_ENTRY support, and path filtering add significant value for web applications
- **Performance Optimizations**: Advanced caching, validation improvements, and memory management enhance browser performance
- **Developer Experience**: Better error handling, null safety, and TypeScript integration improve development productivity
- **Future-Proof Design**: Modern language features and architecture ensure long-term maintainability

### Recommended Implementation Strategy:

1. **Start with Enhanced Kotlin/JS**: Leverage the IR backend for optimal JavaScript output with 4.0 features
2. **Implement Progressive Enhancement**: Use feature detection to provide optimal experience across browsers
3. **Focus on Performance**: Utilize 4.0 optimizations for caching, validation, and batch processing
4. **Build Comprehensive Tooling**: Create development tools, testing frameworks, and deployment pipelines

The 4.0 branch's modern architecture and enhanced feature set make it an ideal foundation for sophisticated JavaScript implementations that can effectively bring OpenEHR template processing capabilities to browser-based healthcare applications while maintaining the performance, reliability, and feature richness of the server-side implementation.