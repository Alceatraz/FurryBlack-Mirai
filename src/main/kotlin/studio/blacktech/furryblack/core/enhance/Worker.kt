package studio.blacktech.furryblack.core.enhance

import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class Worker constructor(size: Int) {

  private val scope = RestrictedScope()
  private val dispatcher = Executors.newFixedThreadPool(size).asCoroutineDispatcher()

  fun launch(func: () -> Unit): Job =
    scope.launch(dispatcher) {
      func.invoke()
    }

  fun close() {
    dispatcher.close()
    scope.cancel()
  }

}