package com.faisel.hebrewdatepicker.utils

import java.lang.StringBuilder

object HebrewDateUtils {

    fun hebrewYearToGematria(year: Int): String {
        var num = year % 1000
        if (num == 0) return ""

        val sb = StringBuilder()
        val hundreds = num - (num % 100)

        when (hundreds) {
            900 -> { sb.append("תתק"); num -= 900 }
            800 -> { sb.append("תת"); num -= 800 }
            700 -> { sb.append("תש"); num -= 700 }
            600 -> { sb.append("תר"); num -= 600 }
            500 -> { sb.append("תק"); num -= 500 }
            400 -> { sb.append("ת"); num -= 400 }
            300 -> { sb.append("ש"); num -= 300 }
            200 -> { sb.append("ר"); num -= 200 }
            100 -> { sb.append("ק"); num -= 100 }
        }

        if (num > 0) {
            if (num == 15) {
                sb.append("ט").append("ו")
                num = 0
            } else if (num == 16) {
                sb.append("ט").append("ז")
                num = 0
            } else {
                if (num >= 90) { sb.append("צ"); num -= 90 }
                else if (num >= 80) { sb.append("פ"); num -= 80 }
                else if (num >= 70) { sb.append("ע"); num -= 70 }
                else if (num >= 60) { sb.append("ס"); num -= 60 }
                else if (num >= 50) { sb.append("נ"); num -= 50 }
                else if (num >= 40) { sb.append("מ"); num -= 40 }
                else if (num >= 30) { sb.append("ל"); num -= 30 }
                else if (num >= 20) { sb.append("כ"); num -= 20 }
                else if (num >= 10) { sb.append("י"); num -= 10 }

                if (num >= 9) sb.append("ט")
                else if (num >= 8) sb.append("ח")
                else if (num >= 7) sb.append("ז")
                else if (num >= 6) sb.append("ו")
                else if (num >= 5) sb.append("ה")
                else if (num >= 4) sb.append("ד")
                else if (num >= 3) sb.append("ג")
                else if (num >= 2) sb.append("ב")
                else if (num >= 1) sb.append("א")
            }
        }

        val rawGematria = sb.toString()
        if (rawGematria.isEmpty()) return ""

        return if (rawGematria.length == 1) {
            "$rawGematria׳" // גרש: 'א
        } else {
            val lastChar = rawGematria.last()
            val remainder = rawGematria.substring(0, rawGematria.length - 1)
            "$remainder״$lastChar"
        }
    }

    private fun addGershayim(gematrialString: String): String {
        val lastChar = gematrialString.last()
        val remainder = gematrialString.substring(0, gematrialString.length - 1)

        return if (remainder.isEmpty()) {
            "$lastChar׳"
        } else {
            "$remainder״$lastChar"
        }
    }

    fun hebrewMonthName(month: Int): String {
        return when (month) {
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
            12 -> "אדר א׳"
            13 -> "אדר ב׳"
            else -> "?"
        }
    }

    fun hebrewDayToGematria(day: Int): String {
        return when (day) {
            1 -> "א׳"
            2 -> "ב׳"
            3 -> "ג׳"
            4 -> "ד׳"
            5 -> "ה׳"
            6 -> "ו׳"
            7 -> "ז׳"
            8 -> "ח׳"
            9 -> "ט׳"
            10 -> "י׳"
            11 -> "י״א"
            12 -> "י״ב"
            13 -> "י״ג"
            14 -> "י״ד"
            15 -> "ט״ו"
            16 -> "ט״ז"
            17 -> "י״ז"
            18 -> "י״ח"
            19 -> "י״ט"
            20 -> "כ׳"
            21 -> "כ״א"
            22 -> "כ״ב"
            23 -> "כ״ג"
            24 -> "כ״ד"
            25 -> "כ״ה"
            26 -> "כ״ו"
            27 -> "כ״ז"
            28 -> "כ״ח"
            29 -> "כ״ט"
            30 -> "ל׳"
            else -> day.toString()
        }
    }
}