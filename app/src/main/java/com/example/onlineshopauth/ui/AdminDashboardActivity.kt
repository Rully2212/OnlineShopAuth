package com.example.onlineshopauth.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshopauth.data.SessionManager
import com.example.onlineshopauth.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = SessionManager(this)
        val session = manager.current()
        if (session?.role != "admin") {
            SessionRouter.openDestination(this, manager)
            return
        }
        val binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.greetingText.text = "Halo, ${session.username}"
        binding.profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
