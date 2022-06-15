package com.udacity.asteroidradar.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid
import java.sql.Date

@Entity
data class DbModelAsteroid(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: Date,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) {

    fun asDomainModel() = Asteroid(
        id = id,
        codename = codename,
        closeApproachDate = closeApproachDate.toString(),
        absoluteMagnitude = absoluteMagnitude,
        estimatedDiameter = estimatedDiameter,
        relativeVelocity = relativeVelocity,
        distanceFromEarth = distanceFromEarth,
        isPotentiallyHazardous = isPotentiallyHazardous,
    )
}

fun List<DbModelAsteroid>.asDomainModel(): List<Asteroid> = map {
    it.asDomainModel()
}