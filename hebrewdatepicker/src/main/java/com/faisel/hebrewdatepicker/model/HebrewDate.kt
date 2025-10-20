package com.faisel.hebrewdatepicker.model

import com.faisel.hebrewdatepicker.utils.HebrewDateUtils

data class HebrewDate(
    val day: Int,
    val month: Int, // מספר החודש לפי KosherJava: 1=Nisan, 7=Tishrei
    val year: Int
) {
    val monthName: String
        get() = when(month) {
            1 -> "ניסן"
            2 -> "אייר"
            3 -> "סיוון"
            4 -> "תמוז"
            5 -> "אב"
            6 -> "אלול"
            7 -> "תשרי"
            8 -> "חשוון"
            9 -> "כסלו"
            10 -> "טבת"
            11 -> "שבט"
            12 -> "אדר א'"
            13 -> "אדר ב'"
            else -> "?"
        }

    val dayGematria: String
        get() = HebrewDateUtils.hebrewDayToGematria(day)

    val yearGematria: String
        get() = HebrewDateUtils.hebrewYearToGematria(year)

    override fun toString(): String = "$dayGematria $monthName $yearGematria"
}