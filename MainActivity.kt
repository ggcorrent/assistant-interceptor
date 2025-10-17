package com.assistant.interceptor

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var btnEnableNotifications: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvLastResponse: TextView
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSimpleUI()
        setupClickListeners()
        checkNotificationPermission()
        
        showLastSavedMessage()
    }

    private fun setupSimpleUI() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            setBackgroundColor(0xFFE8F5E9.toInt())
        }

        val title = TextView(this).apply {
            text = "🤖 Assistant Interceptor"
            textSize = 20f
            setTextColor(0xFF1B5E20.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 30)
        }
        layout.addView(title)

        tvStatus = TextView(this).apply {
            text = "Controllo permessi..."
            textSize = 14f
            setBackgroundColor(0xFFFFFFFF.toInt())
            gravity = android.view.Gravity.CENTER
            setPadding(25, 25, 25, 25)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        layout.addView(tvStatus)

        btnEnableNotifications = Button(this).apply {
            text = "🎯 Attiva Intercettazione"
            textSize = 16f
            setBackgroundColor(0xFF1B5E20.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 25, 0, 25)
        }
        layout.addView(btnEnableNotifications)

        tvLastResponse = TextView(this).apply {
            text = "Le risposte appariranno qui automaticamente..."
            textSize = 14f
            setBackgroundColor(0xFFFFFFFF.toInt())
            setPadding(20, 20, 20, 20)
            setLines(10)
            setTextColor(0xFF333333.toInt())
        }
        layout.addView(tvLastResponse)

        val instructions = TextView(this).apply {
            text = "📋 COME USARE:\n1. Clicca 'Attiva Intercettazione'\n2. Attiva 'Assistant Interceptor'\n3. Torna qui e usa l'assistente vocale\n4. Vedi le risposte qui automaticamente!"
            textSize = 12f
            gravity = android.view.Gravity.CENTER
            setPadding(15, 15, 15, 15)
            setBackgroundColor(0xFFC8E6C9.toInt())
            setTextColor(0xFF1B5E20.toInt())
        }
        layout.addView(instructions)

        setContentView(layout)
        
        prefs = getSharedPreferences("AssistantData", MODE_PRIVATE)
    }

    private fun setupClickListeners() {
        btnEnableNotifications.setOnClickListener {
            openNotificationSettings()
        }
    }

    private fun checkNotificationPermission() {
        val enabled = isNotificationServiceEnabled()
        
        if (enabled) {
            tvStatus.text = "✅ INTERCETTAZIONE ATTIVA!\nOra usa l'assistente vocale!"
            tvStatus.setTextColor(0xFF1B5E20.toInt())
            btnEnableNotifications.text = "🎯 Intercettazione Attiva"
            btnEnableNotifications.isEnabled = false
            btnEnableNotifications.setBackgroundColor(0xFF4CAF50.toInt())
        } else {
            tvStatus.text = "❌ Intercettazione disattivata\nClicca il bottone sotto"
            tvStatus.setTextColor(0xFFD32F2F.toInt())
            btnEnableNotifications.text = "🎯 Attiva Intercettazione"
        }
    }

    private fun openNotificationSettings() {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "Cerca 'Assistant Interceptor' e attivalo!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Impossibile aprire le impostazioni", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        ) ?: ""
        return enabledListeners.contains(packageName)
    }

    private fun showLastSavedMessage() {
        val lastMessage = prefs.getString("last_response", "")
        if (lastMessage?.isNotEmpty() == true) {
            tvLastResponse.text = "📝 Ultima risposta:\n\n$lastMessage"
        }
    }

    fun updateAssistantResponse(text: String) {
        runOnUiThread {
            tvLastResponse.text = "📝 Risposta intercettata:\n\n$text"
            prefs.edit().putString("last_response", text).apply()
            Toast.makeText(this, "🤖 Nuova risposta!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
        showLastSavedMessage()
    }
}