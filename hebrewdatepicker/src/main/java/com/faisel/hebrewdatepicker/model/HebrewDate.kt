package com.faisel.hebrewdatepicker.model

import com.faisel.hebrewdatepicker.utils.HebrewDateUtils

data class HebrewDate(
    val day: Int,
    val month: Int,
    val year: Int
) {
    private val isLeapYear: Boolean
        get() = HebrewDateUtils.isHebrewLeapYear(year)

    val monthName: String
        get() {
            val months = if (isLeapYear) listOf(
                "ניסן", "אייר", "סיוון", "תמוז", "אב", "אלול",
                "תשרי", "חשוון", "כסלו", "טבת", "שבט", "אדר א׳", "אדר ב׳"
            ) else listOf(
                "ניסן", "אייר", "סיוון", "תמוז", "אב", "אלול",
                "תשרי", "חשוון", "כסלו", "טבת", "שבט", "אדר"
            )
            return months.getOrNull(month - 1) ?: "?"
        }

    fun getMonthCount(): Int = if (isLeapYear) 13 else 12
    val dayGematria: String
        get() = HebrewDateUtils.hebrewDayToGematria(day)

    val yearGematria: String
        get() = HebrewDateUtils.hebrewYearToGematria(year)

    override fun toString(): String = "$dayGematria $monthName $yearGematria"
}
