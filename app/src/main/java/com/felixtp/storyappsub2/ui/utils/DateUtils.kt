package com.felixtp.storyappsub2.ui.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String.withDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    inputFormat.timeZone = TimeZone.getTimeZone("GMT")

    val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", Locale.US)
    val date = inputFormat.parse(this) as Date
    return outputFormat.format(date)
}