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

    private val baseDayBackground: Drawable? = ContextCompat.getDrawable(context, R.drawable.day_square_background)

    override fun getCount(): Int = days.size
    override fun getItem(position: Int): Any? = days[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = (convertView as? TextView ?: TextView(context).apply {
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textSize = style.dayTextSize ?: 14f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150
            )
            setLines(2)
            style.bodyTypeface?.let { typeface = it }
        })

        val (gregorian, hebrew) = days[position]

        if (gregorian != null && hebrew != null) {
            textView.text = "${gregorian.dayOfMonth}\n${hebrew.dayGematria}"
            textView.setTextColor(style.dayTextColor ?: Color.BLACK)
            textView.background = baseDayBackground

            when {
                gregorian == selectedDate -> {
                    val color = style.selectedDayBackgroundColor ?: ContextCompat.getColor(context, R.color.blue_default)
                    textView.background = getStyledBackground(color, style.cornerRadius)
                    textView.setTextColor(style.selectedDayTextColor ?: Color.WHITE)
                }
                gregorian.isEqual(todayDate) -> {
                    val color = style.todayHighlightColor ?: ContextCompat.getColor(context, R.color.red_default)
                    textView.background = getStyledBackground(color, style.cornerRadius)
                    textView.setTextColor(style.headerTextColor ?: Color.BLACK)
                }
                else -> {
                    style.dayBackgroundColor?.let {
                        textView.background = getStyledBackground(it, style.cornerRadius)
                    } ?: run {
                        textView.background = baseDayBackground
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

    private object R {
        object color {
            const val blue_default = 0xFF2196F3.toInt()
            const val red_default = 0xFFF44336.toInt()
        }
        object drawable {
            const val day_square_background = 0
        }
    }
}