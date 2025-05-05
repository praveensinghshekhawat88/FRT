package com.callmangement.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val _context: Context) : Application() {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        pref = _context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        editor = pref.edit()
    }

    var permissionGranted: Boolean
        get() = pref.getBoolean(
            PERMISSION_GRANTED,
            false
        )
        set(_PERMISSION_GRANTED) {
            editor.putBoolean(
                PERMISSION_GRANTED,
                _PERMISSION_GRANTED
            )
            editor.apply()
        }

    var permissionGrantedBackground: Boolean
        get() {
            return pref.getBoolean(
                PERMISSION_GRANTED_BACKGROUND,
                false
            )
        }
        set(_PERMISSION_GRANTED_BACKGROUND) {
            editor.putBoolean(
                PERMISSION_GRANTED_BACKGROUND,
                _PERMISSION_GRANTED_BACKGROUND
            )
            editor.apply()
        }

    fun clear() {
        editor.clear()
        editor.commit()
    }

    companion object {
        private const val PREF_NAME: String = "ePDS"
        private const val PERMISSION_GRANTED_BACKGROUND: String = "PERMISSION_GRANTED_BACKGROUND"
        private const val PERMISSION_GRANTED: String = "PERMISSION_GRANTED"
    }
}
