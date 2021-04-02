package care.better.platform.web.template.converter.raw.factory.leaf

import care.better.platform.template.AmNode
import care.better.platform.web.template.converter.WebTemplatePath
import care.better.platform.web.template.converter.raw.context.ConversionContext
import com.fasterxml.jackson.databind.JsonNode
import org.openehr.base.basetypes.PartyRef

/**
 * @author Primoz Delopst
 */
internal object PartyRefFactory : RmObjectLeafNodeFactory<PartyRef>() {
    override fun createInstance(attributes: Set<AttributeDto>): PartyRef = PartyRef()

    override fun handleField(
            conversionContext: ConversionContext,
            amNode: AmNode,
            attribute: AttributeDto,
            rmObject: PartyRef,
            jsonNode: JsonNode,
            webTemplatePath: WebTemplatePath): Boolean =
        ObjectRefFactory.handleField(conversionContext, amNode, attribute, rmObject, jsonNode, webTemplatePath)
}
