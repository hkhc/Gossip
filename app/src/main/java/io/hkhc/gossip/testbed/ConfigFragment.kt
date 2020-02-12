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

package io.hkhc.gossip.testbed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import dagger.android.support.DaggerFragment
import io.hkhc.gossip.R
import io.hkhc.gossip.databinding.FragmentConfigBinding
import io.hkhc.utils.nonNullLet
import io.hkhc.viewmodel.ViewModelFactory
import io.hkhc.viewmodel.resolve
import javax.inject.Inject

class ConfigFragment: DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentConfigBinding
    private lateinit var viewModel: ConfigViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_config,
            container,
            false)
        viewModel = viewModelFactory.resolve(this)

        /*
        Setup Data Binding
         */
        binding.lifecycleOwner = this
        binding.vm = viewModel

        binding.start.setOnClickListener { view ->

            nonNullLet(viewModel.memberName.value, viewModel.hostMode.value) {
                    memberName, hostMode ->
                val action = ConfigFragmentDirections.actionConfigFragmentToTestbedFragment(
                        memberName,
                        hostMode
                    )
                view.findNavController().navigate(action)
            }

        }

        return binding.root

    }
}
