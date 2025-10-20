package com.faisel.hebrewdatepicker.utils

import com.faisel.hebrewdatepicker.model.HebrewDate
import java.time.LocalDate
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import java.util.Date
import java.time.ZoneId

object DateConverter {

    /**
     * ממיר תאריך לועזי לתאריך עברי מדויק באמצעות ספריית KosherJava Zmanim.
     */
    fun gregorianToHebrew(date: LocalDate): HebrewDate {
        // המרה מ-java.time.LocalDate ל-java.util.Date
        val instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val javaDate = Date.from(instant)

        val jewishCalendar = JewishCalendar(javaDate)

        val day = jewishCalendar.jewishDayOfMonth
        val month = jewishCalendar.jewishMonth
        val year = jewishCalendar.jewishYear

        return HebrewDate(day, month, year)
    }
}