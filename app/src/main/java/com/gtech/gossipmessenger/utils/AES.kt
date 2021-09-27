package com.gtech.gossipmessenger.utils

import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES {
    private var key: SecretKey? = null
    private var iv: ByteArray
    init {
        val decoded = "kYp3s5v8y/B?E(H+MbQeThWmZq4t7w9z".toByteArray(StandardCharsets.UTF_8)
        this.key = SecretKeySpec(decoded, 0, decoded.size, "AES")
        this.iv = "v9y\$B&E)H@McQfTh".toByteArray(StandardCharsets.UTF_8)
    }

    fun encrypt(data: String): String? {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, this.key, ivSpec)
            val doFinal = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
            return Base64.encodeToString(doFinal, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun decrypt(cipherText: String?): String? {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, this.key, ivSpec)
            return String(cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}