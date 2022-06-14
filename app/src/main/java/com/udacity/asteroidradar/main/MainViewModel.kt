package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid

class MainViewModel : ViewModel() {

    private val _asteroids = MutableLiveData<List<Asteroid>>()

    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        fetchAsteroidsFromApi()
    }

    private fun fetchAsteroidsFromApi() {
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
    }
}