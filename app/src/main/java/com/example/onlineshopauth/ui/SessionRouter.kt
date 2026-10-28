package com.example.onlineshopauth.ui

import android.app.Activity
import android.content.Intent
import com.example.onlineshopauth.data.SessionManager

object SessionRouter {
    fun openDestination(activity: Activity, sessionManager: SessionManager) {
        val target = when (sessionManager.current()?.role) {
            "admin" -> AdminDashboardActivity::class.java
            "user" -> UserDashboardActivity::class.java
            else -> LoginActivity::class.java
        }
        activity.startActivity(Intent(activity, target).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        activity.finish()
    }

    fun openLogin(activity: Activity) {
        activity.startActivity(Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        activity.finish()
    }
}
