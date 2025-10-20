package com.faisel.hebrewdatepicker.ui

/**
 * מגדיר את סוג התאריך שה-Callback יחזיר בעת בחירת יום.
 */
enum class OutputType {
    /** החזרת תאריך עברי (HebrewDate) בלבד. */
    HEBREW,
    /** החזרת תאריך לועזי (LocalDate) בלבד. */
    GREGORIAN,
    /** החזרת שני סוגי התאריכים (ברירת מחדל). */
    BOTH
}