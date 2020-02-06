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

package io.hkhc.gossip.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.hkhc.gossip.GossipApp
import javax.inject.Singleton

/*
  AndroidSupportInjectionModule is mandatory module.
  You probably need an module for injecting activities (MainActivityBuilder in this case)
  Add other modules that you would like to share globally.
 */

@Component(modules = [
    /* This is mandatory dagger module from library */
    AndroidSupportInjectionModule::class,
    /* You need to create dagger module(s) for injecting Activities */
    MainActivityBuilder::class
    /* You may add other modules here that share globally */
])
@Singleton
/*
    - The template parameter must be the application class used in this project.
    - Cannot be the base Application class. DaggerApplication class expect the component created
    this way. Otherwise we will get error like this at application class:

        Type mismatch: inferred type is ApplicationComponent but
        AndroidInjector<out ReadmooApplication> was expected

 */
interface GossipAppComp : AndroidInjector<GossipApp> {

    /*
        We need a customized builder if we want to inject application object.
        (You are very likely to need that for non-trivial Android apps)
        Otherwise the default builder will do.
    */
    @Component.Builder
    interface Builder {

        /*
            Provide the application instance when creating component instance.
            - Method name does not matter. The key is @BindsInstance
            - The parameter shall be this application class or the base Application class. But It
            should match the class that inject in module.
         */
        @BindsInstance
        fun application(app: Application): Builder

        fun build(): GossipAppComp
    }
}
