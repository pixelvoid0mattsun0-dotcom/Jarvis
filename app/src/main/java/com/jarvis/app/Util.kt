package com.jarvis.app
import android.content.SharedPreferences
import android.telephony.SmsManager
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object Util {
    fun d(n: String) = n.filter { it.isDigit() }.takeLast(10)
    fun allowed(p: SharedPreferences, n: String): Boolean {
        val x = d(n); if (x.length < 10) return false
        return (p.getString("nums", "") ?: "").split(",", "\n").any { d(it) == x }
    }
    fun sms(to: String, text: String) {
        val m = SmsManager.getDefault()
        m.sendMultipartTextMessage(to, null, m.divideMessage(text), null, null)
    }
    fun ai(p: SharedPreferences, incoming: String): String {
        val key = p.getString("key", "") ?: return ""
        if (key.isBlank()) return ""
        val msgs = JSONArray()
            .put(JSONObject().put("role", "system").put("content",
                "You are Jarvis, answering a text message on behalf of your owner. Reply in under 300 characters, politely, in plain text. Never agree to payments, share personal data or make commitments; say the owner will reply personally if needed."))
            .put(JSONObject().put("role", "user").put("content", incoming.take(500)))
        val body = JSONObject().put("model", "openai/gpt-oss-20b").put("max_tokens", 300).put("messages", msgs)
        val c = URL("https://api.groq.com/openai/v1/chat/completions").openConnection() as HttpURLConnection
        c.requestMethod = "POST"; c.doOutput = true; c.connectTimeout = 10000; c.readTimeout = 15000
        c.setRequestProperty("Content-Type", "application/json")
        c.setRequestProperty("Authorization", "Bearer $key")
        c.outputStream.use { it.write(body.toString().toByteArray()) }
        if (c.responseCode != 200) return ""
        val j = JSONObject(c.inputStream.bufferedReader().readText())
        return j.getJSONArray("choices").getJSONObject(0).getJSONObject("message").optString("content").trim()
    }
}
