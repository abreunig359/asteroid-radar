package com.udacity.asteroidradar.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.sql.Date
import java.time.LocalDate

@Dao
interface AsteroidDao {

    @Query("select * from dbmodelasteroid order by  closeApproachDate ASC")
    fun getAllSortedByDateAsc(): LiveData<List<DbModelAsteroid>>

    @Query("select * from dbmodelasteroid where closeApproachDate between :startDate and :endDate order by  closeApproachDate ASC")
    fun getBetweenDates(
        startDate: Date = Date.valueOf(LocalDate.now().toString()),
        endDate: Date
    ): LiveData<List<DbModelAsteroid>>

    @Query("select * from dbmodelasteroid where closeApproachDate >= :startDate order by  closeApproachDate ASC")
    fun getAllFromStartDate(
        startDate: Date = Date.valueOf(LocalDate.now().toString()),
    ): LiveData<List<DbModelAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<DbModelAsteroid>)
}
