package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.db.AsteroidDao
import com.udacity.asteroidradar.db.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate

class AsteroidsRepository(private val asteroidDao: AsteroidDao) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(asteroidDao.getAllSortedByDateAsc()) {
            it.asDomainModel()
        }

    suspend fun updateAsteroids() = withContext(Dispatchers.IO) {
        val today = LocalDate.now().toString()
        val asteroidsJson =
            AsteroidApi.retrofitService.getAsteroids(startDate = today, endDate = null)
        val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsJson)).map { it.asDbModel() }
        asteroidDao.insertAll(asteroids)
    }
}