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

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.hkhc.dagger.ViewModelKey
import io.hkhc.gossip.MainViewModel
import io.hkhc.gossip.testbed.ConfigViewModel
import io.hkhc.gossip.testbed.TestbedViewModel

@Module
@Suppress("unused")
abstract class MainUIModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun main(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TestbedViewModel::class)
    internal abstract fun testbed(viewModel: TestbedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConfigViewModel::class)
    internal abstract fun config(viewModel: ConfigViewModel): ViewModel

}
