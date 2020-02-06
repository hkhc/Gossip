package io.hkhc.viewmodel

import androidx.lifecycle.MutableLiveData
import io.reactivex.CompletableTransformer
import io.reactivex.MaybeTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import java.util.concurrent.atomic.AtomicInteger

/**
 * It is to be used with Rxjava based loading operation, to ensure the progress indicator
 * is shown and hide at proper time. It is independent on the actual implementation of UI
 * element. The loading status is represented by a Boolean MutableLiveData that any UI elemen t
 * can listen with BindingAdapter.
 *
 * The *Loading() methods encapsulate the show and hide logic as RxJava Transformer. It can
 * be integrated into normal Rxjava by compose() operator.
 *
 * e.g. A RxJava based api operation may look like this:
 *
 * ```
 * fun callAPi(): Single<Result> {
 *     apiService.callApi()
 * }
 * ```
 * We may add the loading counter like this:
 *
 * ```
 *
 * val loadingCounter = LiveDataLoadingCounter()
 *
 * fun callAPi(): Single<Result> {
 *     apiService.callApi()
 *         .compose(loadingCounter.singleLoading())
 * }
 * ```
 */
class LiveDataLoadngCounter : MutableLiveData<Boolean>() {

    private var refCount = AtomicInteger(0)

    fun show() {
        value = true
        refCount.addAndGet(1)
    }

    fun hide() {
        if (refCount.addAndGet(-1) == 0) {
            value = false
        }
    }

    fun <T> observableLoading(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.doOnSubscribe { show() }.doFinally { hide() }
        }
    }

    fun <T> singleLoading(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.doOnSubscribe { show() }.doFinally { hide() }
        }
    }

    fun <T> maybeLoading(): MaybeTransformer<T, T> {
        return MaybeTransformer {
            it.doOnSubscribe { show() }.doFinally { hide() }
        }
    }

    fun completableLoading(): CompletableTransformer {
        return CompletableTransformer {
            it.doOnSubscribe { show() }.doFinally { hide() }
        }
    }
}
