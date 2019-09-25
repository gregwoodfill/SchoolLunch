package com.gregwoodfill.concordlunch.handler

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LunchRequestHandlerTest {

    @Test
    fun `folds`() {

        val entreesAsString = listOf("spaghetti", "fun lunch", "tacos").foldIndexed("") { index, acc, s ->
            "$acc${index+1}. $s "
        }

        assertEquals("1. spaghetti 2. fun lunch 3. tacos ", entreesAsString)
    }
}