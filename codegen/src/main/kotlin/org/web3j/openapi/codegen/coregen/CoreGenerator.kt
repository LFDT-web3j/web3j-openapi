/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.openapi.codegen.coregen

import mu.KLogging
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import org.web3j.openapi.codegen.AbstractGenerator
import org.web3j.openapi.codegen.common.Import
import org.web3j.openapi.codegen.common.Tag
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.coregen.subgenerators.CoreApiGenerator
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils.generateFromTemplate
import org.web3j.openapi.codegen.utils.TemplateUtils.mustacheTemplate
import java.io.FileNotFoundException
import java.nio.file.Paths

class CoreGenerator(
    configuration: GeneratorConfiguration,
) : AbstractGenerator(
        configuration,
    ) {
    init {
        context["contractsConfiguration"] = configuration.contracts
        context["apiImports"] = getApiImports()
        context["tags"] = getTags()
        context["contextPath"] = configuration.contextPath
        context["projectName"] = configuration.sanitizedProjectName.capitalize()
        context["version"] = configuration.version
    }

    override fun generate() {
        if (configuration.contracts.isEmpty()) throw FileNotFoundException("No contracts found!")
        val folderPath = CopyUtils.createTree(configuration.outputDir, packageDir, "core")
        copySources(folderPath)

        configuration.contracts.forEach {
            logger.debug("Generating ${it.contractDetails.capitalizedContractName} Open API folders and files")
            CoreApiGenerator(
                configuration.packageName,
                folderPath =
                    Paths
                        .get(
                            folderPath,
                            it.contractDetails.lowerCaseContractName,
                        ).toString(),
                contractDetails = it.contractDetails,
            ).generate()
        }
    }

    private fun getTags(): List<Tag> {
        val tags = mutableListOf<Tag>()
        configuration.contracts.forEach {
            tags.add(
                Tag(
                    "${it.contractDetails.capitalizedContractName} Methods",
                    "List ${it.contractDetails.capitalizedContractName} method's calls",
                ),
            )
            tags.add(
                Tag(
                    "${it.contractDetails.capitalizedContractName} Events",
                    "List ${it.contractDetails.capitalizedContractName} event's calls",
                ),
            )
        }
        return tags.also {
            it.ifNotEmpty { last().lastCharacter = "" }
        }
    }

    private fun getApiImports(): List<Import> =
        configuration.contracts.map {
            Import(
                "import ${configuration.packageName}.core.${it.contractDetails.lowerCaseContractName}.${it.contractDetails.capitalizedContractName}",
            )
        }

    private fun copySources(folderPath: String) {
        generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = mustacheTemplate("core/src/ContractsApi.mustache"),
            name = "${configuration.sanitizedProjectName.capitalize()}Api.kt",
        )
        generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = mustacheTemplate("core/src/GeneratedContractsResource.mustache"),
            name = "${configuration.sanitizedProjectName.capitalize()}Resource.kt",
        )
    }

    companion object : KLogging()
}
