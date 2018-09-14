package io.boonlogic.boku.event.messaging

import com.google.protobuf.Message
import io.boonlogic.boku.event.messaging.converter.PROTOBUF
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder

private const val X_PROTOBUF_SCHEMA_HEADER = "x-protobuf-schema"
private const val X_PROTOBUF_MESSAGE_HEADER = "x-protobuf-message"

fun Message.springCloudStreamMessageBuilder(): MessageBuilder<Message> =
    MessageBuilder.withPayload(this)
        .setHeader(MessageHeaders.CONTENT_TYPE, PROTOBUF)
        .setHeader(X_PROTOBUF_SCHEMA_HEADER, this.descriptorForType.file.name)
        .setHeader(X_PROTOBUF_MESSAGE_HEADER, this.descriptorForType.fullName)

fun Message.toSpringCloudStreamMessage() =
    this.springCloudStreamMessageBuilder().build()

