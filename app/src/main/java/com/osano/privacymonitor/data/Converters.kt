package com.osano.privacymonitor.data

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter fun datestampToCalendar(value: Long): Calendar = Calendar.getInstance().apply { timeInMillis = value }
}