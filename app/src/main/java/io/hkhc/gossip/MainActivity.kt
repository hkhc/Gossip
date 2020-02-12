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

import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import dagger.android.support.DaggerAppCompatActivity
import io.hkhc.gossip.databinding.ActivityMainBinding
import io.hkhc.gossip.databinding.NavHeaderBinding
import io.hkhc.gossip.testbed.ConfigFragment
import io.hkhc.gossip.testbed.TestbedFragment
import io.hkhc.gossip.testbed.TestbedViewModel
import io.hkhc.log.l
import io.hkhc.viewmodel.ViewModelFactory
import io.hkhc.viewmodel.resolve
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var navHeaderBinding: NavHeaderBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = viewModelFactory.resolve(this)

        mainBinding.lifecycleOwner = this
        mainBinding.vm = viewModel

        // https://stackoverflow.com/questions/33962548/how-to-data-bind-to-a-header ??
        // We cannot bind to headier view of NavigationView if it is specified in layout. We need
        // to inflate the header view here and add it to navigation view programmatically
        navHeaderBinding = DataBindingUtil.inflate(layoutInflater, R.layout.nav_header, mainBinding.navigation, false)
        navHeaderBinding.lifecycleOwner = this
        navHeaderBinding.vm = viewModel

        mainBinding.navigation.addHeaderView(navHeaderBinding.root)

        setSupportActionBar(mainBinding.toolbar)

        ActionBarDrawerToggle(
            this,
            mainBinding.drawer, mainBinding.toolbar, 0, 0
        ).apply {
            syncState()
        }

        viewModel.sendMessage.observe(this, Observer { message ->

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

            var currDest = navHostFragment?.getChildFragmentManager()?.getFragments()?.get(0);

            currDest?.let { frag ->
                if (frag is TestbedFragment) {
                    frag.sendMessage(message)
                }
            }

        })

    }
}
