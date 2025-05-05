package com.callmangement.ehr.support

import android.content.Context
import android.content.SharedPreferences
import com.callmangement.R

class Preference {
    fun getBoolean(key: String?): Boolean {
        return sharedPreferences!!.getBoolean(key, false)
    }

    fun putBoolean(key: String?, value: Boolean) {
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getString(key: String?): String {
        return sharedPreferences!!.getString(key, "")!!
    }

    fun putString(key: String?, value: String?) {
        val editor = sharedPreferences!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putInt(key: String?, value: Int) {
        val editor = sharedPreferences!!.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String?): Int {
        return sharedPreferences!!.getInt(key, 0)
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return sharedPreferences!!.getBoolean(key, defaultValue)
    }

    fun putLong(key: String?, value: Long) {
        val editor = sharedPreferences!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String?): Long {
        return sharedPreferences!!.getLong(key, 0)
    }

    fun remove(key: String?) {
        val editor = sharedPreferences!!.edit()
        editor.remove(key)
        editor.apply()
    }

    fun clearPreference() {
        val editor = sharedPreferences!!.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private var preference: Preference? = null
        private var sharedPreferences: SharedPreferences? = null

        const val USER_LOGIN_STATUS: String = "USER_LOGIN_STATUS"
        const val USER_ID: String = "USER_ID"
        const val USER_NAME: String = "USER_NAME"
        const val USER_EMAIL: String = "USER_EMAIL"
        const val USER_Mobile: String = "USER_Mobile"
        const val USER_DISTRICT_ID: String = "USER_DISTRICT_ID"
        const val USER_DISTRICT: String = "USER_DISTRICT"
        const val USER_TYPE: String = "USER_TYPE"
        const val USER_TYPE_ID: String = "USER_TYPE_ID"

        @JvmStatic
        fun getInstance(context: Context): Preference? {
            if (preference == null) {
                preference = Preference()
                sharedPreferences = context.getSharedPreferences(
                    context.resources.getString(R.string.app_name),
                    Context.MODE_PRIVATE
                )
            }
            return preference
        }
    }
}
