package com.sktech.storevisionpro.data.sync

import android.content.Context
import androidx.work.*
import com.sktech.storevisionpro.data.local.StoreVisionDbHelper
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val dbHelper = StoreVisionDbHelper.getInstance(applicationContext)
            val pendingCount = dbHelper.getPendingSyncCount()

            if (pendingCount > 0) {
                // Simulate network latency / Firestore batch write
                delay(1200)
                // Mark batch processed in local queue
                dbHelper.markSyncQueueItemsProcessed(pendingCount)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

object SyncManager {
    private const val PERIODIC_SYNC_TAG = "STOREVISION_PERIODIC_SYNC"
    private const val IMMEDIATE_SYNC_TAG = "STOREVISION_IMMEDIATE_SYNC"

    fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(PERIODIC_SYNC_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    fun triggerImmediateSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val immediateRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .addTag(IMMEDIATE_SYNC_TAG)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_SYNC_TAG,
            ExistingWorkPolicy.REPLACE,
            immediateRequest
        )
    }
}
