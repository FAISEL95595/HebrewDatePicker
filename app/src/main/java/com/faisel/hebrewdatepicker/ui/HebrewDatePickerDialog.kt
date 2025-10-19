package com.faisel.hebrewdatepicker.ui

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import com.faisel.hebrewdatepicker.R
import com.faisel.hebrewdatepicker.model.HebrewDate
import com.faisel.hebrewdatepicker.ui.calendar.CalendarAdapter
import com.faisel.hebrewdatepicker.utils.DateConverter
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.ZoneId
import java.util.Date
import java.util.Calendar

class HebrewDatePickerDialog private constructor(
    context: Context,
    private val mode: PickerMode,
    private val outputType: OutputType, // <--- פרמטר חדש
    private val onHebrewSelected: ((HebrewDate) -> Unit)? = null,
    private val onGregorianSelected: ((LocalDate) -> Unit)? = null
) : Dialog(context) {

    private var currentHebrewMonthCalendar: JewishCalendar = JewishCalendar(Date())

    init {
        currentHebrewMonthCalendar.jewishDayOfMonth = 1
        setCancelable(true)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        if (mode == PickerMode.CALENDAR) {
            setUpCalendarUI(context)
        }
    }

    private fun setUpCalendarUI(context: Context) {
        setContentView(R.layout.calendar_picker)

        val tvMonthYear = findViewById<TextView>(R.id.tvMonthYear)
        val gridDays = findViewById<GridView>(R.id.gridDays)
        val btnPrev = findViewById<Button>(R.id.btnPrevMonth)
        val btnNext = findViewById<Button>(R.id.btnNextMonth)

        fun loadMonth(jc: JewishCalendar) {
            val days = mutableListOf<Pair<LocalDate?, HebrewDate?>>()

            val startGregorianCalendar = jc.gregorianCalendar
            val startGregorianDate = startGregorianCalendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val dayOfWeek = startGregorianDate.dayOfWeek

            val paddingDays = when (dayOfWeek) {
                DayOfWeek.SUNDAY -> 0
                else -> dayOfWeek.value
            }

            for (i in 0 until paddingDays) {
                days.add(Pair(null, null)) // ימי מילוי
            }

            val daysInMonth = jc.getDaysInJewishMonth()

            var currentDate = startGregorianDate

            for (i in 1..daysInMonth) {
                val hebrew = DateConverter.gregorianToHebrew(currentDate)
                days.add(Pair(currentDate, hebrew))

                currentDate = currentDate.plusDays(1)
            }

            // 4. עדכון האדפטר
            val adapter = CalendarAdapter(context, days) { gregorian, hebrew ->
                // *** לוגיקת בחירת סוג הפלט ***
                when (outputType) { // משתמשים בפרמטר החדש
                    OutputType.HEBREW -> onHebrewSelected?.invoke(hebrew)
                    OutputType.GREGORIAN -> onGregorianSelected?.invoke(gregorian)
                    OutputType.BOTH -> {
                        onGregorianSelected?.invoke(gregorian)
                        onHebrewSelected?.invoke(hebrew)
                    }
                }
                dismiss()
            }
            gridDays.adapter = adapter

            // 5. הצגת כותרת
            val firstHebrewDateModel = DateConverter.gregorianToHebrew(startGregorianDate)

            tvMonthYear.text = "${firstHebrewDateModel.monthName} ${firstHebrewDateModel.yearGematria} / ${startGregorianDate.month} ${startGregorianDate.year}"
        }

        loadMonth(currentHebrewMonthCalendar)

        btnPrev.setOnClickListener {
            currentHebrewMonthCalendar.jewishDayOfMonth = 1
            currentHebrewMonthCalendar.back()
            currentHebrewMonthCalendar.jewishDayOfMonth = 1
            loadMonth(currentHebrewMonthCalendar)
        }

        btnNext.setOnClickListener {
            currentHebrewMonthCalendar.forward(Calendar.MONTH, 1)
            currentHebrewMonthCalendar.jewishDayOfMonth = 1
            loadMonth(currentHebrewMonthCalendar)
        }
    }


    class Builder(private val context: Context) {
        private var mode: PickerMode = PickerMode.CALENDAR
        private var outputType: OutputType = OutputType.BOTH // <--- ברירת מחדל: שניהם
        private var onHebrewSelected: ((HebrewDate) -> Unit)? = null
        private var onGregorianSelected: ((LocalDate) -> Unit)? = null

        fun setMode(mode: PickerMode) = apply { this.mode = mode }

        /**
         * מגדיר איזה סוג תאריך יוחזר כאשר יום נבחר.
         * יש לקרוא לפחות לאחד מהקולבקים (onHebrewSelected או onGregorianSelected).
         */
        fun setOutputType(type: OutputType) = apply { this.outputType = type } // <--- שיטה חדשה

        fun onHebrewSelected(callback: (HebrewDate) -> Unit) = apply { this.onHebrewSelected = callback }
        fun onGregorianSelected(callback: (LocalDate) -> Unit) = apply { this.onGregorianSelected = callback }

        fun build() = HebrewDatePickerDialog(
            context,
            mode,
            outputType, // <--- הוספת הפרמטר החדש
            onHebrewSelected,
            onGregorianSelected
        )
    }
}