package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.db.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    private val asteroidsRepository = AsteroidsRepository(getDatabase(application).asteroidDao)
    val asteroids: LiveData<List<Asteroid>> = asteroidsRepository.asteroids

    init {
        viewModelScope.launch {
            asteroidsRepository.updateAsteroids()
        }
    }

    /* create dummy values
            _asteroids.value = (0..20).map {
            val isHazardous = it % 2
            Asteroid(
                it.toLong(),
                "Asteroid $it",
                closeApproachDate = "2022-06-14",
                absoluteMagnitude = 1.0,
                estimatedDiameter = 10.0,
                relativeVelocity = 10.0,
                distanceFromEarth = 10.0,
                isPotentiallyHazardous = isHazardous == 0
            )
        }
     */

    class Factory(private val app: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}