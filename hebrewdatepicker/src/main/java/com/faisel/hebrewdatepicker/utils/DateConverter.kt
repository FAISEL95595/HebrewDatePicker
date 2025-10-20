package com.faisel.hebrewdatepicker.utils

import com.faisel.hebrewdatepicker.model.HebrewDate
import java.time.LocalDate
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import java.util.Date
import java.time.ZoneId

object DateConverter {

    fun gregorianToHebrew(date: LocalDate): HebrewDate {
        val instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val javaDate = Date.from(instant)

        val jewishCalendar = JewishCalendar(javaDate)

        val day = jewishCalendar.jewishDayOfMonth
        val month = jewishCalendar.jewishMonth
        val year = jewishCalendar.jewishYear

        return HebrewDate(day, month, year)
    }
}