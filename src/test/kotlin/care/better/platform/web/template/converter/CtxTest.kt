package care.better.platform.web.template.converter

import care.better.platform.web.template.WebTemplate
import care.better.platform.web.template.abstraction.AbstractWebTemplateTest
import care.better.platform.web.template.builder.WebTemplateBuilder
import care.better.platform.web.template.builder.context.WebTemplateBuilderContext
import care.better.platform.web.template.converter.raw.context.ConversionContext
import com.fasterxml.jackson.databind.node.ObjectNode
import org.junit.jupiter.api.Test
import org.openehr.rm.composition.Composition
import java.io.IOException
import jakarta.xml.bind.JAXBException

class CtxTest : AbstractWebTemplateTest() {
    @Test
    @Throws(IOException::class, JAXBException::class)
    fun testNullValue() {
        val templateName = "/convert/templates/older/Demo Vitals.opt"
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate(templateName), WebTemplateBuilderContext("en"))
        val structuredComposition: ObjectNode = getObjectMapper().readTree(getJson("/convert/compositions/null_ctx_value.json")) as ObjectNode
        webTemplate.convertFromStructuredToRaw<Composition>(structuredComposition, ConversionContext.create().build())
        // Just testing that it does not fail
    }
}
