package com.faisel.hebrewdatepicker.ui

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import com.faisel.hebrewdatepicker.R
import com.faisel.hebrewdatepicker.model.HebrewDate
import com.faisel.hebrewdatepicker.utils.DateConverter
import com.faisel.hebrewdatepicker.utils.HebrewDateUtils
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import android.graphics.Color
import android.graphics.drawable.ColorDrawable


class HebrewDateWheelDialog internal constructor(
    context: Context,
    private val initialDate: Date,
    private val outputType: OutputType,
    private val onHebrewSelected: ((HebrewDate) -> Unit)? = null,
    private val onGregorianSelected: ((LocalDate) -> Unit)? = null
) : Dialog(context) {

    init {
        setCancelable(true)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setUpWheelUI()
    }

    private fun setUpWheelUI() {
        setContentView(R.layout.wheel_picker)

        val rootView = window?.decorView?.findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0)
        rootView?.setBackgroundColor(Color.WHITE)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


        val npDay = findViewById<NumberPicker>(R.id.npDay)
        val npMonth = findViewById<NumberPicker>(R.id.npMonth)
        val npYear = findViewById<NumberPicker>(R.id.npYear)
        val btnOk = findViewById<Button>(R.id.btnWheelOk)
        val tvHeader = findViewById<TextView>(R.id.tvWheelMonthYear)

        val initialJc = JewishCalendar(initialDate)
        val currentYearHebrew = initialJc.jewishYear

        val hebrewMonths = listOf(
            "ניסן", "אייר", "סיוון", "תמוז", "אב", "אלול",
            "תשרי", "חשוון", "כסלו", "טבת", "שבט", "אדר", "אדר ב׳"
        )

        val gregorianMonthNamesHebrew = mapOf(
            "JANUARY" to "ינואר", "FEBRUARY" to "פברואר", "MARCH" to "מרץ",
            "APRIL" to "אפריל", "MAY" to "מאי", "JUNE" to "יוני",
            "JULY" to "יולי", "AUGUST" to "אוגוסט", "SEPTEMBER" to "ספטמבר",
            "OCTOBER" to "אוקטובר", "NOVEMBER" to "נובמבר", "DECEMBER" to "דצמבר"
        )

        fun updateHeader(hebrewDay: Int, hebrewMonth: Int, hebrewYear: Int) {
            val tempJc = JewishCalendar()
            tempJc.jewishYear = hebrewYear
            tempJc.jewishMonth = hebrewMonth
            tempJc.jewishDayOfMonth = hebrewDay

            val gregorianDate = tempJc.gregorianCalendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val hebrewMonthName = hebrewMonths[hebrewMonth - 1]
            val hebrewYearGematria = HebrewDateUtils.hebrewYearToGematria(hebrewYear)

            val gregorianMonthNameEnglish = gregorianDate.month.name
            val gregorianMonthHebrew = gregorianMonthNamesHebrew[gregorianMonthNameEnglish] ?: gregorianMonthNameEnglish

            val hebrewLine = "$hebrewMonthName $hebrewYearGematria"
            val gregorianLine = "${gregorianDate.dayOfMonth} $gregorianMonthHebrew ${gregorianDate.year}"

            tvHeader?.text = "$hebrewLine\n$gregorianLine"
        }

        val minYear = currentYearHebrew - 10
        val maxYear = currentYearHebrew + 10
        npYear.minValue = minYear
        npYear.maxValue = maxYear

        val yearsArray = (minYear..maxYear).map { HebrewDateUtils.hebrewYearToGematria(it) }.toTypedArray()
        npYear.displayedValues = yearsArray
        npYear.value = currentYearHebrew

        npMonth.minValue = 1
        npMonth.maxValue = hebrewMonths.size
        npMonth.displayedValues = hebrewMonths.toTypedArray()
        npMonth.value = initialJc.jewishMonth

        fun updateDayPicker(year: Int, month: Int) {
            val tempJc = JewishCalendar()
            tempJc.jewishYear = year
            tempJc.jewishMonth = month

            val daysInMonth = tempJc.getDaysInJewishMonth()

            val daysArray = (1..daysInMonth).map { HebrewDateUtils.hebrewDayToGematria(it) }.toTypedArray()

            val oldValue = npDay.value

            npDay.displayedValues = null
            npDay.minValue = 1
            npDay.maxValue = daysInMonth
            npDay.displayedValues = daysArray

            val newDayValue = maxOf(1, minOf(oldValue, daysInMonth))
            npDay.value = newDayValue

            updateHeader(npDay.value, month, year)
        }

        npDay.value = initialJc.jewishDayOfMonth

        updateDayPicker(npYear.value, npMonth.value)



        npYear.setOnValueChangedListener { _, _, newYear ->
            updateDayPicker(newYear, npMonth.value)
        }
        npMonth.setOnValueChangedListener { _, _, newMonth ->
            updateDayPicker(npYear.value, newMonth)
        }
        npDay.setOnValueChangedListener { _, _, newDay ->
            updateHeader(newDay, npMonth.value, npYear.value)
        }

        btnOk.setOnClickListener {
            val selectedJc = JewishCalendar()
            selectedJc.jewishYear = npYear.value
            selectedJc.jewishMonth = npMonth.value
            selectedJc.jewishDayOfMonth = npDay.value

            val selectedGregorianDate = selectedJc.gregorianCalendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val selectedHebrewDateModel = DateConverter.gregorianToHebrew(selectedGregorianDate)

            onDateSelected(selectedGregorianDate, selectedHebrewDateModel)
        }
    }

    private fun onDateSelected(gregorian: LocalDate, hebrew: HebrewDate) {
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

    class Builder(private val context: Context) {
        private var outputType: OutputType = OutputType.BOTH
        private var initialDate: Date = Date()
        private var onHebrewSelected: ((HebrewDate) -> Unit)? = null
        private var onGregorianSelected: ((LocalDate) -> Unit)? = null


        fun setDate(date: Date) = apply { this.initialDate = date }

        fun setOutputType(type: OutputType) = apply { this.outputType = type }

        fun onHebrewSelected(callback: (HebrewDate) -> Unit) = apply { this.onHebrewSelected = callback }
        fun onGregorianSelected(callback: (LocalDate) -> Unit) = apply { this.onGregorianSelected = callback }

        fun build() = HebrewDateWheelDialog(
            context,
            initialDate,
            outputType,
            onHebrewSelected,
            onGregorianSelected
        )
    }
}