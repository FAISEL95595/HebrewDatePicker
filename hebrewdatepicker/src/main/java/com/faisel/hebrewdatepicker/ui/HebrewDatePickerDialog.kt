package com.faisel.hebrewdatepicker.ui

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageButton
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

class HebrewDatePickerDialog internal constructor(
    context: Context,
    private val mode: PickerMode,
    private val outputType: OutputType,
    private val onHebrewSelected: ((HebrewDate) -> Unit)? = null,
    private val onGregorianSelected: ((LocalDate) -> Unit)? = null
) : Dialog(context) {

    companion object {
        private val special_dates: Map<Pair<Int, Int>, Int> = mapOf(
            //תשרי
            Pair(7, 1) to R.drawable.chag_highlight,
            Pair(7, 2) to R.drawable.chag_highlight,
            Pair(7, 10) to R.drawable.chag_highlight,
            Pair(7, 15) to R.drawable.chag_highlight,
            Pair(7, 16) to R.drawable.moed_highlight,
            Pair(7, 17) to R.drawable.moed_highlight,
            Pair(7, 18) to R.drawable.moed_highlight,
            Pair(7, 19) to R.drawable.moed_highlight,
            Pair(7, 20) to R.drawable.moed_highlight,
            Pair(7, 21) to R.drawable.moed_highlight,
            Pair(7, 22) to R.drawable.chag_highlight,
            //אדר
            Pair(12, 14) to R.drawable.moed_highlight,
            Pair(12, 15) to R.drawable.moed_highlight,
            Pair(13, 14) to R.drawable.moed_highlight,
            Pair(13, 15) to R.drawable.moed_highlight,
            //ניסן
            Pair(1, 15) to R.drawable.chag_highlight,
            Pair(1, 16) to R.drawable.moed_highlight,
            Pair(1, 17) to R.drawable.moed_highlight,
            Pair(1, 18) to R.drawable.moed_highlight,
            Pair(1, 19) to R.drawable.moed_highlight,
            Pair(1, 20) to R.drawable.moed_highlight,
            Pair(1, 21) to R.drawable.chag_highlight,
            //אייר
            Pair(2, 14) to R.drawable.moed_highlight,
            Pair(2, 18) to R.drawable.moed_highlight,
            //סיוון
            Pair(3, 6) to R.drawable.chag_highlight
        )
    }

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
        val btnPrev = findViewById<ImageButton>(R.id.btnPrevMonth)
        val btnNext = findViewById<ImageButton>(R.id.btnNextMonth)

        val gregorianMonthNamesHebrew = mapOf(
            "JANUARY" to "ינואר", "FEBRUARY" to "פברואר", "MARCH" to "מרץ",
            "APRIL" to "אפריל", "MAY" to "מאי", "JUNE" to "יוני",
            "JULY" to "יולי", "AUGUST" to "אוגוסט", "SEPTEMBER" to "ספטמבר",
            "OCTOBER" to "אוקטובר", "NOVEMBER" to "נובמבר", "DECEMBER" to "דצמבר"
        )

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
                days.add(Pair(null, null))
            }

            val daysInMonth = jc.getDaysInJewishMonth()

            var currentDate = startGregorianDate

            for (i in 1..daysInMonth) {
                val hebrew = DateConverter.gregorianToHebrew(currentDate)
                days.add(Pair(currentDate, hebrew))

                currentDate = currentDate.plusDays(1)
            }
            val today = LocalDate.now(ZoneId.systemDefault())

            val adapter = CalendarAdapter(context, days, today, special_dates) { gregorian, hebrew ->
                when (outputType) {
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

            val firstHebrewDateModel = DateConverter.gregorianToHebrew(startGregorianDate)

            val hebrewMonthLine = "${firstHebrewDateModel.monthName} ${firstHebrewDateModel.yearGematria}"

            val gregorianMonthNameEnglish = startGregorianDate.month.name
            val gregorianMonthHebrew = gregorianMonthNamesHebrew[gregorianMonthNameEnglish] ?: gregorianMonthNameEnglish
            val gregorianLine = "${gregorianMonthHebrew} ${startGregorianDate.year}"

            tvMonthYear.text = "$hebrewMonthLine\n$gregorianLine"
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
        private var outputType: OutputType = OutputType.BOTH
        private var onHebrewSelected: ((HebrewDate) -> Unit)? = null
        private var onGregorianSelected: ((LocalDate) -> Unit)? = null

        fun setMode(mode: PickerMode) = apply { this.mode = mode }

        fun setOutputType(type: OutputType) = apply { this.outputType = type }

        fun onHebrewSelected(callback: (HebrewDate) -> Unit) = apply { this.onHebrewSelected = callback }
        fun onGregorianSelected(callback: (LocalDate) -> Unit) = apply { this.onGregorianSelected = callback }

        fun build() = HebrewDatePickerDialog(
            context,
            mode,
            outputType,
            onHebrewSelected,
            onGregorianSelected
        )
    }
}