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

import care.better.platform.web.template.converter.WebTemplatePathSegment
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * @author Bian Klančnik
 * @since 4.0.5
 *
 * Instance of [StructuredCompositionFilter] that filters the composition in STRUCTURED format by the provided path.
 */
internal class StructuredCompositionFilter {

    fun pruneStructuredComposition(root: ObjectNode, path: String) {
        val compositionPath = WebTemplatePathSegment.fromPathString(path)
        val child = root.get(compositionPath[0].key) as? ObjectNode

        if (child != null) {
            pruneStructuredCompositionToPath(compositionPath, 1, child)
            if (child.isEmpty) {
                root.removeAll()
            }
        } else {
            root.removeAll()
        }
    }

    private fun pruneStructuredCompositionToPath(path: List<WebTemplatePathSegment>, index: Int, parent: ObjectNode) {
        if (index < path.size) {
            val parentIterator = parent.fieldNames()

            while (parentIterator.hasNext()) {
                val candidateName = parentIterator.next()
                if (shouldRemoveAttribute(candidateName, parent, path, index)) {
                    parentIterator.remove()
                }
            }
        }
    }

    private fun shouldRemoveAttribute(candidateName: String, parent: ObjectNode, path: List<WebTemplatePathSegment>, index: Int): Boolean {
        val candidate = parent.get(candidateName)
        if (candidateName == path[index].key) {
            if (path[index].index != null) {
                val pathNode = candidate.path(path[index].index!!)

                // If the child node for path index is present, continue pruning
                if (!pathNode.isEmpty) {
                    pruneStructuredCompositionToPath(path, index + 1, pathNode as ObjectNode)
                }

                // Remove empty arrays or arrays not indexed in the path
                var iteratorIndex = 0
                val childIterator = candidate.iterator()
                while (childIterator.hasNext()) {
                    val child = childIterator.next()
                    if (iteratorIndex != path[index].index) {
                        childIterator.remove()
                    }
                    // If path ends on a leaf, iterate through and remove all that don't match
                    if (path[index].attribute != null) {
                        removeLeaves(path[index].attribute!!, child)
                    }
                    iteratorIndex++
                    if (child.isContainerNode && child.isEmpty) {
                        childIterator.remove()
                    }
                }
            } else {
                // Prune all children and remove any that are empty
                val childIterator = candidate.iterator()
                while (childIterator.hasNext()) {
                    val child = childIterator.next()
                    if (child.isContainerNode) {
                        pruneStructuredCompositionToPath(path, index + 1, child as ObjectNode)
                    }
                    // If path ends on a leaf, iterate through and remove all that don't match
                    if (path[index].attribute != null) {
                        removeLeaves(path[index].attribute!!, child)
                    }
                    if (child.isContainerNode && child.isEmpty) {
                        childIterator.remove()
                    }
                }
            }
        } else {
            if (path[index].attribute != candidateName) {
                return true
            }
        }
        return candidate.isEmpty
    }

    private fun removeLeaves(attribute: String, node: JsonNode) {
        val iterator = node.fieldNames()
        while (iterator.hasNext()) {
            val leaf = iterator.next()
            if (leaf != attribute) {
                iterator.remove()
            }
        }
    }
}