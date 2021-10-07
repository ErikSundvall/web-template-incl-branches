package care.better.platform.web.template.converter.raw.factory.leaf

import care.better.platform.template.AmAttribute
import care.better.platform.template.AmNode
import care.better.platform.web.template.abstraction.AbstractWebTemplateTest
import care.better.platform.web.template.converter.WebTemplatePath
import care.better.platform.web.template.converter.exceptions.ConversionException
import care.better.platform.web.template.converter.raw.context.ConversionContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openehr.am.aom.CDateTime
import org.openehr.am.aom.CPrimitiveObject
import org.openehr.rm.datatypes.DvDateTime
import java.util.*

/**
 * @author Matic Ribic
 */
class DvDateTimeFactoryTest : AbstractWebTemplateTest() {
    private lateinit var dvDateTimeFactory: DvDateTimeFactory
    private lateinit var conversionContext: ConversionContext
    private lateinit var attributeDto: AttributeDto
    private lateinit var webTemplatePath: WebTemplatePath
    private lateinit var dvDateTime: DvDateTime

    companion object {
        private lateinit var defaultTimeZone: TimeZone

        @BeforeAll
        @JvmStatic
        @Suppress("unused")
        internal fun init() {
            defaultTimeZone = TimeZone.getDefault()
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Ljubljana"))
        }

        @AfterAll
        @JvmStatic
        @Suppress("unused")
        internal fun tearDown() {
            TimeZone.setDefault(defaultTimeZone)
        }
    }

    @BeforeEach
    fun setUp() {
        dvDateTimeFactory = DvDateTimeFactory
        conversionContext = ConversionContext.create().withLanguage("en").withTerritory("GB").withComposerName("Test").withStrictMode().build()
        attributeDto = AttributeDto.ofBlank()
        webTemplatePath = WebTemplatePath.forBlankPath()
        dvDateTime = DvDateTime()
    }

    @Test
    fun fullDateTimeUsingFullPatternWithTimeZone() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SSZ"
        val dateTime = "2021-01-01T10:00:00Z"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10:00:00Z")
    }

    @Test
    fun dateTimeWithoutTimeZoneUsingFullPatternWithTimeZone() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SSZ"
        val dateTime = "2021-01-01T10:00:00"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10:00:00+01:00")
    }

    @Test
    fun dateTimeWithoutMinSecUsingFullPatternWithTimeZone() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SSZ"
        val dateTime = "2021-01-01T10Z"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10:00:00Z")
    }

    @Test
    fun dateTimeWithoutMinSecAndTimeZoneUsingFullPatternWithTimeZone() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SSZ"
        val dateTime = "2021-01-01T10"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10:00:00+01:00")
    }

    @Test
    fun dateWithoutTimeZoneUsingFullPatternWithTimeZone() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SSZ"
        val dateTime = "2021-01-01"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T00:00:00+01:00")
    }

    @Test
    fun fullDateTimeUsingLocalFullPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SS"
        val dateTime = "2021-01-01T10:00:00Z"

        // when
        handleField(dateTime, pattern)

        // then
        // todo shouldn't be 2021-01-01T11:00:00
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10:00:00Z")
    }

    @Test
    fun dateTimeWithoutTimeZoneUsingLocalFullPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SS"
        val dateTime = "2021-01-01T10:00:00"

        // when
        handleField(dateTime, pattern)

        // then
        // todo shouldn't be 2021-01-01T10:00:00
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10:00:00+01:00")
    }

    @Test
    fun dateTimeWithoutMinSecUsingLocalFullPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SS"
        val dateTime = "2021-01-01T10Z"

        // when
        handleField(dateTime, pattern)

        // then
        // todo shouldn't be 2021-01-01T11:00:00
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10:00:00Z")
    }

    @Test
    fun dateTimeWithoutMinSecAndTimeZoneUsingLocalFullPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SS"
        val dateTime = "2021-01-01T10"

        // when
        handleField(dateTime, pattern)

        // then
        // todo shouldn't be 2021-01-01T10:00:00
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10:00:00+01:00")
    }

    @Test
    fun dateWithoutTimeZoneUsingLocalFullPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:MM:SS"
        val dateTime = "2021-01-01"

        // when
        handleField(dateTime, pattern)

        // then
        // todo shouldn't be 2021-01-01T00:00:00
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T00:00:00+01:00")
    }

    @Test
    fun fullDateTimeUsingOptionalMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:??:??"
        val dateTime = "2021-01-01T10:00:00Z"

        // when
        assertThatThrownBy{handleField(dateTime, pattern)}
                // then
                // todo shouldn't be 2021-01-01T11:00:00
                .isInstanceOf(ConversionException::class.java)
    }

    @Test
    fun dateTimeWithoutTimeZoneUsingOptionalMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:??:??"
        val dateTime = "2021-01-01T10:00:00"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10:00:00")
    }

    @Test
    fun dateTimeWithoutMinSecUsingOptionalMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:??:??"
        val dateTime = "2021-01-01T10Z"

        // when
        assertThatThrownBy{handleField(dateTime, pattern)}
                // then
                // todo shouldn't be 2021-01-01T11
                .isInstanceOf(ConversionException::class.java)
    }

    @Test
    fun dateTimeWithoutMinSecAndTimeZoneUsingOptionalMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:??:??"
        val dateTime = "2021-01-01T10"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10")
    }

    @Test
    fun dateWithoutTimeZoneUsingOptionalMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:??:??"
        val dateTime = "2021-01-01"

        // when
        handleField(dateTime, pattern)

        // then
        // todo shouldn't be 2021-01-01T00
        assertThat(dvDateTime.value).isEqualTo("2021-01-01")
    }

    @Test
    fun fullDateTimeUsingNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:XX:XX"
        val dateTime = "2021-01-01T10:00:00Z"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T11")
    }

    @Test
    fun dateTimeWithoutTimeZoneUsingNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:XX:XX"
        val dateTime = "2021-01-01T10:00:00"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10")
    }

    @Test
    fun dateTimeWithoutMinSecUsingNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:XX:XX"
        val dateTime = "2021-01-01T10Z"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T11")
    }

    @Test
    fun dateTimeWithoutMinSecAndTimeZoneUsingNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:XX:XX"
        val dateTime = "2021-01-01T10"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10")
    }

    @Test
    fun dateWithoutTimeZoneUsingNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddTHH:XX:XX"
        val dateTime = "2021-01-01"

        // when
        handleField(dateTime, pattern)

        // then
        // todo shouldn't be 2021-01-01T00
        assertThat(dvDateTime.value).isEqualTo("2021-01-01")
    }

    @Test
    fun fullDateTimeUsingOptionalHourAndNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddT??:XX:XX"
        val dateTime = "2021-01-01T10:00:00Z"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T11")
    }

    @Test
    fun dateTimeWithoutTimeZoneUsingOptionalHourAndNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddT??:XX:XX"
        val dateTime = "2021-01-01T10:00:00"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10")
    }

    @Test
    fun dateTimeWithoutMinSecUsingOptionalHourAndNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddT??:XX:XX"
        val dateTime = "2021-01-01T10Z"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T11")
    }

    @Test
    fun dateTimeWithoutMinSecAndTimeZoneUsingOptionalHourAndNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddT??:XX:XX"
        val dateTime = "2021-01-01T10"

        // when
        handleField(dateTime, pattern)

        // then
        assertThat(dvDateTime.value).isEqualTo("2021-01-01T10")
    }

    @Test
    fun dateWithoutTimeZoneUsingOptionalHourAndNotAllowedMinSecPattern() {
        // given
        val pattern = "yyyy-mm-ddT??:XX:XX"
        val dateTime = "2021-01-01"

        // when
        handleField(dateTime, pattern)

        // then
        // todo shouldn't be 2021-01-01T00
        assertThat(dvDateTime.value).isEqualTo("2021-01-01")
    }

    private fun handleField(dateTime: String, pattern: String) =
            dvDateTimeFactory.handleField(conversionContext, getAmNode(pattern), attributeDto, dvDateTime, getTextNode(dateTime), webTemplatePath)

    private fun getAmNode(pattern: String): AmNode {
        // todo actual case
        val cPrimitiveObject = CPrimitiveObject()
        cPrimitiveObject.rmTypeName = "DATE_TIME"
        val cDateTime = CDateTime()
        cDateTime.pattern = pattern
        cPrimitiveObject.item = cDateTime
        val amNode = AmNode(null, "ignored")
        amNode.attributes["value"] = AmAttribute(null, listOf(AmNode(cPrimitiveObject, null)))
        return amNode
    }

    private fun getTextNode(dateTime: String) = getObjectMapper().createObjectNode().textNode(dateTime)
}
