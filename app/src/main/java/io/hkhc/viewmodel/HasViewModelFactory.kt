package io.hkhc.viewmodel

import androidx.lifecycle.ViewModelProvider

interface HasViewModelFactory {

    var viewModelFactory: ViewModelProvider.Factory
}
