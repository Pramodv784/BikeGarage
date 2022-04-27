package com.example.bikegarage.auth

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class UserAuth private constructor(ctx: Context) {
    var sharedPreferences: SharedPreferences? =
        ctx.getSharedPreferences("SHIKSHA_RATNA", Activity.MODE_PRIVATE)

    companion object {
        var instance: UserAuth? = null
        fun getInstance(ctx: Context): UserAuth? {
            if (instance == null) instance = UserAuth(ctx)
            return instance
        }
    }

    fun setUserLoggedIn() {
        sharedPreferences!!.edit().putBoolean(AuthKeys.IS_USER_LOGGED_IN, true).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences!!.getBoolean(AuthKeys.IS_USER_LOGGED_IN, false)
    }

    fun setAppTourCompleted() {
        sharedPreferences!!.edit().putBoolean(AuthKeys.IS_FIRST_TIME, true).apply()
    }

    fun isAppTourCompleted(): Boolean {
        return sharedPreferences!!.getBoolean(AuthKeys.IS_FIRST_TIME, false)
    }

    fun logoutUser() {
        sharedPreferences!!.edit().clear().apply()
    }

}