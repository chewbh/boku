package io.boonlogic.util.encryption

import java.nio.file.Paths
import java.security.Key
import java.security.KeyStore
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec



internal const val ENCRYPT_ALGO_PBKDF2_WITH_HMAC_SHA1 = "PBKDF2WithHmacSHA1"
internal const val ENCRYPT_ALGO_PBKDF2_WITH_HMAC_SHA512 = "PBKDF2WithHmacSHA512"
internal const val KEY_STORE_TYPE = "JCEKS"

private const val KEY_LENGTH = 256
private const val ITERATION_COUNT = 65536
private const val SECERT_KEY_ALIAS = "SECRET_KEY"

class AES256Encryptor(keystoreFilePath: String, stashFilePath: String) {

    private val keystore: KeyStore
    private val keystorePassword: CharArray

    private val secretKey: Key
    private val ecipher: Cipher
    private val dcipher: Cipher

    init {
        keystore = getKeyStore(keystoreFilePath, stashFilePath)
        keystorePassword = getKeyStorePassword(stashFilePath)
        secretKey = keystore.getKey(SECERT_KEY_ALIAS, keystorePassword)

        val iv = if(keystorePassword.size >= 16)
            keystorePassword.toString().toByteArray().copyOf(16)
        else
            keystorePassword.toList().plus(
                (1..(16-keystorePassword.size)).toList().map { it.toChar() })
                .toString().toByteArray().copyOf(16)

        val paramSpec = IvParameterSpec(iv)
        ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        ecipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec)

        dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        dcipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec)
    }

    fun encrypt(plainText: String): String
        = String(Base64.getMimeEncoder().encode(ecipher.doFinal(plainText.toByteArray())))


    fun decrypt(encryptedText: String): String =
        String(dcipher.doFinal(Base64.getMimeDecoder().decode(encryptedText.trim().toByteArray())))

    private fun getKeyStorePassword(stashFilePath: String): CharArray =
        if(Paths.get(stashFilePath).toFile().exists()) {
            val encoded = String(Paths.get(stashFilePath).toFile().readBytes())
                .split("|")
                .map { it.toInt() - 1 }
                .map { it.toByte() }
                .toTypedArray().toByteArray()

            val tmpPwd = String(Base64.getDecoder().decode(encoded))
            if(tmpPwd.contains(','))
                tmpPwd
                    .removeSurrounding("[","]")
                    .replace(",","")
                    .replace(" ", "").toCharArray()
            else tmpPwd.toCharArray()
        } else throw IllegalArgumentException("missing stash file specified")

    private fun getKeyStore(keystoreFilePath: String, stashFilePath: String): KeyStore =
        if(Paths.get(keystoreFilePath).toFile().exists()) {

            KeyStore.getInstance(KEY_STORE_TYPE).apply {
                load(Paths.get(keystoreFilePath).toFile().inputStream(),
                    getKeyStorePassword(stashFilePath))
            }
        } else createKeyStore(keystoreFilePath, stashFilePath)

    private fun createKeyStore(
        keystoreFilePath: String, stashFilePath: String,
        keystorePassword: CharArray = randomPassphase(15)): KeyStore {

        val keystore = KeyStore.getInstance(KEY_STORE_TYPE).apply {
            load(null, keystorePassword)
            val keyStorePP = KeyStore.PasswordProtection(keystorePassword)
            setEntry(SECERT_KEY_ALIAS, KeyStore.SecretKeyEntry(generateKey()), keyStorePP)
            store(Paths.get(keystoreFilePath).toFile().outputStream(), keystorePassword)
        }

        val stash= Base64.getEncoder()
            .encode(keystorePassword.contentToString().toByteArray())
            .map { it.toInt() + 1 }
            .map { it.toString() }
            .joinToString("|")
            .toByteArray()

        Paths.get(stashFilePath).toFile().apply {
            delete()
            writeBytes(stash)
        }

        return keystore
    }

    private fun generateKey(): SecretKey = SecretKeySpec(
        SecretKeyFactory
            .getInstance(ENCRYPT_ALGO_PBKDF2_WITH_HMAC_SHA512)
            .generateSecret(PBEKeySpec(
                randomPassphase(),
                generateSalt(),
                ITERATION_COUNT, KEY_LENGTH)).encoded,
        "AES")

    private fun randomPassphase(length: Int = 8) = ('a'..'z').randomString(length).toCharArray()


    private fun generateSalt(saltByteSize: Int = 8) =
        ByteArray(saltByteSize).apply {
            SecureRandom().nextBytes(this)
        }
}

private fun ClosedRange<Char>.randomString(length: Int) = (1..length)
        .map { (SecureRandom().nextInt(endInclusive.toInt() - start.toInt()) + start.toInt()).toChar() }
        .joinToString("")
