package com.udacity.asteroidradar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.udacity.asteroidradar.worker.RefreshAsteroidsCacheWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        setupAsteroidsCacheRefreshWorker()
    }

    private fun setupAsteroidsCacheRefreshWorker() = applicationScope.launch {
        val workRequest = RefreshAsteroidsCacheWorker.getDailyWorkRequest()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshAsteroidsCacheWorker.NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
