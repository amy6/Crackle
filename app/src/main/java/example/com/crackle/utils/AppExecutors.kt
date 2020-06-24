package example.com.crackle.utils

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(val diskIO: Executor) {

    companion object {

        @Volatile
        private var executorInstance: AppExecutors? = null

        fun getExecutorInstance(): AppExecutors {
            return executorInstance ?: synchronized(this) {
                AppExecutors(Executors.newSingleThreadExecutor())
                        .also { executorInstance = it }
            }
        }
    }
}
