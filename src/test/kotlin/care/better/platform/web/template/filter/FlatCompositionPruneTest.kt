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
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.IOException
import jakarta.xml.bind.JAXBException

/**
 * @author Bian Klančnik
 */
class FlatCompositionPruneTest : AbstractWebTemplateTest() {
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

        val flatComposition: MutableMap<String, String> =
            mapper.readValue(getJson("/filter/referral_flat.json"), object : TypeReference<MutableMap<String, String>>() {})

        webTemplate.pruneFlatCompositionToPath(flatComposition, path)

        assertThat(flatComposition).isNotEmpty
        assertThat(flatComposition).isEqualTo(mapOf("referral_document/context/personal_insurance/patient_insurance_identifier" to "Ident. 74"))
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

        val flatComposition: MutableMap<String, String> =
            mapper.readValue(getJson("/filter/referral_flat.json"), object : TypeReference<MutableMap<String, String>>() {})

        webTemplate.pruneFlatCompositionToPath(flatComposition, path)

        assertThat(flatComposition).isNotEmpty
        assertThat(flatComposition).isEqualTo(
                mapOf(
                        "referral_document/referral_request_slovenia/referral_request/scope/scope_examination" to "false",
                        "referral_document/referral_request_slovenia/referral_request/scope/scope_treatment" to "false",
                        "referral_document/referral_request_slovenia/referral_request/scope/scope_specialist" to "false"))
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

        val flatComposition: MutableMap<String, String> =
            mapper.readValue(getJson("/filter/referral_flat.json"), object : TypeReference<MutableMap<String, String>>() {})

        webTemplate.pruneFlatCompositionToPath(flatComposition, path)

        assertThat(flatComposition).isNotEmpty
        assertThat(flatComposition).isEqualTo(
                mapOf(
                        "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0/telecoms_type|code" to
                                "at0013"))
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

        val flatComposition: MutableMap<String, String> =
            mapper.readValue(getJson("/filter/referral_flat.json"), object : TypeReference<MutableMap<String, String>>() {})

        webTemplate.pruneFlatCompositionToPath(flatComposition, path)

        assertThat(flatComposition).isNotEmpty
        assertThat(flatComposition).isEqualTo(
                mapOf(
                        "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0/telecoms_type|code" to
                                "at0013",
                        "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:1/telecoms_type|code" to
                                "at0014"))
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

        val flatComposition: MutableMap<String, String> =
            mapper.readValue(getJson("/filter/referral_flat.json"), object : TypeReference<MutableMap<String, String>>() {})

        webTemplate.pruneFlatCompositionToPath(flatComposition, path)

        assertThat(flatComposition).isNotEmpty
        assertThat(flatComposition).isEqualTo(mapOf("referral_document/referral_request_slovenia/_other_participation:0|name" to "Dr. Marcus Johnson"))
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

        val flatComposition: MutableMap<String, String> =
            mapper.readValue(getJson("/filter/referral_flat.json"), object : TypeReference<MutableMap<String, String>>() {})

        webTemplate.pruneFlatCompositionToPath(flatComposition, path)

        assertThat(flatComposition).isNotEmpty
        assertThat(flatComposition).isEqualTo(
                mapOf(
                        "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0/telecoms_type|code" to "at0013",
                        "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0/telecoms_type|value" to "Telephone",
                        "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0/telecoms_type|terminology" to "local",
                        "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0/structured_telecoms/country_code" to "Country code 10",
                        "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0/structured_telecoms/number" to "Number 68",
                        "referral_document/referral_request_slovenia/referral_request/patient_contact:0/telecom_details/telecoms:0/structured_telecoms/extension" to "Extension 23"))
    }

    @Test
    @Throws(JAXBException::class, IOException::class)
    fun testCompositionPruneWithWrongPath() {
        val builderContext = WebTemplateBuilderContext("sl")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/filter/Referral_mz_2_v2.xml"), builderContext)
        val path = "referral_document/referral_request_slovenia/referral_request/patient_contact/telecom_details/address/address_type/abc"

        val mapper = createObjectMapper().apply {
            this.registerModule(JodaModule())
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val flatComposition: MutableMap<String, String> =
            mapper.readValue(getJson("/filter/referral_flat.json"), object : TypeReference<MutableMap<String, String>>() {})

        webTemplate.pruneFlatCompositionToPath(flatComposition, path)

        assertThat(flatComposition).isEmpty()
    }
}
