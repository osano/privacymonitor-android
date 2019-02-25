package com.osano.privacymonitor.data

import org.junit.Assert
import org.junit.Test
import java.util.*

class ConvertersTest {

    private val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, 1998)
        set(Calendar.MONTH, Calendar.SEPTEMBER)
        set(Calendar.DAY_OF_MONTH, 4)
    }

    @Test
    fun calendarToDatestamp() {
        Assert.assertEquals(cal.timeInMillis, Converters().calendarToDatestamp(cal))
    }

    @Test
    fun datestampToCalendar() {
        Assert.assertEquals(Converters().datestampToCalendar(cal.timeInMillis), cal)
    }
}