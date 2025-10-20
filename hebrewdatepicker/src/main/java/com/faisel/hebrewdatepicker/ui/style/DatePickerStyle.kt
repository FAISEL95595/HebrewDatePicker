package com.faisel.hebrewdatepicker.ui.style

import android.graphics.Typeface
import androidx.annotation.ColorInt


data class DatePickerStyle(
    @ColorInt val backgroundColor: Int? = null,
    @ColorInt val textColor: Int? = null,
    @ColorInt val headerTextColor: Int? = null,
    @ColorInt val buttonBackgroundColor: Int? = null,
    @ColorInt val buttonTextColor: Int? = null,
    @ColorInt val selectedDayBackgroundColor: Int? = null,
    @ColorInt val selectedDayTextColor: Int? = null,
    @ColorInt val dayBackgroundColor: Int? = null,
    @ColorInt val dayTextColor: Int? = null,
    @ColorInt val todayHighlightColor: Int? = null,
    @ColorInt val wheelDividerColor: Int? = null,
    @ColorInt val wheelTextColor: Int? = null,
    val headerTypeface: Typeface? = null,
    val bodyTypeface: Typeface? = null,
    val headerTextSize: Float? = null,
    val bodyTextSize: Float? = null,
    val dayTextSize: Float? = null,
    val cornerRadius: Float? = null,
    val dialogPadding: Int? = null,
    val isRtl: Boolean = true
) {
    companion object {
        fun default() = DatePickerStyle()

        fun dark() = DatePickerStyle(
            backgroundColor = 0xFF1E1E1E.toInt(),
            textColor = 0xFFFFFFFF.toInt(),
            headerTextColor = 0xFFFFFFFF.toInt(),
            buttonBackgroundColor = 0xFF3700B3.toInt(),
            buttonTextColor = 0xFFFFFFFF.toInt(),
            selectedDayBackgroundColor = 0xFF03DAC5.toInt(),
            selectedDayTextColor = 0xFF000000.toInt(),
            dayTextColor = 0xFFFFFFFF.toInt(),
            todayHighlightColor = 0xFFBB86FC.toInt(),
            wheelDividerColor = 0xFFFFFFFF.toInt(),
            wheelTextColor = 0xFFFFFFFF.toInt(),
            cornerRadius = 16f
        )
    }
}