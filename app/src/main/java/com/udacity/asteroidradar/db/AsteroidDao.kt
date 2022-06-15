package com.udacity.asteroidradar.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.utils.toSqlDate
import java.sql.Date
import java.time.LocalDate

@Dao
interface AsteroidDao {

    @Query("select * from dbmodelasteroid where closeApproachDate >= :startDate order by  closeApproachDate ASC")
    fun getAllStartingToday(
        startDate: Date = LocalDate.now().toSqlDate()
    ): LiveData<List<DbModelAsteroid>>

    @Query("select * from dbmodelasteroid where closeApproachDate between :startDate and :endDate order by  closeApproachDate ASC")
    fun getBetweenDates(
        startDate: Date = LocalDate.now().toSqlDate(),
        endDate: Date
    ): LiveData<List<DbModelAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<DbModelAsteroid>)

    @Query("delete from dbmodelasteroid where closeApproachDate < :startDate")
    fun deleteBeforeDate(startDate: Date)
}
