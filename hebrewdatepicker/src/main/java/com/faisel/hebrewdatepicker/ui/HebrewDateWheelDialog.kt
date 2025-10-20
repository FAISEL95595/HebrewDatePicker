package com.faisel.hebrewdatepicker.ui

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import com.faisel.hebrewdatepicker.R
import com.faisel.hebrewdatepicker.model.HebrewDate
import com.faisel.hebrewdatepicker.utils.DateConverter
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

/**
 * דיאלוג בורר תאריך עברי באמצעות גלגלות (Wheel/Spinner).
 * מחליף את מצב WHEEL מ-HebrewDatePickerDialog.
 */
class HebrewDateWheelDialog internal constructor(
    context: Context,
    private val outputType: OutputType,
    private val onHebrewSelected: ((HebrewDate) -> Unit)? = null,
    private val onGregorianSelected: ((LocalDate) -> Unit)? = null
) : Dialog(context) {

    private var initialDate: Date = Date()

    init {
        setCancelable(true)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setUpWheelUI()
    }

    private fun setUpWheelUI() {
        // הנחה: קיים קובץ R.layout.wheel_picker עם ID's: npDay, npMonth, npYear ו-btnWheelOk
        setContentView(R.layout.wheel_picker)

        val npDay = findViewById<NumberPicker>(R.id.npDay)
        val npMonth = findViewById<NumberPicker>(R.id.npMonth)
        val npYear = findViewById<NumberPicker>(R.id.npYear)
        val btnOk = findViewById<Button>(R.id.btnWheelOk)

        val currentYearHebrew = JewishCalendar(initialDate).jewishYear
        npYear.minValue = currentYearHebrew - 10
        npYear.maxValue = currentYearHebrew + 10
        npYear.value = currentYearHebrew

        val hebrewMonths = listOf(
            "ניסן", "אייר", "סיוון", "תמוז", "אב", "אלול",
            "תשרי", "חשוון", "כסלו", "טבת", "שבט", "אדר", "אדר ב׳"
        )
        npMonth.minValue = 1
        npMonth.maxValue = hebrewMonths.size
        npMonth.displayedValues = hebrewMonths.toTypedArray()
        npMonth.value = JewishCalendar(initialDate).jewishMonth

        fun updateDayPicker(year: Int, month: Int) {
            val tempJc = JewishCalendar()
            tempJc.jewishYear = year
            tempJc.jewishMonth = month

            val daysInMonth = tempJc.getDaysInJewishMonth()

            npDay.minValue = 1
            npDay.maxValue = daysInMonth
            npDay.value = minOf(npDay.value, daysInMonth)
        }

        updateDayPicker(npYear.value, npMonth.value)
        npDay.value = JewishCalendar(initialDate).jewishDayOfMonth

        npYear.setOnValueChangedListener { _, _, newYear ->
            updateDayPicker(newYear, npMonth.value)
        }
        npMonth.setOnValueChangedListener { _, _, newMonth ->
            updateDayPicker(npYear.value, newMonth)
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
        private var onHebrewSelected: ((HebrewDate) -> Unit)? = null
        private var onGregorianSelected: ((LocalDate) -> Unit)? = null

        fun setOutputType(type: OutputType) = apply { this.outputType = type }

        fun onHebrewSelected(callback: (HebrewDate) -> Unit) = apply { this.onHebrewSelected = callback }
        fun onGregorianSelected(callback: (LocalDate) -> Unit) = apply { this.onGregorianSelected = callback }

        fun build() = HebrewDateWheelDialog(
            context,
            outputType,
            onHebrewSelected,
            onGregorianSelected
        )
    }
}