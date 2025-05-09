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
package org.web3j.openapi.codegen.servergen.subgenerators

import mu.KLogging
import org.web3j.openapi.codegen.config.ContractDetails
import org.web3j.openapi.codegen.utils.TemplateUtils
import org.web3j.openapi.codegen.utils.getStructCallParameters
import java.io.File

class LifecycleImplGenerator(
    val packageName: String,
    val folderPath: String,
    val contractDetails: ContractDetails,
) {
    private val context = mutableMapOf<String, Any>()

    private val parameters: String by lazy {
        if (contractDetails.deployParameters == "()") {
            ""
        } else {
            var parameters = ""
            contractDetails.abiDefinitions
                .filter { it.type == "constructor" }
                .map { it.inputs }
                .first()
                .forEach { input ->
                    parameters +=
                        if (input.type == "tuple") {
                            ", ${getStructCallParameters(
                                contractDetails.contractName,
                                input,
                                "",
                                "parameters.${input.name}",
                            )}"
                        } else {
                            ", parameters.${input.name}"
                        }
                }
            parameters.removeSuffix(",")
        }
    }

    init {
        context["packageName"] = packageName
        context["decapitalizedContractName"] = contractDetails.decapitalizedContractName
        context["lowerCaseContractName"] = contractDetails.lowerCaseContractName
        context["capitalizedContractName"] = contractDetails.capitalizedContractName
        context["parameters"] = parameters
        context["deployParameters"] = contractDetails.deployParameters
    }

    fun generate() {
        File(folderPath).apply { mkdirs() }
        copySources()
    }

    private fun copySources() {
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("server/src/contractImpl/ContractLifecycleImpl.mustache"),
            name = "${contractDetails.capitalizedContractName}LifecycleImpl.kt",
        )
    }

    companion object : KLogging()
}
