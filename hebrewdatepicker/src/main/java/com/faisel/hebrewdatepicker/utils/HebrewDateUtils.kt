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

        val finalString = sb.toString()
        if (finalString.isEmpty()) return ""

        val lastChar = finalString.last()
        val remainder = finalString.substring(0, finalString.length - 1)

        return if (remainder.isEmpty()) {
            "$lastChar׳"
        } else {
            "$remainder״$lastChar"
        }
    }

    fun isHebrewLeapYear(year: Int): Boolean {
        return ((7 * year + 1) % 19) < 7
    }
    fun hebrewDayToGematria(day: Int): String {
        val dayMap = listOf(
            "", "א׳", "ב׳", "ג׳", "ד׳", "ה׳", "ו׳", "ז׳", "ח׳", "ט׳",
            "י׳", "י״א", "י״ב", "י״ג", "י״ד", "ט״ו", "ט״ז", "י״ז", "י״ח", "י״ט",
            "כ׳", "כ״א", "כ״ב", "כ״ג", "כ״ד", "כ״ה", "כ״ו", "כ״ז", "כ״ח", "כ״ט", "ל׳"
        )
        return dayMap.getOrNull(day) ?: "?"
    }
}