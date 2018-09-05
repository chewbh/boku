package io.boonlogic.boku.support.infra.config.svr.encryption

import io.boonlogic.util.encryption.AES256Encryptor
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.config.server.encryption.SingleTextEncryptorLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.encrypt.TextEncryptor

class AESEncryptor(
    keystoreFilePath: String,
    stashFilePath: String
): TextEncryptor{

    private val internalEncryptor: AES256Encryptor

    init {
        internalEncryptor = AES256Encryptor(keystoreFilePath, stashFilePath)
    }

    override fun encrypt(text: String?): String =
        internalEncryptor.encrypt(text?: throw IllegalArgumentException("plain text is empty"))


    override fun decrypt(encryptedText: String?) =
        internalEncryptor.decrypt(encryptedText?: throw IllegalArgumentException("encrypted text is empty"))
}

@ConfigurationProperties("custom.encryption")
data class EncryptionProperties(
    var enabled: Boolean = false,
    var keystoreFilePath: String = "",
    var stashFilePath: String = ""
)

@ConditionalOnProperty("custom.encryption.enabled", havingValue = "true")
@Configuration
@EnableConfigurationProperties(EncryptionProperties::class)
class EncryptionConfiguration(
    private val props: EncryptionProperties) {

    @Bean
    fun textEncryptorLocator() =
        SingleTextEncryptorLocator(AESEncryptor(props.keystoreFilePath, props.stashFilePath))

}
