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

package io.hkhc.gossip

import dagger.android.support.DaggerApplication
import io.hkhc.gossip.di.DaggerGossipAppComp
import io.hkhc.log.Priority
import io.hkhc.log.internal.LogFactory
import io.hkhc.log.internal.TagMaker
import io.hkhc.log.providers.AndroidLogProvider
import io.hkhc.log.providers.AndroidPackageMetaTag

/*
    Must extend DaggerApplication. Beware that there are two versions. Pick the right one and
    check the import list above.
    - dagger.android.DaggerApplication
    - dagger.android.support.DaggerApplication
 */
class GossipApp: DaggerApplication() {

    /*
        Implement this and return an component instance with compoment builder.
        Bind the current application instance if you customized the build for that.
        Not setting application instance will cause exception in builder
            "java.lang.IllegalStateException: android.app.Application must be set"

        See the component class.
     */
    override fun applicationInjector() =
        DaggerGossipAppComp
            .builder()
            .application(this)
            .build()


    override fun onCreate() {
        super.onCreate()

        LogFactory.defaultProvider = AndroidLogProvider()
        TagMaker.metaTagPolicy = AndroidPackageMetaTag(this)
        LogFactory.logLevel = Priority.Debug
    }


}
