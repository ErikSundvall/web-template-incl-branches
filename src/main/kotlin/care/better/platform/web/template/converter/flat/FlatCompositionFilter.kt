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

package care.better.platform.web.template.converter.flat

import care.better.platform.web.template.converter.WebTemplatePathSegment

/**
 * @author Bian Klančnik
 * @since 4.0.5
 *
 * Instance of [FlatCompositionFilter] that filters the composition in FLAT format by the provided path.
 */
class FlatCompositionFilter {

    fun pruneFlatCompositionToPath(root: MutableMap<String, String>, path: String) {
        val parsedPath = WebTemplatePathSegment.fromPathString(path)
        root.entries.removeIf { shouldRemove(parsedPath, it) }
    }

    private fun shouldRemove(parsedPath: List<WebTemplatePathSegment>, entry: Map.Entry<String, String>): Boolean {
        val compositionNodes = entry.key.split("/")
        val lastNode = compositionNodes.last()
        var index = 0

        for (node in compositionNodes) {
            if (index == parsedPath.size) {
                return false
            }

            val nodePath = WebTemplatePathSegment.fromString(node)

            // Check if node is a leaf
            if (parsedPath[index].attribute != null) {
                return !checkSegmentMatches(parsedPath[index], nodePath)
            }

            if (parsedPath[index].key != nodePath.key) {
                return true
            }

            if (lastNode == node) {
                return !checkLastNodeMatches(parsedPath[index], nodePath)
            }

            // Check if indexes are present and if they match
            if (nodePath.index != null) {
                when {
                    parsedPath[index].index == null -> {
                        index++
                        continue
                    }

                    nodePath.index != parsedPath[index].index -> {
                        return true
                    }
                }
            }
            index++
        }
        return false
    }

    private fun checkSegmentMatches(path: WebTemplatePathSegment, node: WebTemplatePathSegment): Boolean {
        if (path.index == null) {
            return path.key == node.key && path.attribute == node.attribute
        }
        return path.key == node.key && path.index == node.index && path.attribute == node.attribute
    }

    private fun checkLastNodeMatches(path: WebTemplatePathSegment, node: WebTemplatePathSegment): Boolean {
        return path.key == node.key &&
                (path.index == null || path.index == node.index) &&
                (path.attribute == null || path.attribute == node.attribute)
    }
}