package com.faisel.hebrewdatepicker.ui

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.GridView
import android.widget.TextView
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.faisel.hebrewdatepicker.R
import com.faisel.hebrewdatepicker.model.HebrewDate
import com.faisel.hebrewdatepicker.ui.calendar.CalendarAdapter
import com.faisel.hebrewdatepicker.utils.DateConverter
import com.faisel.hebrewdatepicker.utils.HebrewDateUtils
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Calendar
import java.util.Locale
import com.faisel.hebrewdatepicker.ui.style.DatePickerStyle
class HebrewDatePickerDialog internal constructor(
    context: Context,
    private val mode: PickerMode,
    private val outputType: OutputType,
    private val onHebrewSelected: (HebrewDate) -> Unit,
    private val onGregorianSelected: (LocalDate) -> Unit,
    private val style: DatePickerStyle = DatePickerStyle.default()
) : Dialog(context) {

    private var currentHebrewMonthCalendar: JewishCalendar = JewishCalendar(Date())
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var tvMonthYear: TextView
    private var selectedDate: LocalDate = LocalDate.now()

    init {
        currentHebrewMonthCalendar.jewishDayOfMonth = 1
        setCancelable(true)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        style.backgroundColor?.let {
            window?.setBackgroundDrawable(ColorDrawable(it))
        } ?: run {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        if (mode == PickerMode.CALENDAR) {
            setUpCalendarUI(context)
        } else {
            dismiss()
        }
    }

    private fun setUpCalendarUI(context: Context) {
        setContentView(R.layout.calendar_picker)

        val btnPrev = findViewById<ImageButton>(R.id.btnPrevMonth)
        val btnNext = findViewById<ImageButton>(R.id.btnNextMonth)
        tvMonthYear = findViewById(R.id.tvMonthYear)
        val calendarGridView = findViewById<GridView>(R.id.gridDays)

        tvMonthYear.setTextColor(style.headerTextColor ?: Color.BLACK)
        style.headerTypeface?.let { tvMonthYear.typeface = it }
        style.headerTextSize?.let { tvMonthYear.textSize = it }

        calendarAdapter = CalendarAdapter(
            context,
            emptyList(),
            this::onDayClick,
            selectedDate,
            LocalDate.now(),
            style
        )
        calendarGridView.adapter = calendarAdapter

        loadMonth(currentHebrewMonthCalendar)

        btnPrev.setOnClickListener {
            // **תיקון לוגיקה חודש אחורה:** (כפי שסוכם)
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

    private fun loadMonth(jc: JewishCalendar) {
        val gregorianCal = jc.gregorianCalendar

        val hebrewMonthLine = "${HebrewDateUtils.hebrewMonthName(jc.jewishMonth)} ${HebrewDateUtils.hebrewYearToGematria(jc.jewishYear)}"

        // **תיקון שגיאת 'String?' (קו 101/129):** הוספת ?: "??" לאחר קריאה ל-getDisplayName
        val gregorianLine = (gregorianCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: "??") +
                " " + gregorianCal.get(Calendar.YEAR)

        tvMonthYear.text = "$hebrewMonthLine\n$gregorianLine"

        val daysList = createDaysList(jc)

        calendarAdapter.updateDays(daysList, selectedDate)
    }

    private fun createDaysList(jc: JewishCalendar): List<Pair<LocalDate?, HebrewDate?>> {
        val days = mutableListOf<Pair<LocalDate?, HebrewDate?>>()
        val jcStart = JewishCalendar(jc.gregorianCalendar.time).apply { jewishDayOfMonth = 1 }
        val gregorianStart = jcStart.gregorianCalendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

        val startDayOfWeek = gregorianStart.dayOfWeek.value % 7
        val emptyDaysCount = startDayOfWeek

        repeat(emptyDaysCount) { days.add(Pair(null, null)) }

        for (day in 1..jc.daysInJewishMonth) {
            jcStart.jewishDayOfMonth = day
            val localDate = jcStart.gregorianCalendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val hebrewDateModel = DateConverter.gregorianToHebrew(localDate)
            days.add(Pair(localDate, hebrewDateModel))
        }

        return days
    }

    private fun onDayClick(gregorian: LocalDate, hebrew: HebrewDate) {
        selectedDate = gregorian
        calendarAdapter.updateDays(calendarAdapter.days, selectedDate)

        when (outputType) {
            OutputType.HEBREW -> onHebrewSelected(hebrew)
            OutputType.GREGORIAN -> onGregorianSelected(gregorian)
            OutputType.BOTH -> {
                onGregorianSelected(gregorian)
                onHebrewSelected(hebrew)
            }
        }
        dismiss()
    }


    class Builder(private val context: Context) {
        private var mode: PickerMode = PickerMode.CALENDAR
        private var outputType: OutputType = OutputType.BOTH
        private var onHebrewSelected: ((HebrewDate) -> Unit)? = null
        private var onGregorianSelected: ((LocalDate) -> Unit)? = null
        private var style: DatePickerStyle = DatePickerStyle.default()

        fun setMode(mode: PickerMode) = apply { this.mode = mode }
        fun setOutputType(type: OutputType) = apply { this.outputType = type }
        fun setStyle(style: DatePickerStyle) = apply { this.style = style }

        fun onHebrewSelected(callback: (HebrewDate) -> Unit) = apply { this.onHebrewSelected = callback }
        fun onGregorianSelected(callback: (LocalDate) -> Unit) = apply { this.onGregorianSelected = callback }

        fun build() = HebrewDatePickerDialog(
            context,
            mode,
            outputType,
            onHebrewSelected ?: {},
            onGregorianSelected ?: {},
            style
        )
    }
}