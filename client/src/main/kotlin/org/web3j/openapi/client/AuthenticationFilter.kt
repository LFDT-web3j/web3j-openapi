/*
 * Copyright 2019 Web3 Labs Ltd.
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
package org.web3j.openapi.client

import jakarta.ws.rs.client.ClientRequestContext
import jakarta.ws.rs.client.ClientRequestFilter
import jakarta.ws.rs.core.HttpHeaders

class AuthenticationFilter private constructor(
    private val token: String,
) : ClientRequestFilter {
    override fun filter(requestContext: ClientRequestContext) {
        requestContext.headers.putSingle(HttpHeaders.AUTHORIZATION, "Bearer $token")
    }

    companion object {
        fun token(token: String) = AuthenticationFilter(token)
    }
}
