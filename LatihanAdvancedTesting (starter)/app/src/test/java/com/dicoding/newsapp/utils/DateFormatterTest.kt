package com.dicoding.newsapp.utils

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import java.time.format.DateTimeParseException

class DateFormatterTest {
    @Test
    fun `Given correct ISO 8601 format then should format correctly`() {
        val currentDate = "2022-02-02T10:10:10Z"
        Assert.assertEquals("02 Feb 2022 | 17:10", DateFormatter.formatDate(currentDate, "Asia/Jakarta"))
        Assert.assertEquals("03 Des 2024 | 18:31", DateFormatter.formatDate(currentDate, "Asia/Jakarta"))

    }

    @Test
    fun `Given wrong ISO 8601 format then should throw error`() {
        val wrongFormat = "2022-02-02T10:10"
        Assert.assertThrows(DateTimeParseException::class.java) {
            DateFormatter.formatDate(wrongFormat, "Asia/Jakarta")
        }
    }

    @Test
    fun `Biven invalid timezone then should throw error`() {
        val wrongFormat = "2022-02-02T10:10:10Z"
        Assert.assertThrows(DateTimeParseException::class.java) {
            DateFormatter.formatDate(wrongFormat, "Asia/Bandung")
        }
    }
}