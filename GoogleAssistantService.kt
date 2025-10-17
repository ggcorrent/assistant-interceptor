package com.assistant.interceptor

import android.content.Intent
import android.content.SharedPreferences
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast

class GoogleAssistantService : NotificationListenerService() {
    
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("AssistantData", MODE_PRIVATE)
    }
    
    override fun onNotificationPosted(notification: StatusBarNotification) {
        try {
            val packageName = notification.packageName.toLowerCase()
            
            if (packageName.contains("google") || 
                packageName.contains("assistant") ||
                packageName.contains("gemini")) {
                
                val notificationText = extractNotificationText(notification)
                
                if (isAssistantResponse(notificationText)) {
                    Log.d("AssistantInterceptor", "RISPOSTA: $notificationText")
                    sendToMainApp(notificationText)
                }
            }
        } catch (e: Exception) {
            // Ignora errori
        }
    }
    
    private fun extractNotificationText(notification: StatusBarNotification): String {
        return try {
            val extras = notification.notification.extras
            val title = extras.getString("android.title") ?: ""
            val text = extras.getString("android.text") ?: ""
            
            "$title\n$text".trim()
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun isAssistantResponse(text: String): Boolean {
        if (text.length < 10) return false
        val excludeKeywords = listOf("download", "update", "sync", "backup", "battery")
        return !excludeKeywords.any { text.contains(it, ignoreCase = true) }
    }
    
    private fun sendToMainApp(text: String) {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("assistant_response", text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            saveResponse(text)
        }
    }
    
    private fun saveResponse(text: String) {
        prefs.edit().putString("last_response", text).apply()
    }
    
    override fun onListenerConnected() {
        super.onListenerConnected()
        Toast.makeText(this, "🤖 Intercettazione ATTIVA!", Toast.LENGTH_LONG).show()
    }
}