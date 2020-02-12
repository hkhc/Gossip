/*
 * Copyright (c) 2020. Herman Cheung
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
 *
 */

package io.hkhc.gossip.nds

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import io.hkhc.log.l

class NSDHelper {

    var serviceName = "Gossip"

    lateinit var context: Context
    lateinit var nsdManager: NsdManager
    lateinit var serviceInfo: NsdServiceInfo
    var registered: Boolean = false
    var successBlock: ((NsdServiceInfo) -> (Unit))? = null

    fun init(context: Context, block: (NsdServiceInfo) -> (Unit)) {
        this.context = context
        this.nsdManager = (context.getSystemService(Context.NSD_SERVICE) as NsdManager)
        successBlock = block

    }

    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) {
            // Save the service name. Android may have changed it in order to
            // resolve a conflict, so update the name you initially requested
            // with the name Android actually used.
            serviceName = nsdServiceInfo.serviceName
            registered = true
            l.debug("NSD registration succeed ${serviceName}")
        }

        override fun onRegistrationFailed(nsdServiceInfo: NsdServiceInfo, errorCode: Int) {
            // Registration failed! Put debugging code here to determine why.
            l.debug("NSD registration failed ${nsdServiceInfo.serviceName}")
        }

        override fun onServiceUnregistered(nsdServiceInfo: NsdServiceInfo) {
            // Service has been unregistered. This only happens when you call
            // NsdManager.unregisterService() and pass in this listener.
            l.debug("NSD unregistration succeed ${nsdServiceInfo.serviceName}")
            registered = false
        }

        override fun onUnregistrationFailed(nsdServiceInfo: NsdServiceInfo, errorCode: Int) {
            // Unregistration failed. Put debugging code here to determine why.
            l.debug("NSD unregistration failed ${nsdServiceInfo.serviceName}")
        }
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            l.debug("Service discovery started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            l.debug("Service discovery success $service")
            when {
                service.serviceType != serviceInfo.serviceType -> // Service type is the string containing the protocol and
                    // transport layer for this service.
                    l.debug("Unknown Service Type: actual ${service.serviceType} vs expected ${serviceInfo.serviceType}")
                service.serviceName == serviceInfo.serviceName -> // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    l.debug("Same machine: ${serviceInfo.serviceName}")
                service.serviceName.contains("Gossip") -> nsdManager.resolveService(service, resolveListener)
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            l.debug("service lost: $service")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            l.debug("Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            l.debug("Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)

        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            l.debug("Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }

    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Called when the resolve fails. Use the error code to debug.
            l.debug("Resolve failed: $errorCode")
        }

        override fun onServiceResolved(info: NsdServiceInfo) {
            l.err("Resolve Succeeded. $info")

            if (info.serviceName == serviceInfo.serviceName) {
                l.debug("Same IP.")
                return
            }
            this@NSDHelper.serviceInfo = info
            successBlock?.invoke(serviceInfo)
        }
    }

    fun registerService(port: Int) {
        serviceInfo = NsdServiceInfo().apply {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceName = this@NSDHelper.serviceName
            serviceType = "_gossip._tcp."
            setPort(port)
        }

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)

    }

    fun unregisterService() {

        if (registered)
            nsdManager.unregisterService(registrationListener)

    }

    fun discoverService() {

        serviceInfo = NsdServiceInfo().apply {
            serviceType = "_gossip._tcp."
        }

        nsdManager.discoverServices(serviceInfo.serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscoveryService() {

        nsdManager.stopServiceDiscovery(discoveryListener)

    }

    fun tearDown() {
        nsdManager.apply {
            if (registered)
                unregisterService(registrationListener)
            stopServiceDiscovery(discoveryListener)
        }
    }

}
