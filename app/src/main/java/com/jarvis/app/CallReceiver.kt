package com.jarvis.app
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager

class CallReceiver : BroadcastReceiver() {
    @Suppress("DEPRECATION")
    override fun onReceive(c: Context, i: Intent) {
        if (i.getStringExtra(TelephonyManager.EXTRA_STATE) != TelephonyManager.EXTRA_STATE_RINGING) return
        val num = i.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: return
        val p = c.getSharedPreferences("jv", 0)
        if (!p.getBoolean("on", false) || !Util.allowed(p, num)) return
        if (System.currentTimeMillis() - p.getLong("lastcall", 0) < 5000) return
        p.edit().putLong("lastcall", System.currentTimeMillis()).apply()
        try { (c.getSystemService(Context.TELECOM_SERVICE) as TelecomManager).endCall() } catch (e: SecurityException) {}
        try { Util.sms(num, p.getString("busy", "") ?: "") } catch (e: Exception) {}
    }
}
