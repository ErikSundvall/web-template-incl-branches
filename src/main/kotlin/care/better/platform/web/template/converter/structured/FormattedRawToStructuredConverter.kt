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

package care.better.platform.web.template.converter.structured

import care.better.openehr.rm.RmObject
import care.better.platform.web.template.converter.structured.mapper.RmObjectToStructuredMapperDelegator
import care.better.platform.web.template.converter.value.ValueConverter
import com.fasterxml.jackson.databind.JsonNode
import care.better.platform.web.template.builder.model.WebTemplateNode

/**
 * @author Primoz Delopst
 * @since 3.1.0
 *
 * Instance of [AbstractRawToStructuredConverter] that converts the RM object in RAW format to the RM object in STRUCTURED format with formatted values.
 *
 * @constructor Creates a new instance of [FormattedRawToStructuredConverter]
 * @param valueConverter [ValueConverter]
 */
internal class FormattedRawToStructuredConverter(private val valueConverter: ValueConverter) : AbstractRawToStructuredConverter() {
    override fun <R : RmObject> mapRmObject(webTemplateNode: WebTemplateNode, rmObject: R): JsonNode? =
        RmObjectToStructuredMapperDelegator.delegateFormatted(webTemplateNode, valueConverter, rmObject)
}
