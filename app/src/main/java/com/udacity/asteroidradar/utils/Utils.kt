package com.udacity.asteroidradar.utils

import java.sql.Date
import java.time.LocalDate

fun LocalDate.toSqlDate(): Date {
    return Date.valueOf(this.toString())
}