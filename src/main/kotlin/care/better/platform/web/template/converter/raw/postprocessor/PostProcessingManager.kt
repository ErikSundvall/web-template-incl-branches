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

package care.better.platform.web.template.converter.raw.postprocessor

import care.better.openehr.rm.RmObject
import care.better.platform.template.AmNode
import care.better.platform.web.template.converter.WebTemplatePath
import care.better.platform.web.template.converter.raw.context.ConversionContext
import care.better.platform.web.template.converter.raw.context.RmVisitor
import java.util.*

/**
 * @author Primoz Delopst
 * @since 3.1.0
 *
 * Used when [RmObject] is not converted using depth first recursion.
 *
 * @constructor Creates a new instance of [PostProcessingManager]
 * @param conversionContext [ConversionContext]
 * @param additionalPostProcessors [Map] of additional [PostProcessor]
 * @param externalPostProcessors [Map] of external [PostProcessor]
 */
class PostProcessingManager @JvmOverloads constructor(
        private val conversionContext: ConversionContext,
        private val additionalPostProcessors: Map<Class<*>, PostProcessor<*>> = mapOf(),
        private val externalPostProcessors: Map<Class<*>, PostProcessor<*>> = mapOf()) {

    private val elementPostProcessors: MutableList<ElementPostProcessor<*>> = ArrayList<ElementPostProcessor<*>>()
    private val elementVisitors: IdentityHashMap<RmObject, ElementVisitor<RmObject>> = IdentityHashMap<RmObject, ElementVisitor<RmObject>>()


    @Suppress("UNCHECKED_CAST")
    fun <T> add(amNode: AmNode, rm: T, webTemplatePath: WebTemplatePath) {
        if (rm is RmObject) {
            if (!elementVisitors.containsKey(rm)) {
                conversionContext.rmVisitors[rm.javaClass]?.also {
                    elementVisitors[rm] = ElementVisitor(rm, webTemplatePath.toString(), it as RmVisitor<RmObject>)
                }
            }
        }

        addPostProcessor(amNode, rm, webTemplatePath, externalPostProcessors, true)
        if (!addPostProcessor(amNode, rm, webTemplatePath, additionalPostProcessors, false)) {
            addPostProcessor(amNode, rm, webTemplatePath, PostProcessDelegator.getPostProcessors(), false)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> addPostProcessor(
            amNode: AmNode,
            rm: T,
            webTemplatePath: WebTemplatePath,
            postProcessors: Map<Class<*>, PostProcessor<*>>,
            addMultiple: Boolean): Boolean {
        var added = false
        if (rm != null) {
            for ((key, value) in postProcessors) {
                if (key.isAssignableFrom(rm.javaClass)) {
                    val elementPostProcessor: ElementPostProcessor<T> = ElementPostProcessor(amNode, rm, webTemplatePath, value as PostProcessor<T>)
                    if (!elementPostProcessors.contains(elementPostProcessor)) {
                        elementPostProcessors.add(elementPostProcessor)
                    }
                    if (addMultiple)
                        added = true
                    else
                        break
                }
            }
        }
        return added
    }


    fun postProcess() {
        elementVisitors.values.forEach { it.visit() }
        val iterator: ListIterator<ElementPostProcessor<*>> = elementPostProcessors.listIterator(elementPostProcessors.size)
        while (iterator.hasPrevious()) {
            iterator.previous().postProcess()
        }
    }

    private inner class ElementPostProcessor<T> constructor(
            private val amNode: AmNode,
            private val element: T,
            private val webTemplatePath: WebTemplatePath,
            private val postProcessor: PostProcessor<T>) {

        fun postProcess() {
            postProcessor.postProcess(conversionContext, amNode, element, webTemplatePath)
        }

        override fun equals(other: Any?): Boolean =
            when {
                this === other -> true
                other == null || javaClass != other.javaClass -> false
                (other as ElementPostProcessor<*>).element != element -> false
                else -> other.postProcessor == postProcessor
            }

        override fun hashCode(): Int {
            var result = element.hashCode()
            result = 31 * result + postProcessor.hashCode()
            return result
        }

        override fun toString(): String {
            return StringJoiner(", ", ElementPostProcessor::class.java.simpleName + "[", "]")
                .add("element=$element")
                .add("postProcessor=$postProcessor")
                .toString()
        }
    }

    private inner class ElementVisitor<T : RmObject> constructor(
            private val element: T,
            private val webTemplatePath: String,
            private val rmVisitor: RmVisitor<T>) {

        fun visit() {
            rmVisitor.visit(element, webTemplatePath)
        }

        override fun equals(other: Any?): Boolean =
            when {
                this === other -> true
                other == null || javaClass != other.javaClass -> false
                else -> (other as ElementVisitor<*>).element == element
            }

        override fun hashCode(): Int = element.hashCode()
    }
}
