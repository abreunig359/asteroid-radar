package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.DEFAULT_END_DATE_DAYS
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.db.AsteroidDao
import com.udacity.asteroidradar.db.asDomainModel
import com.udacity.asteroidradar.utils.toSqlDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate

class AsteroidsRepository(private val asteroidDao: AsteroidDao) {

    fun getAsteroids(
        endDate: LocalDate?,
    ): LiveData<List<Asteroid>> {
        return when {
            endDate != null -> getAsteroidsFromTodayUntil(endDate)
            else -> getAllStoredAsteroids()
        }
    }

    private fun getAsteroidsFromTodayUntil(endDate: LocalDate): LiveData<List<Asteroid>> {
        val now = LocalDate.now()
        return Transformations.map(
            asteroidDao.getBetweenDates(
                now.toSqlDate(),
                endDate.toSqlDate()
            )
        ) {
            it.asDomainModel()
        }
    }

    private fun getAllStoredAsteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(asteroidDao.getAllStartingToday()) {
            it.asDomainModel()
        }
    }

    suspend fun updateAsteroids(
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(DEFAULT_END_DATE_DAYS)
    ) = withContext(Dispatchers.IO) {
        val asteroidDbModels = getAsteroidsFromApi(startDate, endDate).map {
            it.asDbModel()
        }
        asteroidDao.insertAll(asteroidDbModels)
    }

    suspend fun deleteAsteroidsBeforeToday() = withContext(Dispatchers.IO) {
        val now = LocalDate.now().toSqlDate()
        asteroidDao.deleteBeforeDate(now)
    }

    private suspend fun getAsteroidsFromApi(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<Asteroid> {
        return try {
            val asteroidsJson =
                AsteroidApi.retrofitService.getAsteroids(
                    startDate = startDate.toString(),
                    endDate = endDate.toString()
                )
            parseAsteroidsJsonResult(JSONObject(asteroidsJson))
        } catch (e: Exception) {
            emptyList()
        }
    }
}