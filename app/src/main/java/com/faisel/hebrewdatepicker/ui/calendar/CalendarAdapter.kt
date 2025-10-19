package com.faisel.hebrewdatepicker.ui.calendar

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat // ייבוא חדש
import com.faisel.hebrewdatepicker.R // נדרש כדי לגשת למשאבים שלנו
import com.faisel.hebrewdatepicker.model.HebrewDate
import java.time.LocalDate

class CalendarAdapter(
    private val context: Context,
    private var days: List<Pair<LocalDate?, HebrewDate?>>,
    private val onDayClick: ((LocalDate, HebrewDate) -> Unit)? = null
) : BaseAdapter() {

    // שמירת ה-Drawable בזיכרון פעם אחת
    private val dayBackground = ContextCompat.getDrawable(context, R.drawable.day_square_background)

    override fun getCount(): Int = days.size
    override fun getItem(position: Int): Any? = days[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = convertView as? TextView ?: TextView(context).apply {
            // הגדרות עיצוב בסיסיות לטקסט
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textSize = 14f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150 // גובה קבוע ליצירת ריבוע בקירוב (ניתן לשנות)
            )
            setLines(2) // ודא שיש מספיק מקום לשתי שורות (לועזי+עברי)
        }

        val (gregorian, hebrew) = days[position]

        if (gregorian != null && hebrew != null) {
            // יום חוקי - הצגת תאריכים
            textView.text = "${gregorian.dayOfMonth}\n${hebrew.dayGematria}"
            textView.setTextColor(Color.BLACK)

            // *** החלת הרקע (הריבוע) ***
            textView.background = dayBackground

            // טיפול בלחיצה
            textView.setOnClickListener {
                onDayClick?.invoke(gregorian, hebrew)
            }
        } else {
            // יום מילוי (Padding)
            textView.text = ""
            textView.setTextColor(Color.TRANSPARENT)
            // *** הסרת הרקע לימי מילוי ***
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