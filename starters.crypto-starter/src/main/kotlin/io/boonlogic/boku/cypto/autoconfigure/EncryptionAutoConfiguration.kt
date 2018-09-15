package io.boonlogic.boku.cypto.autoconfigure

import io.boonlogic.util.encryption.AES256Encryptor
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.config.server.encryption.CipherEnvironmentEncryptor
import org.springframework.cloud.config.server.encryption.EnvironmentEncryptor
import org.springframework.cloud.config.server.encryption.SingleTextEncryptorLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.encrypt.TextEncryptor

class AESEncryptor(
    keystoreFilePath: String,
    stashFilePath: String
): TextEncryptor {

    private val internalEncryptor: AES256Encryptor

    init {
        internalEncryptor = AES256Encryptor(keystoreFilePath, stashFilePath)
    }

    override fun encrypt(text: String?): String =
        internalEncryptor.encrypt(text?: throw IllegalArgumentException("plain text is empty"))

    override fun decrypt(encryptedText: String?): String {
        println("decrypting $encryptedText")
        return internalEncryptor.decrypt(encryptedText?: throw IllegalArgumentException("encrypted text is empty"))
    }

}

@ConfigurationProperties("app.encryption")
data class EncryptionProperties(
    var enabled: Boolean = false) {
    lateinit var keystoreFilePath: String
    lateinit var stashFilePath: String
}

@ConditionalOnProperty("app.encryption.enabled", havingValue = "true")
@ConditionalOnMissingClass("org.springframework.cloud.config.server.encryption.SingleTextEncryptorLocator")
@EnableConfigurationProperties(EncryptionProperties::class)
@Configuration
class EncryptionAutoConfiguration(
    private val props: EncryptionProperties) {

    @Primary
    @Bean
    fun aesEncryptor() = AESEncryptor(props.keystoreFilePath, props.stashFilePath)
}

@ConditionalOnProperty("app.encryption.enabled", havingValue = "true")
@ConditionalOnClass(SingleTextEncryptorLocator::class)
@EnableConfigurationProperties(EncryptionProperties::class)
@Configuration
class EncryptorLocatorAutoConfiguration(
    private val props: EncryptionProperties
) {

    @Primary
    @Bean
    fun aesEncryptor() = AESEncryptor(props.keystoreFilePath, props.stashFilePath)

    @Bean
    fun aesEncryptorLocator(aesEncryptor: AESEncryptor): SingleTextEncryptorLocator =
        SingleTextEncryptorLocator(aesEncryptor)

    @Bean
    fun environmentEncryptor(aesEncryptorLocator: SingleTextEncryptorLocator): EnvironmentEncryptor {
        return CipherEnvironmentEncryptor(aesEncryptorLocator)
    }
}


