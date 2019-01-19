package com.gregwoodfill.concordlunch

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class LunchController(val lunchService: LunchService) {

    @GetMapping("/api/lunch/{date}")
    fun getLunch(@PathVariable date: String): List<String> {
        return lunchService.getEntrees(LocalDate.parse(date))
    }

}