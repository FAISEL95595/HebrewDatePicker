package com.faisel.hebrewdatepicker.ui.calendar

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.faisel.hebrewdatepicker.R
import com.faisel.hebrewdatepicker.model.HebrewDate
import java.time.LocalDate

class CalendarAdapter(
    private val context: Context,
    private var days: List<Pair<LocalDate?, HebrewDate?>>,
    private val today: LocalDate,
    private val onDayClick: ((LocalDate, HebrewDate) -> Unit)? = null
) : BaseAdapter() {

    private val dayBackground = ContextCompat.getDrawable(context, R.drawable.square_background)
    private val todayHighlightBackground = ContextCompat.getDrawable(context, R.drawable.today_highlight)

    override fun getCount(): Int = days.size
    override fun getItem(position: Int): Any? = days[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = convertView as? TextView ?: TextView(context).apply {
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textSize = 14f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150
            )
            setLines(2)
        }

        val (gregorian, hebrew) = days[position]

        if (gregorian != null && hebrew != null) {
            textView.text = "${gregorian.dayOfMonth}\n${hebrew.dayGematria}"
            if (gregorian == today) {
                textView.background = todayHighlightBackground
                textView.setTextColor(Color.RED)
            } else {
                textView.background = dayBackground
                textView.setTextColor(Color.BLACK)
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

    fun updateDays(newDays: List<Pair<LocalDate?, HebrewDate?>>) {
        days = newDays
        notifyDataSetChanged()
    }
}