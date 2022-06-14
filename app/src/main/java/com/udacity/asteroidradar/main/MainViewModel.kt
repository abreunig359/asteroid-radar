package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.db.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDate

class MainViewModel(private val application: Application) : ViewModel() {

    private val asteroidsRepository = AsteroidsRepository(getDatabase(application).asteroidDao)

    // A null value for a certain date means it can be ignored.
    // If both values are null, all asteroids stored in DB should be fetched.
    private val startEndDateForAsteroidRepoFetch = MutableLiveData<Pair<LocalDate?, LocalDate?>>()

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.switchMap(startEndDateForAsteroidRepoFetch) { (startDate, endDate) ->
            asteroidsRepository.getAsteroids(startDate, endDate)
        }

    private val _pictureOfDayDescription = MutableLiveData<String>()
    val pictureOfDayDescription: LiveData<String>
        get() = _pictureOfDayDescription

    private val _pictureOfDayUrl = MutableLiveData<String>()
    val pictureOfDayUrl: LiveData<String>
        get() = _pictureOfDayUrl

    init {
        viewModelScope.launch {
            asteroidsRepository.updateAsteroids()
            getPictureOfTheDay()
        }
        showTodaysAsteroids()
    }

    private suspend fun getPictureOfTheDay() {
        try {
            val pictureOfDay = AsteroidApi.retrofitService.getPictureOfTheDay()
            _pictureOfDayDescription.value = pictureOfDay.title
            _pictureOfDayUrl.value = pictureOfDay.url
        } catch (e: Exception) {
            _pictureOfDayDescription.value =
                application.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
        }
    }

    fun showNextWeekAsteroids() {
        val now = LocalDate.now()
        startEndDateForAsteroidRepoFetch.value = Pair(now, now.plusDays(7))
    }

    fun showTodaysAsteroids() {
        val now = LocalDate.now()
        startEndDateForAsteroidRepoFetch.value = Pair(now, now)
    }

    fun showAllSavedAsteroids() {
        startEndDateForAsteroidRepoFetch.value = Pair(null, null)
    }

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