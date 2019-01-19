package com.gregwoodfill.concordlunch

import com.google.actions.api.ActionRequest
import com.google.actions.api.ActionsSdkApp
import com.google.actions.api.DefaultApp
import com.google.actions.api.ForIntent
import com.google.actions.api.response.ResponseBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ConcordlunchApplication

fun main(args: Array<String>) {
	runApplication<ConcordlunchApplication>(*args)
}

@ForIntent("date")
class ConcordLunchApp : ActionsSdkApp() {
	override fun createRequest(inputJson: String, headers: Map<*, *>?): ActionRequest {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getResponseBuilder(request: ActionRequest): ResponseBuilder {
		TODO()
	}
}
