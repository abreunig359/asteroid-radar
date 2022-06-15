package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.DEFAULT_END_DATE_DAYS
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.db.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(private val application: Application) : ViewModel() {

    private val asteroidsRepository = AsteroidsRepository(getDatabase(application).asteroidDao)

    // LocalDate is nullable because a null value is interpreted as "no end date"
    private val endDate = MutableLiveData<LocalDate?>()

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.switchMap(endDate) { endDate ->
            asteroidsRepository.getAsteroids(endDate)
        }

    private val _pictureOfDayDescription = MutableLiveData<String>()
    val pictureOfDayDescription: LiveData<String>
        get() = _pictureOfDayDescription

    private val _pictureOfDayUrl = MutableLiveData<String>()
    val pictureOfDayUrl: LiveData<String>
        get() = _pictureOfDayUrl

    init {
        showNextWeekAsteroids()
    }

    fun getPictureOfTheDay() = viewModelScope.launch {
        try {
            val pictureOfDay = AsteroidApi.retrofitService.getPictureOfTheDay()
            val descriptionFormat =
                application.getString(R.string.nasa_picture_of_day_content_description_format)
            _pictureOfDayDescription.postValue(String.format(descriptionFormat, pictureOfDay.title))
            _pictureOfDayUrl.postValue(pictureOfDay.url)
        } catch (e: Exception) {
            val placeholderString =
                application.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
            _pictureOfDayDescription.postValue(placeholderString)
        }
    }

    fun showNextWeekAsteroids() {
        val now = LocalDate.now()
        viewModelScope.launch {
            asteroidsRepository.updateAsteroids(endDate = now.plusDays(DEFAULT_END_DATE_DAYS))
            // using post value, since setting the end date after the coroutine produced ugly flickering on the screen
            endDate.postValue(now.plusDays(DEFAULT_END_DATE_DAYS))
        }
    }

    fun showTodayAsteroids() {
        val now = LocalDate.now()
        viewModelScope.launch {
            asteroidsRepository.updateAsteroids()
            endDate.postValue(now)
        }
    }

    fun showAllSavedAsteroids() {
        endDate.value = null
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