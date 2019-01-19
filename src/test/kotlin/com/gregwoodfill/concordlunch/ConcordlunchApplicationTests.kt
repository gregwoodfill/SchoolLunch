package com.gregwoodfill.concordlunch

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gargoylesoftware.htmlunit.Page
import com.gargoylesoftware.htmlunit.WebClient
import com.google.gson.Gson
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ConcordlunchApplicationTests {

	@Test
	fun contextLoads() {

		val date = LocalDate.parse("2019-02-11")
		val formatted = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(date)
		val jsonString = RestTemplate().getForObject<String>("https://edinaschools.nutrislice.com/menu/api/weeks/school/concord/menu-type/lunch/$formatted")
		val tree = jacksonObjectMapper().readTree(jsonString!!.toByteArray())

		val dates = tree.get("days")
		val entrees = dates.find { it["date"].textValue() == date.toString() }
				?.get("menu_items")?.filter { it["category"]?.textValue() == "entree" }?.map { it["food"]["description"] }

		println(entrees)
	}

}

