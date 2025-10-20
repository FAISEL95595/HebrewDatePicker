 HebrewDatePicker: בורר תאריך עברי מקיף לאנדרואיד

ספריית אנדרואיד פשוטה וגמישה המאפשרת בחירה קלה של תאריכים עבריים או המרתם לתאריך גרגוריאני (לועזי). הספרייה מציעה שתי תצוגות: לוח שנה (Calendar) וגלגלת (Wheel/Spinner).

1. התקנה

1.1. תלות בגריידל

כדי להשתמש בספרייה, הוסף את התלות (Dependency) הבאה לקובץ ה-Gradle של המודול שלך (app/build.gradle.kts):

// build.gradle.kts (רמת מודול: app)

dependencies {
    implementation("com.github.FAISEL95595:HebrewDatePicker:1.5.1") 
}

2. שימוש והפעלה (דוגמאות)

הספרייה מספקת שני דיאלוגים ראשיים: HebrewDateWheelDialog (גלגלת) ו-HebrewDatePickerDialog (לוח שנה).

2.1. ייבוא נדרש

בפעילות (Activity) או בפרגמנט שבו אתה מציג את הדיאלוג:

import com.faisel.hebrewdatepicker.ui.HebrewDateWheelDialog // אם אתה רוצה ספינר/גלגלת
import com.faisel.hebrewdatepicker.ui.HebrewDatePickerDialog // אם אתה רוצה בורר רגיל
import com.faisel.hebrewdatepicker.ui.OutputType
import com.faisel.hebrewdatepicker.ui.PickerMode
import com.faisel.hebrewdatepicker.model.HebrewDate
import java.time.LocalDate // אם אתה רוצה שיחזיר תאריך גרגוריאני
import java.time.ZoneId // נדרש להמרה ל-Timestamp
import java.time.format.DateTimeFormatter // אם אתה רוצה לשנות את הפורמט של התאריך


2.2. הפעלת בורר הגלגלת (HebrewDateWheelDialog)

הדיאלוג הזה משתמש בתצוגת גלגלות לבחירת יום, חודש ושנה.

private fun showHebrewWheelDialog() {
    // הגדרת תאריך התחלתי (לדוגמה, התאריך שנבחר כרגע או Date())
    val initialDate = selectedCalendar.time 
    HebrewDateWheelDialog.Builder(this)
        .setDate(initialDate) 
        .setOutputType(OutputType.GREGORIAN) // מחזיר רק תאריך לועזי (LocalDate)
        .onGregorianSelected { selectedDate ->   
            // --- אפשרות א: שינוי פורמט התאריך (מחרוזת) ---
            val formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy") // לדוגמה: יום, 20/10/2025
            val formattedDateString = selectedDate.format(formatter)
            // --- אפשרות ב: קבלת Timestamp ---
            val timestampInMilliseconds: Long = selectedDate
                .atStartOfDay(ZoneId.systemDefault()) 
                .toInstant()
                .toEpochMilli()
            // עדכון הממשק
            binding.tvGregorianDate.text = formattedDateString
            // ... שמירת ה-Timestamp ...
        }
        .onHebrewSelected { hebrewDate ->
             // קולבק זה יופעל גם אם OutputType.BOTH מוגדר
             // hebrewDate מכיל: יום (Int), חודש (Int), שנה (Int), חודש עברי (String)
             // Toast.makeText(this, "התאריך העברי: ${hebrewDate.hebrewMonthName} ${hebrewDate.hebrewDay}", Toast.LENGTH_SHORT).show()
        }
        .build()
        .show()
}


2.3. הפעלת בורר לוח השנה (HebrewDatePickerDialog)

הדיאלוג הזה משתמש בתצוגת לוח שנה.

private fun showHebrewCalendarDialog() {
    // שימו לב: HebrewDatePickerDialog דורש גם הגדרת PickerMode
    HebrewDatePickerDialog.Builder(this)
        .setMode(PickerMode.CALENDAR)
        .setOutputType(OutputType.BOTH) // יחזיר גם עברי וגם לועזי
        // אין אפשרות setDate בפיקר הזה, הוא מתחיל מהתאריך הנוכחי
        .onGregorianSelected { selectedDate -> 
            // ... לוגיקה ...
        }
        .onHebrewSelected { hebrewDate ->
             // ... לוגיקה ...
        }
        .build()
        .show()
}


3. מבנה ואפשרויות API מפורטות

3.1. מחלקת OutputType

זהו enum class המגדיר את הפורמט של התוצאה שתחזור מהדיאלוג. יש לקרוא לאחד מהקולבקים המתאימים (onGregorianSelected או onHebrewSelected) בהתאם ל-OutputType שבחרת.



.setOutputType(OutputType.HEBREW)
.setOutputType(OutputType.GREGORIAN)
.setOutputType(OutputType.BOTH)




HEBREW
יחזיר רק את התאריך העברי.
.onHebrewSelected
com.faisel.hebrewdatepicker.model.HebrewDate





GREGORIAN
יחזיר רק את התאריך הלועזי.
.onGregorianSelected
java.time.LocalDate




BOTH
יחזיר את שני סוגי התאריכים.
אחד או שני הקולבקים
LocalDate ו-HebrewDate

3.2. מחלקת PickerMode (בשימוש רק ב-HebrewDatePickerDialog)

זהו enum class המגדיר את מצב התצוגה של הדיאלוג (לוח שנה או גלגלות).


CALENDAR
תצוגת לוח שנה חודשית מסורתית.

3.3. הקולבקים (Callbacks)
.onGregorianSelected { selectedDate -> ... }
מופעל כשהמשתמש מסיים לבחור תאריך. מחזיר אובייקט LocalDate.
(LocalDate) -> Unit
GREGORIAN או BOTH

.onHebrewSelected { hebrewDate -> ... }

מופעל כשהמשתמש מסיים לבחור תאריך. מחזיר מודל HebrewDate.
(HebrewDate) -> Unit
HEBREW או BOTH

4. המרת התאריך (גמישות בפורמט ו-Timestamp)

הספרייה מחזירה אובייקטים מובנים (LocalDate ו-HebrewDate), המאפשרים לך לעצב את הפלט כרצונך.

4.1. שליטה מלאה בפורמט ה-String

כדי לשנות את סדר היום/חודש/שנה או את אופן ההצגה (כמו הוספת שם יום או חודש מלא), השתמש ב-DateTimeFormatter בתוך הקולבק:

.onGregorianSelected { selectedDate ->
    // יום בשבוע מלא, יום נומרי, חודש נומרי, שנה מלאה (לדוגמה: Monday, 20-10-2025)
    val customFormatter = DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy") 
    val dateDisplay = selectedDate.format(customFormatter)
    // ...
}


4.2. המרה ל-Timestamp (מספר)
כדי לקבל את התאריך כ-Timestamp (Long - מספר המילישניות מאז $1970$):
.onGregorianSelected { selectedDate ->
    val timestampInMilliseconds: Long = selectedDate
        .atStartOfDay(ZoneId.systemDefault()) // המרת תחילת היום באזור זמן מקומי
        .toInstant()
        .toEpochMilli() // התוצאה היא מספר ה-Timestamp
    // Toast.makeText(this, "Timestamp: $timestampInMilliseconds", Toast.LENGTH_LONG).show()
}
