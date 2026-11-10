package com.jarvis.app
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        val p = c.getSharedPreferences("jv", 0)
        if (!p.getBoolean("on", false)) return
        val msgs = Telephony.Sms.Intents.getMessagesFromIntent(i) ?: return
        if (msgs.isEmpty()) return
        val from = msgs[0].originatingAddress ?: return
        if (!Util.allowed(p, from)) return
        val k = "t_" + Util.d(from)
        if (System.currentTimeMillis() - p.getLong(k, 0) < 60000) return // loop/spam guard
        p.edit().putLong(k, System.currentTimeMillis()).apply()
        val text = msgs.joinToString("") { it.messageBody ?: "" }
        val pr = goAsync()
        Thread {
            try { val r = Util.ai(p, text); if (r.isNotBlank()) Util.sms(from, r) }
            catch (e: Exception) {} finally { pr.finish() }
        }.start()
    }
}
