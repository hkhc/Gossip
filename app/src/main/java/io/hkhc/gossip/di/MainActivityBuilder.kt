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

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.hkhc.dagger.ActivityScope
import io.hkhc.gossip.MainActivity

@Module
/* there is no explicit reference to the methods, suppress the "unused" warning here */
@Suppress("unused")
abstract class MainActivityBuilder {

    /**
        Generate the dagger sub-component with each of the activities here.
        Add module that used by the activity only.
        As other provider methods, the method name does not matter.

        Given that MainActivity is scoped (@ActivityScope), it can only inject @AndroidScope
        or non-scoped dependencies. Otherwise we will get [Dagger/IncompatiblyScopedBindings]
        error. That's all dependencies in the following modules cannot have scope other than
        @ActivityScope

        Every instance of the sub-component provides single instance of a dependencies of the
        same scope. E.g. we have two sub-components for MainActivity and LoginActivity2,
        generated by @ContributeAndroidInjector. Single instance of each dependency is provided
        When another subcomponent (even of the same scope annotation) injects the same module,
        another instance of dependencies are created.

        Ref: https://stackoverflow.com/questions/29923376/dagger2-custom-scopes-how-do-custom-scopes-activityscope-actually-work

     */
    @ContributesAndroidInjector(modules = [
        /* We need the module here that provides the view model with @IntoMap. */
        MainUIModule::class
//        MainFragmentBuilder::class,
        /*
            Then you may add modules that specifically need by this activity.
            Global module is declared at the ApplicationComponent.
         */
//        DeviceModule::class,
//        MeModule::class
    ])
    @ActivityScope
    abstract fun mainActivity(): MainActivity

}
