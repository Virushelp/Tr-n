package com.artem.fitathlete.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val amountFormat = DecimalFormat("#,##0.##")

fun Double.pretty(): String = amountFormat.format(this)

fun formatDate(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy", Locale("ru")).format(Date(millis))

fun formatDateTime(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru")).format(Date(millis))
