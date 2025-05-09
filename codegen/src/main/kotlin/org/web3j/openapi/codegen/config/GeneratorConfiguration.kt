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
package org.web3j.openapi.codegen.config

import org.jetbrains.kotlin.ir.backend.js.utils.sanitizeName
import org.web3j.abi.datatypes.Address

data class GeneratorConfiguration
    @JvmOverloads
    constructor(
        val projectName: String,
        var packageName: String,
        val outputDir: String,
        val contracts: List<ContractConfiguration>,
        val addressLength: Int = Address.DEFAULT_LENGTH / java.lang.Byte.SIZE,
        val contextPath: String,
        val version: String = VersionProvider.versionName,
        val sanitizedProjectName: String = sanitizeName(projectName),
        val withImplementations: Boolean = true,
    ) {
        val rootProjectName = sanitizedProjectName.lowercase().replace(' ', '-')

        init {
            packageName = packageName.lowercase()
        }
    }
