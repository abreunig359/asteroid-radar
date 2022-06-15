package com.udacity.asteroidradar

import android.os.Parcelable
import com.udacity.asteroidradar.db.DbModelAsteroid
import kotlinx.android.parcel.Parcelize
import java.sql.Date

@Parcelize
data class Asteroid(
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable {

    // Using named arguments since there are multiple parameters with the same value
    fun asDbModel() = DbModelAsteroid(
        id = id,
        codename = codename,
        closeApproachDate = Date.valueOf(closeApproachDate),
        absoluteMagnitude = absoluteMagnitude,
        estimatedDiameter = estimatedDiameter,
        relativeVelocity = relativeVelocity,
        distanceFromEarth = distanceFromEarth,
        isPotentiallyHazardous = isPotentiallyHazardous,
    )
}