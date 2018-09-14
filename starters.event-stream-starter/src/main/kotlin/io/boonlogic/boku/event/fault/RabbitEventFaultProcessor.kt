package io.boonlogic.boku.event.fault

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

private const val X_RETRIES_HEADER = "x-retries"

private const val MQ_PROPS_DLQ_ENABLED = "spring.cloud.stream.rabbit.bindings.input.consumer.autoBindDlq"
private const val MQ_PROPS_TOPIC = "spring.cloud.stream.bindings.input.destination"
private const val MQ_PROPS_CONSUMER_GROUP = "spring.cloud.stream.bindings.input.group"

private const val MAX_RETRIES = 1

@Service
@ConditionalOnClass(RabbitTemplate::class)
@ConditionalOnProperty(MQ_PROPS_DLQ_ENABLED, havingValue = "true")
class RabbitEventFaultProcessor(
    private val rabbitTemplate: RabbitTemplate,
    @Value("\${${MQ_PROPS_TOPIC}}") private val topic: String,
    @Value("\${${MQ_PROPS_CONSUMER_GROUP}}") private val consumerGroup: String
) {
    private val originalQueue get() = "$topic.$consumerGroup"
    private val dlq get() = "$originalQueue.dlq"

    private val log = LoggerFactory.getLogger(RabbitEventFaultProcessor::class.java)

    @RabbitListener(queues = ["\${${MQ_PROPS_TOPIC}}.\${${MQ_PROPS_CONSUMER_GROUP}}.dlq"])
    fun handleFault(faultMessage: Message) {
        val retriesCount = faultMessage.messageProperties.headers[X_RETRIES_HEADER] as? Int ?: 0
        if(meetRetryConditions(faultMessage) &&
            retriesCount < MAX_RETRIES) {
                faultMessage.messageProperties.headers[X_RETRIES_HEADER] = retriesCount + 1
                rabbitTemplate.send(originalQueue, faultMessage)
                log.info("Fault occurred. requeue / retry = {} of {}: {}", retriesCount + 1, MAX_RETRIES, faultMessage)
        } else {
            // time to kill message
            handleFailure(faultMessage)
        }
    }

    fun meetRetryConditions(msg: Message): Boolean {
        // check is recoverable i/o error (network connectivity problems)
        val errorMessage = (msg.messageProperties.headers["x-exception-message"] as? String ?: "").toLowerCase()
        if(errorMessage.contains("connection reset") ||
            errorMessage.contains("timed out") ||
            errorMessage.contains("i/o error occurred while sending to the backend")) {
            return true
        }
        return false
    }

    fun handleFailure(msg: Message) {

        // val stacktrace = msg.messageProperties.headers["x-exception-stacktrace"] as? String ?: ""
        // x-exception-message =  event error = 0.1250...
        // x-exception-stacktrace
        // failedMessage 

        // wrong message format

        log.error("killed {}", msg)
    }
}