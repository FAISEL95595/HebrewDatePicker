package com.faisel.hebrewdatepicker.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import com.faisel.hebrewdatepicker.R
import com.faisel.hebrewdatepicker.model.HebrewDate
import com.faisel.hebrewdatepicker.ui.style.DatePickerStyle
import com.faisel.hebrewdatepicker.utils.DateConverter
import com.faisel.hebrewdatepicker.utils.HebrewDateUtils
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class HebrewDateWheelDialog internal constructor(
    context: Context,
    private val initialDate: Date,
    private val outputType: OutputType,
    private val style: DatePickerStyle,
    private val onHebrewSelected: ((HebrewDate) -> Unit),
    private val onGregorianSelected: ((LocalDate) -> Unit)
) : Dialog(context) {

    private lateinit var npDay: NumberPicker
    private lateinit var npMonth: NumberPicker
    private lateinit var npYear: NumberPicker
    private lateinit var btnSelect: Button
    private var tvHeader: TextView? = null

    private val hebrewMonths: Array<String> = (1..13).map {
        HebrewDateUtils.hebrewMonthName(it)
    }.toTypedArray()

    private val minYear = JewishCalendar(Date()).jewishYear - 100
    private val maxYear = JewishCalendar(Date()).jewishYear + 50

    init {
        setCancelable(true)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // **תיקון: יישום צבע רקע מהסטייל**
        style.backgroundColor?.let {
            window?.setBackgroundDrawable(ColorDrawable(it))
        } ?: run {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        setUpWheelUI()
    }

    private fun setUpWheelUI() {
        setContentView(R.layout.wheel_picker)

        npDay = findViewById(R.id.npDay)
        npMonth = findViewById(R.id.npMonth)
        npYear = findViewById(R.id.npYear)
        btnSelect = findViewById(R.id.btnWheelOk)
        tvHeader = findViewById(R.id.tvWheelMonthYear)

        tvHeader?.setTextColor(style.headerTextColor ?: Color.BLACK)
        style.headerTypeface?.let { tvHeader?.typeface = it }
        style.headerTextSize?.let { tvHeader?.textSize = it }

        btnSelect.setBackgroundColor(style.buttonBackgroundColor ?: Color.BLUE)
        btnSelect.setTextColor(style.buttonTextColor ?: Color.WHITE)

        val initialJc = JewishCalendar(initialDate)
        var currentYearHebrew = initialJc.jewishYear

        npMonth.minValue = 1
        npMonth.maxValue = 13 // מספר המקסימלי של חודשים (כולל אדר א' וב')
        npMonth.displayedValues = hebrewMonths

        npYear.minValue = minYear
        npYear.maxValue = maxYear
        npYear.value = currentYearHebrew

        updateDayPicker(initialJc.jewishYear, initialJc.jewishMonth)
        npDay.value = initialJc.jewishDayOfMonth

        npYear.setOnValueChangedListener { _, _, newVal ->
            currentYearHebrew = newVal
            updateDayPicker(currentYearHebrew, npMonth.value)
            updateHeader(currentYearHebrew, npMonth.value)
        }

        npMonth.setOnValueChangedListener { _, _, newVal ->
            updateDayPicker(npYear.value, newVal)
            updateHeader(npYear.value, newVal)
        }

        npDay.setOnValueChangedListener { _, _, newVal ->
        }

        updateHeader(currentYearHebrew, initialJc.jewishMonth)

        btnSelect.setOnClickListener {
            val day = npDay.value
            val month = npMonth.value
            val year = npYear.value

            val selectedJc = JewishCalendar().apply {
                jewishYear = year
                jewishMonth = month
                jewishDayOfMonth = day
            }

            val selectedGregorianDate = selectedJc.gregorianCalendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val selectedHebrewDateModel = DateConverter.gregorianToHebrew(selectedGregorianDate)

            onDateSelected(selectedGregorianDate, selectedHebrewDateModel)
        }
    }

    private fun updateDayPicker(year: Int, month: Int) {
        val jc = JewishCalendar().apply {
            jewishYear = year
            jewishMonth = month
        }

        val daysInMonth = jc.daysInJewishMonth
        val dayGematriaList = (1..daysInMonth).map {
            HebrewDateUtils.hebrewDayToGematria(it)
        }.toTypedArray()

        npDay.minValue = 1
        npDay.maxValue = daysInMonth
        npDay.displayedValues = dayGematriaList

        if (npDay.value > daysInMonth) {
            npDay.value = daysInMonth
        }
    }

    private fun updateHeader(year: Int, month: Int) {
        val monthName = hebrewMonths.getOrElse(month - 1) { "?" }
        tvHeader?.text = "$monthName ${HebrewDateUtils.hebrewYearToGematria(year)}"
    }

    private fun onDateSelected(gregorian: LocalDate, hebrew: HebrewDate) {
        when (outputType) {
            OutputType.HEBREW -> onHebrewSelected.invoke(hebrew)
            OutputType.GREGORIAN -> onGregorianSelected.invoke(gregorian)
            OutputType.BOTH -> {
                onGregorianSelected.invoke(gregorian)
                onHebrewSelected.invoke(hebrew)
            }
        }
        dismiss()
    }

    class Builder(private val context: Context) {
        private var outputType: OutputType = OutputType.BOTH
        private var initialDate: Date = Date()
        private var onHebrewSelected: ((HebrewDate) -> Unit)? = null
        private var onGregorianSelected: ((LocalDate) -> Unit)? = null
        private var style: DatePickerStyle = DatePickerStyle.default() // **תיקון: הוספת style**

        fun setDate(date: Date) = apply { this.initialDate = date }
        fun setOutputType(type: OutputType) = apply { this.outputType = type }
        fun onHebrewSelected(callback: (HebrewDate) -> Unit) = apply { this.onHebrewSelected = callback }
        fun onGregorianSelected(callback: (LocalDate) -> Unit) = apply { this.onGregorianSelected = callback }
        fun setStyle(style: DatePickerStyle) = apply { this.style = style } // **תיקון: הוספת setStyle**

        fun build() = HebrewDateWheelDialog(
            context,
            initialDate,
            outputType,
            style,
            onHebrewSelected ?: {},
            onGregorianSelected ?: {}
        )
    }
}