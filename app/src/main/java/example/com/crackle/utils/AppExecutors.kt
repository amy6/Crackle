package example.com.crackle.utils

import android.os.Handler
import android.os.Looper

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors(val diskIO: Executor, val networkIO: Executor, val mainThread: Executor) {

    private class MainThreadExecutor : Executor {

        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(runnable: Runnable) {
            mainThreadHandler.post(runnable)
        }
    }

    companion object {

        private val LOCK = Any()
        private var executorInstance: AppExecutors? = null

        fun getExecutorInstance(): AppExecutors {
            if (executorInstance == null) {
                synchronized(LOCK) {
                    executorInstance = AppExecutors(Executors.newSingleThreadExecutor(),
                            Executors.newFixedThreadPool(3),
                            MainThreadExecutor())
                }
            }
            return executorInstance!!
        }
    }
}
