package com.deepvisiontech.thecomicinator3000.core.data.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeUtils {
    fun millisToDateString(
        millis: Long,
        pattern: String = "dd-MM-yyyy",
        zone: ZoneId = ZoneId.systemDefault()
    ): String {
        return Instant.ofEpochMilli(millis)
            .atZone(zone)
            .format(DateTimeFormatter.ofPattern(pattern))
    }

    fun millisToIsoUtc(millis: Long): String {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ISO_INSTANT)
    }

    fun nowAsString(
        pattern: String = "yyyy-MM-dd HH:mm:ss",
        zone: ZoneId = ZoneId.systemDefault()
    ): String = millisToDateString(System.currentTimeMillis(), pattern, zone)
}