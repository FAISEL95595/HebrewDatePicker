package com.faisel.hebrewdatepicker.model

import com.faisel.hebrewdatepicker.utils.HebrewDateUtils

data class HebrewDate(
    val day: Int,
    val month: Int,
    val year: Int
) {
    val monthName: String
        get() = HebrewDateUtils.hebrewMonthName(month)

    val dayGematria: String
        get() = HebrewDateUtils.hebrewDayToGematria(day)

    val yearGematria: String
        get() = HebrewDateUtils.hebrewYearToGematria(year)

    override fun toString(): String = "$dayGematria $monthName $yearGematria"
}