/* Copyright 2021 Better Ltd (www.better.care)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package care.better.platform.web.template.converter

import care.better.platform.json.jackson.openehr.OpenEhrObjectMapper
import care.better.platform.web.template.abstraction.AbstractWebTemplateTest
import care.better.platform.web.template.builder.WebTemplateBuilder
import care.better.platform.web.template.builder.context.WebTemplateBuilderContext
import care.better.platform.web.template.converter.raw.context.ConversionContext
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.openehr.rm.composition.Composition

/**
 * @author Maja Razinger
 * @since 4.0.6
 */
class ConversionRawTest : AbstractWebTemplateTest(){

    private val openEhrObjectMapper = OpenEhrObjectMapper()

    @Test
    fun testConvertFromFlatToStructured() {
        val templateName = "/convert/compositions/AtemfrequenzTemplate.xml"
        val webTemplate = WebTemplateBuilder.buildNonNull(getTemplate(templateName), WebTemplateBuilderContext("de"))
        val flatCompositionString = IOUtils.toString(
            ConversionRawTest::class.java.getResourceAsStream("/convert/compositions/flat_composition_with_raw.json"),
            Charsets.UTF_8
        )

        val flatCompositionMap: Map<String, Any> = openEhrObjectMapper.readValue(flatCompositionString, object : TypeReference<Map<String, Any>>() {})
        webTemplate.convertFromFlatToRaw<Composition>(flatCompositionMap, ConversionContext.create().withRawDataMapper(openEhrObjectMapper).build())
        // Just testing that it does not fail
    }

    @Test
    fun testConvertFromStructuredToRaw() {
        val templateName = "/convert/compositions/AtemfrequenzTemplate.xml"
        val webTemplate = WebTemplateBuilder.buildNonNull(getTemplate(templateName), WebTemplateBuilderContext("de"))
        val structuredCompositionString = IOUtils.toString(
            ConversionRawTest::class.java.getResourceAsStream("/convert/compositions/structured_composition_with_raw.json"),
            Charsets.UTF_8
        )

        val structuredCompositionNode = openEhrObjectMapper.readTree(structuredCompositionString) as ObjectNode
        webTemplate.convertFromStructuredToRaw<Composition>(structuredCompositionNode, ConversionContext.create().withRawDataMapper(openEhrObjectMapper).build())
        // Just testing that it does not fail
    }
}