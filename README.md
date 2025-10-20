📅 HebrewDatePicker

ספריית אנדרואיד קטנה וקלה לשימוש, המציגה רכיב Date Picker Dialog לבחירת תאריכים בלוח השנה העברי. הספרייה מאפשרת למפתחים לקבל את התאריך הנבחר בפורמט עברי (כאובייקט) או בפורמט גרגוריאני (כ-LocalDate).

🛠️ טכנולוגיות ודרישות

מאפיין

ערך

שפת פיתוח

Kotlin

מינימום SDK

26 (בשל שימוש ב-LocalDate)

תלות קריטית

KosherJava Zmanim (משמש כמנוע החישובים העבריים)

🚀 התקנה

כדי לשלב את הספרייה בפרויקט האנדרואיד שלך, הוסף את התלויות הבאות לקובץ build.gradle.kts (ברמת המודול):

// ודא שאתה מחליף 'גרסה_עדכנית' בגרסה המדויקת של ספריית הפיקר שלך.

dependencies {
    // 1. התלות בספריית ה-Picker (המודול הנוכחי)
    implementation("com.faisel.hebrewdatepicker:hebrewdatepicker:גרסה_עדכנית") 
    
    // 2. מנוע החישוב העברי - חובה!
    implementation(libs.zmanim) 
    
    // ודא ששאר התלויות הבסיסיות קיימות, כגון:
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}


📖 שימוש (דוגמאות בקוטלין)

הדיאלוג נוצר באמצעות תבנית Builder (בונה) גמישה.

1. ייבוא נדרש

import android.content.Context
import android.widget.Toast
import com.faisel.hebrewdatepicker.ui.HebrewDatePickerDialog
import com.faisel.hebrewdatepicker.ui.OutputType
import com.faisel.hebrewdatepicker.ui.PickerMode
import com.faisel.hebrewdatepicker.model.HebrewDate // פלט עברי
import java.time.LocalDate // פלט גרגוריאני (נדרש ב-SDK 26+)


2. קבלת פלט עברי (OutputType.HEBREW)

מקבלים אובייקט HebrewDate וממירים אותו למחרוזת מלאה באמצעות .toString().

fun openHebrewDatePicker(context: Context) {
    HebrewDatePickerDialog.Builder(context) 
        .setMode(PickerMode.CALENDAR) // ברירת מחדל: לוח שנה. ניתן לבחור גם SPINNER.
        .setOutputType(OutputType.HEBREW) 
        .onHebrewSelected { hebrewDate: HebrewDate ->
            
            val hebrewDateStr = hebrewDate.toString() 
            
            Toast.makeText(context, "תאריך עברי נבחר: $hebrewDateStr", Toast.LENGTH_LONG).show()
        }
        .build()
        .show()
}


3. קבלת פלט גרגוריאני (OutputType.GREGORIAN)

מקבלים אובייקט LocalDate גרגוריאני.

fun openGregorianDatePicker(context: Context) {
    HebrewDatePickerDialog.Builder(context)
        .setMode(PickerMode.SPINNER) // דוגמה לשימוש במצב גלילה
        .setOutputType(OutputType.GREGORIAN)
        .onGregorianSelected { localDate: LocalDate ->
            
            val gregorianDateStr = localDate.toString() // לדוגמה: "2025-01-20"
            
            Toast.makeText(context, "תאריך גרגוריאני נבחר: $gregorianDateStr", Toast.LENGTH_LONG).show()
        }
        .build()
        .show()
}


⚙️ מתודות Builder זמינות

מתודה

טיפוס

תיאור

Builder(Context)

Context

חובה. אתחול הבונה.

setMode(PickerMode)

PickerMode

בחירת ממשק המשתמש (CALENDAR או SPINNER).

setOutputType(OutputType)

OutputType

קביעת הפורמט שיוחזר (HEBREW או GREGORIAN).

setDate(Date)

Date

הגדרת תאריך התחלה ספציפי (אופציונלי).

onHebrewSelected { HebrewDate -> Unit }

Lambda

Callback לפלט עברי.

onGregorianSelected { LocalDate -> Unit }

Lambda

Callback לפלט גרגוריאני.
