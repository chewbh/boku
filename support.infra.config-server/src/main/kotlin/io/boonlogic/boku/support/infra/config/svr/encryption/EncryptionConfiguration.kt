package io.boonlogic.boku.support.infra.config.svr.encryption

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.config.server.encryption.SingleTextEncryptorLocator
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.encrypt.TextEncryptor

object AESEncryptor: TextEncryptor{
    override fun encrypt(text: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decrypt(encryptedText: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

@ConfigurationProperties("encryption")
class EncryptionProperties

@Configuration
@EnableConfigurationProperties(EncryptionProperties::class)
class EncryptionConfiguration(
    props: EncryptionProperties) {

//    @Bean
    fun textEncryptorLocator() =
        SingleTextEncryptorLocator(AESEncryptor)

}
