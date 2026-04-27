package com.example.liboffan

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var authToken: String?
        get() = sharedPreferences.getString("token", null)
        set(value) {
            sharedPreferences.edit().putString("token", value).apply()
        }

    var userEmail: String?
        get() = sharedPreferences.getString("email", null)
        set(value) = sharedPreferences.edit().putString("email", value).apply()

    var userDisplayName: String?
        get() = sharedPreferences.getString("displayName", null)
        set(value) = sharedPreferences.edit().putString("displayName", value).apply()

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}