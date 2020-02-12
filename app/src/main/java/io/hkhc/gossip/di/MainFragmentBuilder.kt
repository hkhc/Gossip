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
import io.hkhc.dagger.FragmentScope
import io.hkhc.gossip.testbed.ConfigFragment
import io.hkhc.gossip.testbed.TestbedFragment

@Module
@Suppress("unused")
abstract class MainFragmentBuilder {

    @ContributesAndroidInjector(modules = [])
    @FragmentScope
    abstract fun testbed(): TestbedFragment

    @ContributesAndroidInjector(modules = [])
    @FragmentScope
    abstract fun config(): ConfigFragment
}
