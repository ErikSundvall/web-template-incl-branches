/**
 * WebTemplate JavaScript Implementation
 * 
 * A vanilla JavaScript implementation of the WebTemplate functionality
 * for converting between flat and structured healthcare data formats.
 * 
 * @author WebTemplate JS Team
 * @version 1.0.0
 */

/**
 * Conversion context for healthcare data processing
 * @typedef {Object} ConversionContext
 * @property {string} language - Language code (e.g., 'en', 'sl', 'de')
 * @property {string} territory - Territory code (e.g., 'US', 'SI', 'DE')
 * @property {string} composerName - Name of the person creating the data
 */

/**
 * Validation result for healthcare data
 * @typedef {Object} ValidationResult
 * @property {boolean} valid - Whether the data is valid
 * @property {string[]} errors - Array of error messages
 * @property {string[]} warnings - Array of warning messages
 */

/**
 * WebTemplate node structure for template hierarchies
 * @typedef {Object} WebTemplateNode
 * @property {string} jsonId - JSON identifier for the node
 * @property {string} name - Human-readable name
 * @property {string} rmType - openEHR reference model type
 * @property {string} path - Path within the template
 * @property {WebTemplateNode[]} children - Child nodes
 */

/**
 * WebTemplateJS - Main class for healthcare data conversion
 * 
 * This class provides functionality to convert between different formats
 * commonly used in healthcare applications:
 * - Flat key-value pairs (form data)
 * - Structured JSON (hierarchical data)
 * - Raw openEHR canonical JSON
 */
class WebTemplateJS {
    /**
     * Constructor for WebTemplateJS
     * @param {string} templateId - Unique identifier for the template
     * @param {WebTemplateNode} tree - Template node hierarchy
     * @param {string} defaultLanguage - Default language code
     */
    constructor(templateId, tree, defaultLanguage = 'en') {
        this.templateId = templateId;
        this.tree = tree;
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Create a WebTemplateJS instance from a JSON template definition
     * @param {string|Object} templateJson - JSON template definition as string or object
     * @returns {WebTemplateJS} New WebTemplateJS instance
     */
    static fromJsonTemplate(templateJson) {
        let templateData;
        if (typeof templateJson === 'string') {
            templateData = JSON.parse(templateJson);
        } else {
            templateData = templateJson;
        }

        const templateId = templateData.templateId || 'unknown';
        const tree = WebTemplateJS._parseTreeFromJson(templateData.tree || {});
        const defaultLanguage = templateData.defaultLanguage || 'en';

        return new WebTemplateJS(templateId, tree, defaultLanguage);
    }

    /**
     * Create a demo template for testing purposes
     * @returns {WebTemplateJS} Demo WebTemplateJS instance
     */
    static createDemoTemplate() {
        const temperatureNode = {
            jsonId: 'temperature',
            name: 'Body Temperature',
            rmType: 'DV_QUANTITY',
            path: '/vitals/temperature',
            children: []
        };

        const vitalsNode = {
            jsonId: 'vitals',
            name: 'Vital Signs',
            rmType: 'OBSERVATION',
            path: '/vitals',
            children: [temperatureNode]
        };

        const rootNode = {
            jsonId: 'vital_signs_encounter',
            name: 'Vital Signs Encounter',
            rmType: 'COMPOSITION',
            path: '/',
            children: [vitalsNode]
        };

        return new WebTemplateJS('demo-vitals-v1', rootNode, 'en');
    }

    /**
     * Convert flat key-value data to structured JSON format
     * 
     * @param {Record<string, any>} flatData - Flat key-value pairs
     * @param {ConversionContext} context - Conversion context
     * @returns {string} Structured JSON string
     * 
     * @example
     * const flatData = {
     *   'vitals/temperature|magnitude': '37.5',
     *   'vitals/temperature|unit': '°C',
     *   'ctx/language': 'en'
     * };
     * const structured = webTemplate.convertFromFlatToStructured(flatData);
     */
    convertFromFlatToStructured(flatData, context = {}) {
        const structured = {};
        const conversionContext = {
            language: this.defaultLanguage,
            territory: 'US',
            composerName: 'Web User',
            ...context
        };

        // Process each flat key-value pair
        Object.entries(flatData).forEach(([path, value]) => {
            if (path.startsWith('ctx/')) {
                // Handle context data
                this._processContextData(structured, path, value, conversionContext);
            } else if (path.includes('|')) {
                // Handle typed values (e.g., "temperature|magnitude", "temperature|unit")
                this._processTypedValue(structured, path, value);
            } else {
                // Handle simple path values
                this._processSimpleValue(structured, path, value);
            }
        });

        return JSON.stringify(structured, null, 2);
    }

    /**
     * Convert structured JSON back to flat format
     * 
     * @param {string|Object} structuredJson - Structured JSON as string or object
     * @returns {Record<string, string>} Flat key-value pairs
     * 
     * @example
     * const structured = '{"vitals": {"temperature": {"magnitude": 37.5, "unit": "°C"}}}';
     * const flat = webTemplate.convertFromStructuredToFlat(structured);
     */
    convertFromStructuredToFlat(structuredJson) {
        let structured;
        if (typeof structuredJson === 'string') {
            structured = JSON.parse(structuredJson);
        } else {
            structured = structuredJson;
        }

        const flat = {};
        this._flattenObject(structured, '', flat);
        return flat;
    }

    /**
     * Validate flat data against template constraints
     * 
     * @param {Record<string, any>} flatData - Flat key-value pairs to validate
     * @returns {ValidationResult} Validation result with errors and warnings
     * 
     * @example
     * const validation = webTemplate.validateFlatData({
     *   'vitals/temperature|magnitude': '37.5',
     *   'vitals/temperature|unit': '°C'
     * });
     * if (!validation.valid) {
     *   console.log('Errors:', validation.errors);
     * }
     */
    validateFlatData(flatData) {
        const errors = [];
        const warnings = [];

        // Basic validation examples
        Object.entries(flatData).forEach(([path, value]) => {
            if (path.includes('temperature|magnitude')) {
                const temp = parseFloat(value);
                if (isNaN(temp)) {
                    errors.push(`Temperature magnitude must be a number: ${path}`);
                } else if (temp < 30 || temp > 45) {
                    warnings.push(`Temperature ${temp}°C seems unusual: ${path}`);
                }
            } else if (path.includes('temperature|unit')) {
                if (!['°C', '°F', 'K'].includes(value)) {
                    errors.push(`Invalid temperature unit '${value}': ${path}`);
                }
            } else if (path.startsWith('ctx/language')) {
                if (value.length !== 2) {
                    warnings.push(`Language code should be 2 characters: ${value}`);
                }
            }
        });

        return {
            valid: errors.length === 0,
            errors: errors,
            warnings: warnings
        };
    }

    /**
     * Find template node by path
     * @param {string} path - Path to search for
     * @returns {WebTemplateNode|null} Found node or null
     */
    findNode(path) {
        return this._findNodeRecursive(this.tree, path.split('/').filter(p => p));
    }

    /**
     * Convert to raw openEHR canonical JSON format
     * @param {Record<string, any>} flatData - Flat data to convert
     * @param {ConversionContext} context - Conversion context
     * @returns {string} Raw openEHR JSON string
     */
    convertToRawOpenEHR(flatData, context = {}) {
        const structured = JSON.parse(this.convertFromFlatToStructured(flatData, context));
        
        // Transform to canonical openEHR format
        const canonical = {
            '@type': 'COMPOSITION',
            'archetype_node_id': this.templateId,
            'name': {
                '@type': 'DV_TEXT',
                'value': structured._context?.composition_name || 'Health Data'
            },
            'language': {
                '@type': 'CODE_PHRASE',
                'terminology_id': {
                    '@type': 'TERMINOLOGY_ID',
                    'value': 'ISO_639-1'
                },
                'code_string': context.language || this.defaultLanguage
            },
            'territory': {
                '@type': 'CODE_PHRASE',
                'terminology_id': {
                    '@type': 'TERMINOLOGY_ID',
                    'value': 'ISO_3166-1'
                },
                'code_string': context.territory || 'US'
            },
            'composer': {
                '@type': 'PARTY_IDENTIFIED',
                'name': context.composerName || 'Web User'
            },
            'content': this._transformToOpenEHRContent(structured)
        };

        return JSON.stringify(canonical, null, 2);
    }

    // Private helper methods

    /**
     * Parse template tree from JSON
     * @private
     */
    static _parseTreeFromJson(treeJson) {
        return {
            jsonId: treeJson.jsonId || '',
            name: treeJson.name || '',
            rmType: treeJson.rmType || '',
            path: treeJson.path || '',
            children: (treeJson.children || []).map(child => 
                WebTemplateJS._parseTreeFromJson(child)
            )
        };
    }

    /**
     * Process context data
     * @private
     */
    _processContextData(structured, path, value, context) {
        const contextKey = path.substring(4); // Remove 'ctx/'
        if (!structured._context) {
            structured._context = {};
        }
        structured._context[contextKey] = value;
    }

    /**
     * Process typed values (with | separator)
     * @private
     */
    _processTypedValue(structured, path, value) {
        const parts = path.split('|');
        const basePath = parts[0];
        const attribute = parts[1];
        
        const pathParts = basePath.split('/').filter(p => p);
        let current = structured;
        
        // Navigate/create the nested structure
        for (let i = 0; i < pathParts.length - 1; i++) {
            const part = pathParts[i];
            if (!current[part]) {
                current[part] = {};
            }
            current = current[part];
        }
        
        const finalKey = pathParts[pathParts.length - 1];
        if (!current[finalKey]) {
            current[finalKey] = {};
        }
        
        // Convert value based on attribute type
        current[finalKey][attribute] = this._convertValueByAttribute(value, attribute);
    }

    /**
     * Process simple path values
     * @private
     */
    _processSimpleValue(structured, path, value) {
        const pathParts = path.split('/').filter(p => p);
        let current = structured;
        
        for (let i = 0; i < pathParts.length - 1; i++) {
            const part = pathParts[i];
            if (!current[part]) {
                current[part] = {};
            }
            current = current[part];
        }
        
        current[pathParts[pathParts.length - 1]] = value;
    }

    /**
     * Convert value based on attribute type
     * @private
     */
    _convertValueByAttribute(value, attribute) {
        switch (attribute) {
            case 'magnitude':
                const numValue = parseFloat(value);
                return isNaN(numValue) ? value : numValue;
            case 'code':
            case 'value':
            case 'unit':
                return value;
            default:
                return value;
        }
    }

    /**
     * Flatten object to flat key-value pairs
     * @private
     */
    _flattenObject(obj, prefix, result) {
        Object.entries(obj).forEach(([key, value]) => {
            const newKey = prefix ? `${prefix}/${key}` : key;
            
            if (key === '_context') {
                // Handle context specially
                Object.entries(value).forEach(([ctxKey, ctxValue]) => {
                    result[`ctx/${ctxKey}`] = String(ctxValue);
                });
            } else if (value && typeof value === 'object' && !Array.isArray(value)) {
                // Check if this is a typed object (has magnitude, unit, etc.)
                const hasTypedAttributes = Object.keys(value).some(k => 
                    ['magnitude', 'unit', 'code', 'value'].includes(k)
                );
                
                if (hasTypedAttributes) {
                    Object.entries(value).forEach(([attr, attrValue]) => {
                        result[`${newKey}|${attr}`] = String(attrValue);
                    });
                } else {
                    this._flattenObject(value, newKey, result);
                }
            } else {
                result[newKey] = String(value);
            }
        });
    }

    /**
     * Find node recursively
     * @private
     */
    _findNodeRecursive(node, pathParts) {
        if (pathParts.length === 0) return node;
        
        const nextPart = pathParts[0];
        const remainingParts = pathParts.slice(1);
        
        const childNode = node.children.find(child => 
            child.jsonId === nextPart || child.name === nextPart
        );
        
        return childNode ? this._findNodeRecursive(childNode, remainingParts) : null;
    }

    /**
     * Transform structured data to openEHR content format
     * @private
     */
    _transformToOpenEHRContent(structured) {
        const content = [];
        
        // Transform each top-level element (excluding _context)
        Object.entries(structured).forEach(([key, value]) => {
            if (key !== '_context' && value && typeof value === 'object') {
                content.push(this._transformToOpenEHRObservation(key, value));
            }
        });
        
        return content;
    }

    /**
     * Transform to openEHR observation format
     * @private
     */
    _transformToOpenEHRObservation(name, data) {
        return {
            '@type': 'OBSERVATION',
            'name': {
                '@type': 'DV_TEXT',
                'value': name.charAt(0).toUpperCase() + name.slice(1)
            },
            'data': {
                '@type': 'HISTORY',
                'events': [{
                    '@type': 'POINT_EVENT',
                    'data': {
                        '@type': 'ITEM_TREE',
                        'items': this._transformDataItems(data)
                    }
                }]
            }
        };
    }

    /**
     * Transform data items recursively
     * @private
     */
    _transformDataItems(data) {
        const items = [];
        
        Object.entries(data).forEach(([key, value]) => {
            if (value && typeof value === 'object' && value.magnitude !== undefined) {
                // This is a quantity
                items.push({
                    '@type': 'ELEMENT',
                    'name': {
                        '@type': 'DV_TEXT',
                        'value': key.charAt(0).toUpperCase() + key.slice(1)
                    },
                    'value': {
                        '@type': 'DV_QUANTITY',
                        'magnitude': value.magnitude,
                        'units': value.unit || value.units || ''
                    }
                });
            } else if (typeof value === 'string' || typeof value === 'number') {
                items.push({
                    '@type': 'ELEMENT',
                    'name': {
                        '@type': 'DV_TEXT',
                        'value': key.charAt(0).toUpperCase() + key.slice(1)
                    },
                    'value': {
                        '@type': 'DV_TEXT',
                        'value': String(value)
                    }
                });
            }
        });
        
        return items;
    }
}

// Export for both ES modules and global usage
if (typeof module !== 'undefined' && module.exports) {
    module.exports = WebTemplateJS;
}

if (typeof window !== 'undefined') {
    window.WebTemplateJS = WebTemplateJS;
}