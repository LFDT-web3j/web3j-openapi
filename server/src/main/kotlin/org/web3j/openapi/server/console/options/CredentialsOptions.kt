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
package org.web3j.openapi.server.console.options

import com.fasterxml.jackson.annotation.JsonProperty
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option

class CredentialsOptions {
    @Option(
        names = ["--private-key"],
        description = ["private key for signed transactions"],
    )
    var privateKey: String? = null

    @Mixin
    @JsonProperty("wallet")
    val walletOptions = WalletOptions()
}
