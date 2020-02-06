package io.hkhc.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * It is used to serve the Dagger [multibindings](https://dagger.dev/multibindings.html)
 * of view model. New view models are added with Dagger in Dagger module like this:
 *
 * ```
 * @Binds
 * @IntoMap
 * @ViewModelKey(MyViewModel::class)
 * internal abstract fun myViewModel(viewModel: MyViewModel): ViewModel
 * ```
 *
 * All viewmodels declared same way and Dagger create a Map<Class<out ViewModel>,Provider<ViewModel>>.
 * Then activities and fragments may inject this viewmodel factory to obtain a viewmodel.
 * d
 * **See also** [ViewModel with Dagger2](https://proandroiddev.com/viewmodel-with-dagger2-architecture-components-2e06f06c9455)
 *
 */
class ViewModelFactory @Inject constructor(
    @set:Inject var viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        @Suppress("UNCHECKED_CAST")
        return viewModels[modelClass]?.get() as T
    }
}

inline fun <reified T : ViewModel> ViewModelProvider.Factory.resolve(fragment: Fragment): T =
        ViewModelProvider(fragment, this)[T::class.java]

inline fun <reified T : ViewModel> ViewModelProvider.Factory.resolve(activity: FragmentActivity): T {
    return ViewModelProvider(activity, this)[T::class.java]
}
