package com.gregwoodfill.concordlunch

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

internal class LunchServiceTest {

    @Test
    fun `get lunches`() {
        val response = LunchService().getEntrees(LocalDate.now())

        response
    }
}