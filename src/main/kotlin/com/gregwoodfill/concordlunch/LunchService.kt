package com.gregwoodfill.concordlunch

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.awaitResult
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.success
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class LunchService {
    val log = LoggerFactory.getLogger(LunchService::class.java)

    fun getEntrees(date: LocalDate): List<String> {
        val formatted = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(date)
        val (req, res, result) = Fuel.get("https://edinaschools.nutrislice.com/menu/api/weeks/school/concord/menu-type/lunch/$formatted/")
                .header("Accept" to "application/json")
                .responseString()

        when(result) {
            is Result.Success -> {
                val jsonString = result.get()

                val tree = jacksonObjectMapper().readTree(jsonString.toByteArray())

                val dates = tree.get("days")
                return dates.find { it["date"].textValue() == date.toString() }
                        ?.get("menu_items")?.filter { it["category"]?.textValue() == "entree" }?.map { it["food"]["name"].textValue() }?: listOf()
            }
            else ->{
                    log.error("error getting lunches, $res")
                    throw IllegalStateException("error getting lunches")
            }


        }


    }
}
