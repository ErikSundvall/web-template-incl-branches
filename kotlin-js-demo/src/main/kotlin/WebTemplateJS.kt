/*
 * Kotlin/JS Demo Implementation of WebTemplate Core Functionality
 * This demonstrates how the main WebTemplate library could be compiled to JavaScript
 */

package care.better.platform.web.template.js

import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 * Simplified conversion context for JavaScript environments
 */
@Serializable
data class ConversionContextJS(
    val language: String = "en",
    val territory: String = "US", 
    val composerName: String = "Web User"
)

/**
 * Simplified WebTemplate node for JavaScript
 */
@Serializable
data class WebTemplateNodeJS(
    val jsonId: String,
    val name: String,
    val rmType: String,
    val path: String,
    val children: List<WebTemplateNodeJS> = emptyList()
)

/**
 * Main WebTemplate class adapted for JavaScript/Browser environments
 * This is a simplified version demonstrating the conversion concepts
 */
@JsExport
class WebTemplateJS(
    private val templateId: String,
    private val tree: WebTemplateNodeJS,
    private val defaultLanguage: String = "en"
) {
    
    companion object {
        private val json = Json { 
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
    
    /**
     * Convert flat key-value data to structured JSON format
     * This is the core conversion that would be most useful in browsers
     */
    fun convertFromFlatToStructured(
        flatData: Map<String, String>,
        context: ConversionContextJS = ConversionContextJS()
    ): String {
        val structured = mutableMapOf<String, Any?>()
        
        // Process each flat key-value pair
        flatData.forEach { (path, value) ->
            when {
                path.startsWith("ctx/") -> {
                    // Handle context data
                    processContextData(structured, path, value, context)
                }
                path.contains("|") -> {
                    // Handle typed values (e.g., "temperature|magnitude", "temperature|unit")
                    processTypedValue(structured, path, value)
                }
                else -> {
                    // Handle simple path values
                    processSimpleValue(structured, path, value)
                }
            }
        }
        
        return json.encodeToString(structured)
    }
    
    /**
     * Convert structured JSON back to flat format
     */
    fun convertFromStructuredToFlat(structuredJson: String): Map<String, String> {
        val structured = json.parseToJsonElement(structuredJson).jsonObject
        val flat = mutableMapOf<String, String>()
        
        flattenJsonObject(structured, "", flat)
        
        return flat
    }
    
    /**
     * Validate flat data against template constraints
     */
    fun validateFlatData(flatData: Map<String, String>): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        // Basic validation examples
        flatData.forEach { (path, value) ->
            when {
                path.contains("temperature|magnitude") -> {
                    val temp = value.toDoubleOrNull()
                    if (temp == null) {
                        errors.add("Temperature magnitude must be a number: $path")
                    } else if (temp < 30 || temp > 45) {
                        warnings.add("Temperature $temp°C seems unusual: $path")
                    }
                }
                path.contains("temperature|unit") -> {
                    if (value !in listOf("°C", "°F")) {
                        errors.add("Invalid temperature unit '$value': $path")
                    }
                }
                path.startsWith("ctx/language") -> {
                    if (value.length != 2) {
                        warnings.add("Language code should be 2 characters: $value")
                    }
                }
            }
        }
        
        return ValidationResult(
            valid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
    
    /**
     * Find template node by path
     */
    fun findNode(path: String): WebTemplateNodeJS? {
        return findNodeRecursive(tree, path.split("/"))
    }
    
    // Private helper methods
    
    private fun processContextData(
        structured: MutableMap<String, Any?>,
        path: String,
        value: String,
        context: ConversionContextJS
    ) {
        val contextKey = path.substringAfter("ctx/")
        val contextMap = structured.getOrPut("_context") { mutableMapOf<String, Any?>() } as MutableMap<String, Any?>
        contextMap[contextKey] = value
    }
    
    private fun processTypedValue(structured: MutableMap<String, Any?>, path: String, value: String) {
        val parts = path.split("|")
        val basePath = parts[0]
        val attribute = parts[1]
        
        val pathParts = basePath.split("/")
        var current = structured
        
        // Navigate/create the nested structure
        for (i in 0 until pathParts.size - 1) {
            val part = pathParts[i]
            if (part.isNotEmpty()) {
                current = current.getOrPut(part) { mutableMapOf<String, Any?>() } as MutableMap<String, Any?>
            }
        }
        
        val finalKey = pathParts.last()
        val valueMap = current.getOrPut(finalKey) { mutableMapOf<String, Any?>() } as MutableMap<String, Any?>
        
        // Convert value based on attribute type
        valueMap[attribute] = when (attribute) {
            "magnitude" -> value.toDoubleOrNull() ?: value
            "code" -> value
            "value", "unit" -> value
            else -> value
        }
    }
    
    private fun processSimpleValue(structured: MutableMap<String, Any?>, path: String, value: String) {
        val pathParts = path.split("/")
        var current = structured
        
        for (i in 0 until pathParts.size - 1) {
            val part = pathParts[i]
            if (part.isNotEmpty()) {
                current = current.getOrPut(part) { mutableMapOf<String, Any?>() } as MutableMap<String, Any?>
            }
        }
        
        current[pathParts.last()] = value
    }
    
    private fun flattenJsonObject(obj: JsonObject, prefix: String, result: MutableMap<String, String>) {
        obj.forEach { (key, value) ->
            val newKey = if (prefix.isEmpty()) key else "$prefix/$key"
            
            when (value) {
                is JsonObject -> flattenJsonObject(value, newKey, result)
                is JsonArray -> {
                    value.forEachIndexed { index, item ->
                        if (item is JsonObject) {
                            flattenJsonObject(item, "$newKey:$index", result)
                        } else {
                            result["$newKey:$index"] = item.toString().trim('"')
                        }
                    }
                }
                else -> result[newKey] = value.toString().trim('"')
            }
        }
    }
    
    private fun findNodeRecursive(node: WebTemplateNodeJS, pathParts: List<String>): WebTemplateNodeJS? {
        if (pathParts.isEmpty()) return node
        
        val nextPart = pathParts.first()
        val remainingParts = pathParts.drop(1)
        
        val childNode = node.children.find { it.jsonId == nextPart || it.name == nextPart }
        return childNode?.let { findNodeRecursive(it, remainingParts) }
    }
}

/**
 * Validation result for template data
 */
@Serializable
@JsExport
data class ValidationResult(
    val valid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)

/**
 * Factory for creating WebTemplate instances from template definitions
 */
@JsExport
object WebTemplateFactoryJS {
    
    /**
     * Create a WebTemplate from a JSON template definition
     */
    fun fromJsonTemplate(templateJson: String): WebTemplateJS {
        val json = Json { ignoreUnknownKeys = true }
        val templateData = json.parseToJsonElement(templateJson).jsonObject
        
        val templateId = templateData["templateId"]?.jsonPrimitive?.content ?: "unknown"
        val tree = parseTreeFromJson(templateData["tree"]?.jsonObject ?: JsonObject(emptyMap()))
        val defaultLanguage = templateData["defaultLanguage"]?.jsonPrimitive?.content ?: "en"
        
        return WebTemplateJS(templateId, tree, defaultLanguage)
    }
    
    /**
     * Create a simple demo template for testing
     */
    fun createDemoTemplate(): WebTemplateJS {
        val temperatureNode = WebTemplateNodeJS(
            jsonId = "temperature",
            name = "Body Temperature",
            rmType = "DV_QUANTITY",
            path = "/vitals/temperature"
        )
        
        val vitalsNode = WebTemplateNodeJS(
            jsonId = "vitals",
            name = "Vital Signs",
            rmType = "OBSERVATION",
            path = "/vitals",
            children = listOf(temperatureNode)
        )
        
        val rootNode = WebTemplateNodeJS(
            jsonId = "vital_signs_encounter",
            name = "Vital Signs Encounter",
            rmType = "COMPOSITION",
            path = "/",
            children = listOf(vitalsNode)
        )
        
        return WebTemplateJS("demo-vitals-v1", rootNode, "en")
    }
    
    private fun parseTreeFromJson(treeJson: JsonObject): WebTemplateNodeJS {
        return WebTemplateNodeJS(
            jsonId = treeJson["jsonId"]?.jsonPrimitive?.content ?: "",
            name = treeJson["name"]?.jsonPrimitive?.content ?: "",
            rmType = treeJson["rmType"]?.jsonPrimitive?.content ?: "",
            path = treeJson["path"]?.jsonPrimitive?.content ?: "",
            children = treeJson["children"]?.jsonArray?.map { 
                parseTreeFromJson(it.jsonObject) 
            } ?: emptyList()
        )
    }
}