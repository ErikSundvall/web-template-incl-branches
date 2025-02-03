/*
 * Copyright 2021 Better Ltd (www.better.care)
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
 *
 */

package care.better.platform.web.template.filter

import care.better.platform.web.template.WebTemplate
import care.better.platform.web.template.abstraction.AbstractWebTemplateTest
import care.better.platform.web.template.builder.WebTemplateBuilder
import care.better.platform.web.template.builder.context.WebTemplateBuilderContext
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.joda.JodaModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException
import javax.xml.bind.JAXBException

/**
 * @author Bian Klančnik
 */
class StructuredCompositionPruneTest : AbstractWebTemplateTest() {
    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testCompositionPruneWithSingleResult() {
        val builderContext = WebTemplateBuilderContext("sl")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/filter/Referral_mz_2_v2.xml"), builderContext)
        val path = "referral_document/context/personal_insurance/patient_insurance_identifier"

        val mapper = createObjectMapper().apply {
            this.registerModule(JodaModule())
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val structuredComposition: ObjectNode = mapper.readValue(getJson("/filter/referral_structured.json"), ObjectNode::class.java)

        webTemplate.pruneStructuredCompositionToPath(structuredComposition, path)

        assertThat(
                structuredComposition.path("referral_document").path("context").path(0).path("personal_insurance").path(0).path("patient_insurance_identifier")
                    .path(0)
                    .textValue()).isEqualTo("Ident. 74")
    }

    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testCompositionPruneWithMultipleResults() {
        val builderContext = WebTemplateBuilderContext("sl")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/filter/Referral_mz_2_v2.xml"), builderContext)
        val path = "referral_document/referral_request_slovenia/referral_request/scope"

        val mapper = createObjectMapper().apply {
            this.registerModule(JodaModule())
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val structuredComposition: ObjectNode = mapper.readValue(getJson("/filter/referral_structured.json"), ObjectNode::class.java)

        webTemplate.pruneStructuredCompositionToPath(structuredComposition, path)

        val convertedComposition = mapper.convertValue(structuredComposition, Map::class.java)

        val expected = mapper.readValue(getJson("/filter/multiple_results_referral_structured.json"), Map::class.java)

        assertThat(convertedComposition).isEqualTo(expected)
    }

    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testCompositionPruneWithIndexInPath() {
        val builderContext = WebTemplateBuilderContext("sl")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/filter/Referral_mz_2_v2.xml"), builderContext)
        val path = "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0/telecoms_type|code"

        val mapper = createObjectMapper().apply {
            this.registerModule(JodaModule())
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val structuredComposition: ObjectNode = mapper.readValue(getJson("/filter/referral_structured.json"), ObjectNode::class.java)

        webTemplate.pruneStructuredCompositionToPath(structuredComposition, path)

        assertEquals(
                1, structuredComposition.path("referral_document").path("referral_request_slovenia").path(0).path("referral_request").path(0)
            .path("patient_contact").path(0).path("telecom_details").path(0).path("telecoms").path(0).path("telecoms_type").path(0).size())
        assertThat(
                structuredComposition.path("referral_document").path("referral_request_slovenia").path(0).path("referral_request").path(0)
                    .path("patient_contact").path(0).path("telecom_details").path(0).path("telecoms").path(0).path("telecoms_type").path(0).path("|code")
                    .textValue()).isEqualTo("at0013")
    }

    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testCompositionPruneWithMultipleResultsIndexInPath() {
        val builderContext = WebTemplateBuilderContext("sl")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/filter/Referral_mz_2_v2.xml"), builderContext)
        val path = "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms/telecoms_type|code"

        val mapper = createObjectMapper().apply {
            this.registerModule(JodaModule())
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val structuredComposition: ObjectNode = mapper.readValue(getJson("/filter/referral_structured.json"), ObjectNode::class.java)

        webTemplate.pruneStructuredCompositionToPath(structuredComposition, path)

        assertEquals(
                2, structuredComposition.path("referral_document").path("referral_request_slovenia").path(0).path("referral_request").path(0)
            .path("patient_contact").path(0).path("telecom_details").path(0).path("telecoms").size())
        assertThat(
                structuredComposition.path("referral_document").path("referral_request_slovenia").path(0).path("referral_request").path(0)
                    .path("patient_contact").path(0).path("telecom_details").path(0).path("telecoms").path(0).path("telecoms_type").path(0).path("|code")
                    .textValue()).isEqualTo("at0013")
        assertThat(
                structuredComposition.path("referral_document").path("referral_request_slovenia").path(0).path("referral_request").path(0)
                    .path("patient_contact").path(0).path("telecom_details").path(0).path("telecoms").path(1).path("telecoms_type").path(0).path("|code")
                    .textValue()).isEqualTo("at0014")
    }

    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testCompositionPruneNoIndexInPath() {
        val builderContext = WebTemplateBuilderContext("sl")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/filter/Referral_mz_2_v2.xml"), builderContext)
        val path = "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms/telecoms_type|code"

        val mapper = createObjectMapper().apply {
            this.registerModule(JodaModule())
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val structuredComposition: ObjectNode = mapper.readValue(getJson("/filter/referral_structured.json"), ObjectNode::class.java)

        webTemplate.pruneStructuredCompositionToPath(structuredComposition, path)

        val convertedComposition = mapper.convertValue(structuredComposition, Map::class.java)

        val expected = mapper.readValue(getJson("/filter/no_index_path_referral_structured.json"), Map::class.java)

        assertThat(convertedComposition).isEqualTo(expected)
    }

    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testCompositionPruneToLeafWithIndex() {
        val builderContext = WebTemplateBuilderContext("sl")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/filter/Referral_mz_2_v2.xml"), builderContext)
        val path = "referral_document/referral_request_slovenia/_other_participation:0|name"

        val mapper = createObjectMapper().apply {
            this.registerModule(JodaModule())
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val structuredComposition: ObjectNode = mapper.readValue(getJson("/filter/referral_structured.json"), ObjectNode::class.java)

        webTemplate.pruneStructuredCompositionToPath(structuredComposition, path)

        assertEquals(1, structuredComposition.path("referral_document").path("referral_request_slovenia").path(0).path("_other_participation").path(0).size())
        assertThat(
                structuredComposition.path("referral_document").path("referral_request_slovenia").path(0).path("_other_participation").path(0).path("|name")
                    .textValue()).isEqualTo("Dr. Marcus Johnson")
    }

    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testCompositionPruneWithMultipleResultsIndexOnPathEnd() {
        val builderContext = WebTemplateBuilderContext("sl")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/filter/Referral_mz_2_v2.xml"), builderContext)
        val path = "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0"

        val mapper = createObjectMapper().apply {
            this.registerModule(JodaModule())
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val structuredComposition: ObjectNode = mapper.readValue(getJson("/filter/referral_structured.json"), ObjectNode::class.java)

        webTemplate.pruneStructuredCompositionToPath(structuredComposition, path)

        val convertedComposition = mapper.convertValue(structuredComposition, Map::class.java)

        val expected = mapper.readValue(getJson("/filter/multiple_results_with_index_referral_structured.json"), Map::class.java)

        assertThat(convertedComposition).isEqualTo(expected)
    }

    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testCompositionPruneWithWrongPath() {
        val builderContext = WebTemplateBuilderContext("sl")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/filter/Referral_mz_2_v2.xml"), builderContext)
        val path = "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/abc"

        val mapper = createObjectMapper().apply {
            this.registerModule(JodaModule())
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val structuredComposition: ObjectNode = mapper.readValue(getJson("/filter/referral_structured.json"), ObjectNode::class.java)

        webTemplate.pruneStructuredCompositionToPath(structuredComposition, path)

        assertThat(structuredComposition).isEmpty()
    }
}