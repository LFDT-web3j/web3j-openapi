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

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.glassfish.jersey.logging.LoggingFeature
import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.logging.Level
import java.util.logging.Logger

class ClientService
    @JvmOverloads
    constructor(
        val uri: String,
        readTimeout: Int = DEFAULT_READ_TIMEOUT,
        connectTimeout: Int = DEFAULT_CONNECT_TIMEOUT,
    ) : AutoCloseable {
        private val mapper =
            jacksonObjectMapper()
                .setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .enable(SerializationFeature.INDENT_OUTPUT)

        internal val client: Client by lazy {
            val config =
                ClientConfig().apply {
                    // Redirect ALL logs to SLF4J using logging.properties
                    register(LoggingFeature(logger.apply { level = Level.ALL }, Short.MAX_VALUE.toInt()))
                    register(JacksonJaxbJsonProvider(mapper, arrayOf(Annotations.JACKSON)))
                    property(ClientProperties.READ_TIMEOUT, readTimeout)
                    property(ClientProperties.CONNECT_TIMEOUT, connectTimeout)
                }
            ClientBuilder.newClient(config)
        }

        override fun close() = client.close()

        companion object {
            const val DEFAULT_READ_TIMEOUT: Int = 60000
            const val DEFAULT_CONNECT_TIMEOUT: Int = 60000

            init {
                SLF4JBridgeHandler.removeHandlersForRootLogger()
                SLF4JBridgeHandler.install()
            }

            private val logger = Logger.getLogger(ClientService::class.java.canonicalName)!!
        }
    }
