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
package org.web3j.openapi.codegen.coregen.subgenerators

import mu.KLogging
import org.web3j.openapi.codegen.utils.toDataClass
import org.web3j.protocol.core.methods.response.AbiDefinition.NamedType
import java.io.File

internal class CoreDeployModelGenerator(
    val packageName: String,
    private val contractName: String,
    val folderPath: String,
    val inputs: List<NamedType>,
) {
    fun generate() {
        val constructorFile =
            inputs.toDataClass(
                "$packageName.core.${contractName.lowercase()}.model",
                contractName,
                "DeployParameters",
                packageName,
                contractName,
            )
        logger.debug("Generating $contractName deploy parameters")
        constructorFile.writeTo(File(folderPath))
    }

    companion object : KLogging()
}
