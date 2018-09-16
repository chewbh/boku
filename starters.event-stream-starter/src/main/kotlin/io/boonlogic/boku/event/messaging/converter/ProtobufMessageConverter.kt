package io.boonlogic.boku.event.messaging.converter

import com.google.protobuf.Message
import org.slf4j.LoggerFactory
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.converter.AbstractMessageConverter
import org.springframework.messaging.converter.MessageConversionException
import org.springframework.util.MimeType
import java.nio.charset.StandardCharsets

private val DEFAULT_CHARSET = StandardCharsets.UTF_8
internal val PROTOBUF = MimeType("application", "x-protobuf", DEFAULT_CHARSET)

class ProtobufMessageConverter: AbstractMessageConverter(PROTOBUF) {

    val log = LoggerFactory.getLogger(ProtobufMessageConverter::class.java)

    override fun supports(p0: Class<*>): Boolean = Message::class.java.isAssignableFrom(p0)

    /** Converts the payload to serialized form */
    override fun convertToInternal(payload: Any, headers: MessageHeaders?, conversionHint: Any?): Any? =
        (payload as? Message)?.toByteArray() ?: 
        throw MessageConversionException("Failed to write payload as protobuf message")

    override fun convertFromInternal(
        message: org.springframework.messaging.Message<*>,
        targetClass: Class<*>,
        conversionHint: Any?
    ): Any? =
        try {
            targetClass
                .getMethod("parseFrom", ByteArray::class.java)
                .invoke(null, message.payload)
        } catch(e: Exception) {
            throw MessageConversionException("Failed to read protobuf message: ${e.message}", e)
        }
    
}