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

import care.better.platform.web.template.WebTemplate
import care.better.platform.web.template.abstraction.AbstractWebTemplateTest
import care.better.platform.web.template.builder.WebTemplateBuilder
import care.better.platform.web.template.builder.context.WebTemplateBuilderContext
import care.better.platform.web.template.converter.raw.context.ConversionContext
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.collect.ImmutableList
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test
import org.openehr.rm.composition.Composition
import java.io.IOException
import javax.xml.bind.JAXBException

/**
 * @author Igor Popovic
 */
class DuplicatedTextInFlatFormatTest : AbstractWebTemplateTest() {

    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testNoDuplicateDvTextExists() {
        val builderContext = WebTemplateBuilderContext("en", ImmutableList.of("en", "sl"))
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/convert/templates/multiple_choice_data_value.opt"), builderContext)
        val context = ConversionContext.create().withLanguage("en").withTerritory("GB").withComposerName("Test").build()

        val root: ObjectNode = getObjectMapper().readTree(getJson("/convert/compositions/multiple_choice_data_value.json")) as ObjectNode
        val structuredComposition = webTemplate.convertFromStructuredToRaw<Composition>(root, context)!!
        val flatMap = webTemplate.convertFromRawToFlat(structuredComposition)

        assertThat(flatMap).contains(
                entry("coded_text_1/context/dv_coded_text/coded_text_value|code", "at0003"),
                entry("coded_text_1/context/dv_coded_text/coded_text_value|value", "Yes"),
                entry("coded_text_1/context/dv_coded_text/coded_text_value|terminology", "local"))

        assertThat(flatMap.keys).doesNotContain(
                "coded_text_1/context/dv_coded_text/text_value|code",
                "coded_text_1/context/dv_coded_text/text_value|value",
                "coded_text_1/context/dv_coded_text/text_value|terminology")
    }
}