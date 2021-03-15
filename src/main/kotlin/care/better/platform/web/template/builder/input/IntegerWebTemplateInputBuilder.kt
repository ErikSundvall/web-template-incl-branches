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

package care.better.platform.web.template.builder.input

import care.better.platform.template.AmNode
import care.better.platform.template.AmUtils.getPrimitiveItem
import care.better.platform.web.template.builder.context.WebTemplateBuilderContext
import care.better.platform.web.template.builder.model.WebTemplateInputType
import care.better.platform.web.template.builder.model.WebTemplateNode
import care.better.platform.web.template.builder.model.input.WebTemplateCodedValue
import care.better.platform.web.template.builder.model.input.WebTemplateInput
import care.better.platform.web.template.builder.model.input.WebTemplateValidation
import care.better.platform.web.template.builder.model.input.range.WebTemplateValidationIntegerRange
import care.better.platform.web.template.builder.utils.WebTemplateBuilderUtils
import org.openehr.am.aom.CInteger
import org.openehr.rm.datatypes.DvCount

/**
 * @author Bostjan Lah
 * @author Primoz Delopst
 * @since 3.1.0
 */
internal object IntegerWebTemplateInputBuilder : WebTemplateInputBuilder<CInteger> {
    override fun build(amNode: AmNode, validator: CInteger?, context: WebTemplateBuilderContext): WebTemplateInput {
        val input = WebTemplateInput(WebTemplateInputType.INTEGER)
        if (validator != null) {
            val range = WebTemplateValidationIntegerRange(validator.range)
            if (!range.isEmpty()) {
                input.validation = WebTemplateValidation().apply { this.range = range }
            }
            if (validator.list.isNotEmpty()) {
                input.list.addAll(validator.list.map(WebTemplateCodedValue.toCodedValue()))
            }
            input.fixed = range.isFixed() || validator.list.size == 1
            if (validator.assumedValue != null) {
                input.defaultValue = validator.assumedValue.toString()
            }
        }
        val defaultValue = WebTemplateBuilderUtils.getDefaultValue(amNode, DvCount::class.java)
        if (defaultValue != null) {
            input.defaultValue = defaultValue.magnitude.toString()
        }
        return input
    }

    override fun build(node: WebTemplateNode, context: WebTemplateBuilderContext) {
        val cInteger = getPrimitiveItem(node.amNode, CInteger::class.java, "magnitude")
        val input = build(node.amNode, cInteger, context)
        if (cInteger?.assumedValue != null) {
            input.defaultValue = cInteger.assumedValue
        }
        val defaultValue = WebTemplateBuilderUtils.getDefaultValue(node.amNode, DvCount::class.java)
        if (defaultValue != null) {
            input.defaultValue = defaultValue.magnitude
        }
        node.inputs.add(input)
    }
}
