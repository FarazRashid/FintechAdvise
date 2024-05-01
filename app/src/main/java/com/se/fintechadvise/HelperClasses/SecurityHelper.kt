package com.se.fintechadvise.HelperClasses

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.content.Context
import android.util.Base64
object SecurityHelper {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val KEY_SIZE = 256
    private const val PREFS_NAME = "com.se.fintechadvise"
    private const val KEY_ALIAS = "encryption_key"
    private const val IV_ALIAS = "encryption_iv"




    fun generateKeyAndStoreInSharedPreferences(context: Context): Pair<SecretKey, ByteArray> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Check if key and IV already exist in SharedPreferences
        val existingKeyString = sharedPreferences.getString(KEY_ALIAS, null)
        val existingIvString = sharedPreferences.getString(IV_ALIAS, null)

        if (existingKeyString != null && existingIvString != null) {
            // Convert string back to key and IV
            val decodedKey = Base64.decode(existingKeyString, Base64.DEFAULT)
            val key = SecretKeySpec(decodedKey, 0, decodedKey.size, ALGORITHM)
            val iv = Base64.decode(existingIvString, Base64.DEFAULT)

            return Pair(key, iv)
        }

        // If key and IV do not exist, generate new ones
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(KEY_SIZE)
        val key = keyGenerator.generateKey()

        // Generate IV
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(cipher.blockSize).also { SecureRandom().nextBytes(it) }

        // Convert key and IV to string and store in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString(KEY_ALIAS, Base64.encodeToString(key.encoded, Base64.DEFAULT))
        editor.putString(IV_ALIAS, Base64.encodeToString(iv, Base64.DEFAULT))
        editor.apply()

        return Pair(key, iv)
    }

    fun getKeyAndIvFromSharedPreferences(context: Context): Pair<SecretKeySpec?, ByteArray?> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val keyString = sharedPreferences.getString(KEY_ALIAS, null)
        val ivString = sharedPreferences.getString(IV_ALIAS, null)

        // Convert string back to key and IV
        val key = if (keyString != null) {
            val decodedKey = Base64.decode(keyString, Base64.DEFAULT)
            SecretKeySpec(decodedKey, 0, decodedKey.size, ALGORITHM)
        } else {
            null
        }

        val iv = ivString?.let { Base64.decode(it, Base64.DEFAULT) }

        return Pair(key, iv)
    }


    fun encrypt(data: ByteArray, key: SecretKey, iv: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        return Pair(iv, cipher.doFinal(data))
    }

    fun decrypt(data: ByteArray, iv: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        return cipher.doFinal(data)
    }
}