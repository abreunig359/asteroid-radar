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
import java.lang.Exception
import java.sql.Date
import java.time.LocalDate

class AsteroidsRepository(private val asteroidDao: AsteroidDao) {

    fun getAsteroids(
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): LiveData<List<Asteroid>> {
        return when {
            startDate != null && endDate == null -> getAsteroidsFrom(startDate)
            endDate != null -> getAsteroidsFromTodayUntil(endDate)
            else -> getAllStoredAsteroids()
        }
    }

    private fun getAsteroidsFrom(startDate: LocalDate): LiveData<List<Asteroid>> {
        val startDateSql = Date.valueOf(startDate.toString())
        return Transformations.map(asteroidDao.getAllFromStartDate(startDateSql)) {
            it.asDomainModel()
        }
    }

    private fun getAsteroidsFromTodayUntil(endDate: LocalDate): LiveData<List<Asteroid>> {
        val startDateSql = Date.valueOf(LocalDate.now().toString())
        val endDateSql = Date.valueOf(endDate.toString())
        return Transformations.map(asteroidDao.getBetweenDates(startDateSql, endDateSql)) {
            it.asDomainModel()
        }
    }

    private fun getAllStoredAsteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(asteroidDao.getAllSortedByDateAsc()) {
            it.asDomainModel()
        }
    }

    suspend fun updateAsteroids(
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(DEFAULT_END_DATE)
    ) = withContext(Dispatchers.IO) {
        val asteroidDbModels = getAsteroidsFromApi(startDate, endDate).map {
            it.asDbModel()
        }
        asteroidDao.insertAll(asteroidDbModels)
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

    companion object {

        const val DEFAULT_END_DATE = 7L
    }
}