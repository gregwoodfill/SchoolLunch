package com.gregwoodfill.concordlunch.handler

import com.amazon.ask.SkillStreamHandler
import com.amazon.ask.Skills
import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.*
import com.amazon.ask.model.ui.Card
import com.amazon.ask.request.Predicates.intentName
import com.amazon.ask.request.Predicates.requestType
import com.amazon.ask.request.handler.GenericRequestHandler
import com.gregwoodfill.concordlunch.LunchService
import java.util.*
import java.util.Optional
import com.amazon.ask.request.Predicates.intentName
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase.getRequestId
import com.amazon.ask.request.Predicates.requestType
import org.slf4j.LoggerFactory
import com.amazon.ask.request.Predicates.intentName
import java.time.*
import java.time.format.DateTimeFormatter


class AwsRequestHandler : SkillStreamHandler(lunchSkill()) {

    companion object {
        fun lunchSkill() = Skills.standard().withSkillId("amzn1.ask.skill.af3b0be2-4d76-4c47-bf55-de823e584336")
                .addRequestHandlers(
                        CancelandStopIntentHandler(),
                        LaunchRequestHandler(),
                        LunchRequestHandler(),
                        HelpIntentHandler(),
                        SessionEndedRequestHandler(),
                        FallBackIntentHandler())
                .build()
    }

}

class LunchRequestHandler :  RequestHandler {
    override fun canHandle(input: HandlerInput): Boolean {
        println("requestType: ${input.requestEnvelope.request.type}")
        val request = input.requestEnvelope.request

        if(request is IntentRequest) {
            println("Intent: ${request.intent.name}")
        }
        val canHandle = input.requestEnvelope.request.type == "IntentRequest"
                && (input.requestEnvelope.request as IntentRequest).intent.name == "GetLunchIntent"

        println("lunch request can be handled $canHandle")
        return canHandle
    }

    override fun handle(input: HandlerInput): Optional<Response> {
        println("got input = $input")
        val intent = (input.request as IntentRequest).intent

        val slot = intent.slots.get("on_date")
        println("got ${slot?.value}")
        val date = when (slot?.value) {
            null -> {
                if(LocalTime.now().isAfter(LocalTime.of(12, 0))) {
                    LocalDate.now().plusDays(1)
                }
                else {
                    LocalDate.now()
                }
            }
            else -> LocalDate.parse(slot.value, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }

        println("got date: $date")

        val entrees = LunchService().getEntrees(date)
        println("entrees: $entrees")

        val speechText = when {
            date.isBefore(LocalDate.now()) -> "I can't get a menu in the for the date: $date."
            date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY -> "I can only get menus for school days."
            entrees.isEmpty() -> "I'm sorry, I couldn't find a menu, it may be a holiday."
            else -> "Lunch at Concord elementary for ${date.dayOfWeek}, ${date.month} ${date.dayOfMonth} ${date.year}. " + entrees.foldIndexed("") { index, acc, s ->
                "${acc}Option ${index+1}. $s. "
            }
        }
        return input.responseBuilder
                .withSpeech(speechText)
                .build()
    }

}

class HelpIntentHandler : RequestHandler {
    override fun canHandle(input: HandlerInput): Boolean {
        return input.matches(intentName("AMAZON.HelpIntent"));
    }

    override fun handle(input: HandlerInput): Optional<Response> {
        val speechText = "You can ask me: what's for lunch at concord today"
        return input.responseBuilder
                .withSpeech(speechText)
                .withReprompt(speechText).build()
    }

}

class LaunchRequestHandler : RequestHandler {
    override fun canHandle(input: HandlerInput): Boolean {
        println("launch request handler: type: ${input.request.type}")
        return input.matches(requestType(LaunchRequest::class.java))
    }

    override fun handle(input: HandlerInput): Optional<Response> {
            println("launching from $input")

            return input.responseBuilder.withSpeech("Welcome to Concord lunch")
                    .withReprompt("What day's menu would you like to see?")
                    .build()

    }
}

class CancelandStopIntentHandler : RequestHandler {
    override fun canHandle(input: HandlerInput): Boolean {
        return input.matches(intentName("AMAZON.StopIntent").or(intentName("AMAZON.CancelIntent")).or(intentName("AMAZON.NoIntent")))
    }

    override fun handle(input: HandlerInput): Optional<Response> {
        val speechText = "Bon apetit."
        return input.responseBuilder
                .withSpeech(speechText)
                .withReprompt(speechText)
                .build()
    }
}

class SessionEndedRequestHandler : RequestHandler {

    override fun canHandle(input: HandlerInput): Boolean {
        return input.requestEnvelope.request.type == "SessionEnded"
    }

    override fun handle(input: HandlerInput): Optional<Response> {
        val envelope = input.requestEnvelope
        log.info("onSessionEnded requestId={}, sessionId={}", envelope.request.requestId,
                envelope.session.sessionId)
        // any cleanup logic goes here
        return input.responseBuilder.build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(SessionEndedRequestHandler::class.java)
    }

}


class FallBackIntentHandler : RequestHandler {

    override fun canHandle(input: HandlerInput): Boolean {
        return input.matches(intentName("AMAZON.FallbackIntent"))
    }

    override fun handle(input: HandlerInput): Optional<Response> {
        val speechText = "Sorry, I don't know that. Would you like to ask what's for lunch today?"
        return input.responseBuilder
                .withSpeech(speechText)
                .withReprompt(speechText)
                .build()
    }

}