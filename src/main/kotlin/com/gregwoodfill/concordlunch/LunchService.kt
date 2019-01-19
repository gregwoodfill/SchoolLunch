package com.gregwoodfill.concordlunch

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.tomcat.jni.Local
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Component
class LunchService {
    fun getEntrees(date: LocalDate): List<String> {
        val formatted = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(date)
        val jsonString = RestTemplate().getForObject<String>("https://edinaschools.nutrislice.com/menu/api/weeks/school/concord/menu-type/lunch/$formatted")
        val tree = jacksonObjectMapper().readTree(jsonString!!.toByteArray())

        val dates = tree.get("days")
        return dates.find { it["date"].textValue() == date.toString() }
                ?.get("menu_items")?.filter { it["category"]?.textValue() == "entree" }?.map { it["food"]["description"].textValue() }?: listOf()
    }
}
