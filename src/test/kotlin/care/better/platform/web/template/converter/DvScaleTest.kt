package care.better.platform.web.template.converter

import care.better.platform.web.template.WebTemplate
import care.better.platform.web.template.abstraction.AbstractWebTemplateTest
import care.better.platform.web.template.builder.WebTemplateBuilder
import care.better.platform.web.template.builder.context.WebTemplateBuilderContext
import care.better.platform.web.template.converter.raw.context.ConversionContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openehr.rm.composition.Composition

/**
 * @author Primoz Delopst
 */
class DvScaleTest  : AbstractWebTemplateTest() {

    @Test
    fun testDvScale() {
        val builderContext = WebTemplateBuilderContext("en")
        val webTemplate: WebTemplate = WebTemplateBuilder.buildNonNull(getTemplate("/convert/templates/scaleTest.opt"), builderContext)
        assertThat(webTemplate).isNotNull

        val firstScaleWebTemplateNode = webTemplate.findWebTemplateNode("scaletest/rm110test/any_event/scalewithvalue")
        val secondScaleWebTemplateNode = webTemplate.findWebTemplateNode("scaletest/rm110test/any_event/scalewithoutvalue")

        assertThat(firstScaleWebTemplateNode.rmType).isNotNull()
        assertThat(secondScaleWebTemplateNode.rmType).isNotNull()

        val flatMap: Map<String, Any> = mapOf(
            Pair("scaletest/rm110test/any_event/scalewithvalue|code", "at0005"),
            Pair("scaletest/rm110test/any_event/scalewithvalue|value", "Test"),
            Pair("scaletest/rm110test/any_event/scalewithvalue|scale", 22.2))

        val composition = webTemplate.convertFromFlatToRaw<Composition>(flatMap, ConversionContext.create().build())!!

        val compositionFlatMap = webTemplate.convertFromRawToFlat(composition)
        assertThat(compositionFlatMap).isNotEmpty()
        assertThat(compositionFlatMap.containsKey("scaletest/rm110test/any_event:0/scalewithvalue|code"))
        assertThat(compositionFlatMap.containsKey("scaletest/rm110test/any_event:0/scalewithvalue|value"))
        assertThat(compositionFlatMap.containsKey("scaletest/rm110test/any_event:0/scalewithvalue|scale"))
    }
}
