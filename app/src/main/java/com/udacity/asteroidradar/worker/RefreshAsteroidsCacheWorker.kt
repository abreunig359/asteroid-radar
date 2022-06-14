package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.db.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException
import java.time.Duration

class RefreshAsteroidsCacheWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val repository = AsteroidsRepository(getDatabase(appContext).asteroidDao)

    override suspend fun doWork(): Result {
        return try {
            repository.updateAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

    companion object {

        const val NAME = "RefreshAsteroidsWorker"
        private val constraintsWifiAndCharging =
            Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true).build()

        fun getDailyWorkRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<RefreshAsteroidsCacheWorker>(Duration.ofDays(1)).setConstraints(
                constraintsWifiAndCharging
            ).build()
        }
    }
}