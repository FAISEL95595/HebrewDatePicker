package com.faisel.hebrewdatepicker.ui.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.faisel.hebrewdatepicker.model.HebrewDate
import java.time.LocalDate
import com.faisel.hebrewdatepicker.ui.style.DatePickerStyle

class CalendarAdapter(
    private val context: Context,
    internal var days: List<Pair<LocalDate?, HebrewDate?>>,
    private val onDayClick: ((LocalDate, HebrewDate) -> Unit)? = null,
    private var selectedDate: LocalDate? = null,
    private val todayDate: LocalDate = LocalDate.now(),
    private val style: DatePickerStyle = DatePickerStyle.default()
) : BaseAdapter() {

    // **תיקון: שינוי מ-ContextCompat.getDrawable(...) ל-null כדי למנוע Resource$NotFoundException**
    // ה-Drawable הפנימי אינו נמצא (ID 0). נגדיר אותו כ-null ונשתמש בהגדרות ה-style כברירת מחדל.
    private val baseDayBackground: Drawable? = null

    override fun getCount(): Int = days.size
    override fun getItem(position: Int): Any? = days[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = (convertView as? TextView ?: TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                parent!!.height / 6
            )
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            textSize = style.dayTextSize ?: 14f
            setTypeface(style.bodyTypeface)
        })

        val (gregorian, hebrew) = days[position]

        if (gregorian != null && hebrew != null) {
            textView.text = hebrew.dayGematria
            textView.visibility = View.VISIBLE

            if (gregorian == selectedDate) {
                val selectedDayColor = style.selectedDayBackgroundColor ?: R.color.blue_default
                textView.background = getStyledBackground(selectedDayColor, style.cornerRadius)
                textView.setTextColor(style.selectedDayTextColor ?: Color.WHITE)
            } else {
                val isToday = gregorian == todayDate
                if (isToday) {
                    val todayColor = style.todayHighlightColor ?: R.color.red_default
                    textView.background = getStyledBackground(todayColor, style.cornerRadius)
                    textView.setTextColor(style.dayTextColor ?: Color.BLACK)
                } else {
                    if (style.dayBackgroundColor != null) {
                        textView.background = getStyledBackground(style.dayBackgroundColor, style.cornerRadius)
                    } else if (baseDayBackground != null) {
                        textView.background = baseDayBackground
                    } else {
                        textView.background = null // לוודא שאין רקע אם baseDayBackground הוא null
                    }
                    textView.setTextColor(style.dayTextColor ?: Color.BLACK)
                }
            }

            textView.setOnClickListener {
                onDayClick?.invoke(gregorian, hebrew)
            }
        } else {
            textView.text = ""
            textView.setTextColor(Color.TRANSPARENT)
            textView.background = null
            textView.setOnClickListener(null)
        }

        return textView
    }

    fun updateDays(newDays: List<Pair<LocalDate?, HebrewDate?>>, newSelectedDate: LocalDate?) {
        days = newDays
        selectedDate = newSelectedDate
        notifyDataSetChanged()
    }

    private fun getStyledBackground(color: Int, cornerRadius: Float?): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.setColor(color)
        drawable.cornerRadius = cornerRadius?.let { dpToPx(it) } ?: 0f
        return drawable
    }

    private fun dpToPx(dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

    // שימוש בצבעים מוגדרים כאן כ-Int אם אין משאבי צבע רשמיים בפרויקט
    private object R {
        object color {
            const val blue_default = 0xFF2196F3.toInt()
            const val red_default = 0xFFF44336.toInt()
        }
    }
}