📅 Hebrew Date Picker Library

ספריית אנדרואיד (Kotlin) המספקת רכיב Date Picker Dialog לבחירת תאריכים בלוח השנה העברי. הספרייה משלבת את חישובי הזמנים העבריים ומאפשרת למפתחים לקבל את התאריך הנבחר בפורמט עברי ובפורמט גרגוריאני כאחד.

🛠️ טכנולוגיות ותלויות

שפת פיתוח Kotlin

מינימום SDK
26 ומעלה

🚀 התקנה

הוסף את התלות בספרייה לקובץ build.gradle.kts:

dependencies {
    implementation("com.github.FAISEL95595:HebrewDatePicker:1.0.0") 
}


📖 שימוש (דוגמאות בקוטלין)

1. ייבוא נדרש

import com.faisel.hebrewdatepicker.ui.HebrewDatePickerDialog
import com.faisel.hebrewdatepicker.ui.OutputType
import com.faisel.hebrewdatepicker.model.HebrewDate
import java.time.LocalDate 


2. קבלת פלט כפול (OutputType.BOTH)

כדי לקבל את התאריך העברי (HebrewDate) ואת התאריך הגרגוריאני (LocalDate) לאחר בחירה אחת, הגדר את ה-OutputType ל-BOTH וספק את שני ה-Callbacks:

fun openDualDatePicker(context: Context) {
    var hebrewDateOutput: HebrewDate? = null
    var gregorianDateOutput: LocalDate? = null
    
    HebrewDatePickerDialog.Builder(context)
        .setOutputType(OutputType.BOTH) // מגדיר פלט עברי וגרגוריאני

        .onHebrewSelected { hebrewDate: HebrewDate ->
            hebrewDateOutput = hebrewDate
              Toast.makeText(
                context,  
                Toast.LENGTH_LONG
            ).show()
        }
        .build()
        .show()
        }

        .onGregorianSelected { localDate: LocalDate ->
            gregorianDateOutput = localDate
            
            Toast.makeText(
                context,  
                Toast.LENGTH_LONG
            ).show()
        }
        .build()
        .show()
}


3. קבלת פלט עברי בלבד (OutputType.HEBREW)

HebrewDatePickerDialog.Builder(context)
    .setOutputType(OutputType.HEBREW) 
    .onHebrewSelected { hebrewDate: HebrewDate ->
    }
    .build()
    .show()
