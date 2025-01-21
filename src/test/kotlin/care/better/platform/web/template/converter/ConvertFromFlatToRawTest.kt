package care.better.platform.web.template.converter

import care.better.platform.json.jackson.openehr.OpenEhrObjectMapper
import care.better.platform.web.template.abstraction.AbstractWebTemplateTest
import care.better.platform.web.template.builder.WebTemplateBuilder
import care.better.platform.web.template.builder.context.WebTemplateBuilderContext
import care.better.platform.web.template.converter.raw.context.ConversionContext
import care.better.platform.web.template.converter.structured.FlatToStructuredConverter
import com.fasterxml.jackson.core.type.TypeReference
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.openehr.rm.composition.Composition

/**
 * @author Maja Razinger
 */
class ConvertFromFlatToRawTest : AbstractWebTemplateTest(){

    private val flatToStructuredConverter = FlatToStructuredConverter(OpenEhrObjectMapper())
    private val openEhrObjectMapper = OpenEhrObjectMapper()

    @Test
    fun testConvertFromFlatToStructured() {
        val templateName = "/convert/compositions/AtemfrequenzTemplate.xml"
        val webTemplate = WebTemplateBuilder.buildNonNull(getTemplate(templateName), WebTemplateBuilderContext("de"))
        val flatCompositionString = IOUtils.toString(
            ConvertFromFlatToRawTest::class.java.getResourceAsStream("/convert/compositions/flat_composition_with_raw.json"),
            Charsets.UTF_8
        )

        val flatCompositionMap: Map<String, Any> = openEhrObjectMapper.readValue(flatCompositionString, object : TypeReference<Map<String, Any>>() {})
        webTemplate.convertFromFlatToRaw<Composition>(flatCompositionMap, ConversionContext.create().build())
        // Just testing that it does not fail
    }
}