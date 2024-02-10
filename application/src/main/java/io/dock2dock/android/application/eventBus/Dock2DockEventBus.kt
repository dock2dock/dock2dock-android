package io.dock2dock.android.application.eventBus

import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.coroutineContext

object Dock2DockEventBus {
    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }
}

data class LoginEvent(
    val userId: String,
    val userName: String
)

//class LoginEventHandler {
//    suspend fun postLoginEvent(loginEvent: LoginEvent) {
//        EventBus.publish(loginEvent)
//    }
//
//    fun subscribeLoginEvent(lifecycleOwner: LifecycleOwner) {
//        lifecycleOwner .launch {
//            EventBus.subscribe<LoginEvent> { loginEvent ->
//                Log.d("LoginEventHandler", "${loginEvent.userName} logged-in successfully")
//            }
//        }
//    }
//}