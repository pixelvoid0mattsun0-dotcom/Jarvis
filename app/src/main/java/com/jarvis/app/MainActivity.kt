package com.jarvis.app
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val p = getSharedPreferences("jv", 0)
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 80, 40, 40) }
        fun label(t: String) = col.addView(TextView(this).apply { text = t; setPadding(0, 24, 0, 0) })
        fun field(k: String, hint: String, pw: Boolean = false, lines: Int = 1) = EditText(this).apply {
            this.hint = hint; setText(p.getString(k, "")); minLines = lines
            inputType = if (pw) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }.also { col.addView(it, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)) }
        val on = CheckBox(this).apply { text = "Auto-handle calls and texts"; isChecked = p.getBoolean("on", false) }
        col.addView(on)
        label("Groq API key"); val key = field("key", "gsk_...", true)
        label("Numbers Jarvis handles (one per line, with country code)"); val nums = field("nums", "+15551234567", lines = 3)
        label("Text sent after a rejected call")
        val busy = field("busy", "Sir is busy. Jarvis will pass on your call.")
        fun btn(t: String, f: () -> Unit) = col.addView(Button(this).apply { text = t; setOnClickListener { f() } })
        btn("Save") {
            p.edit().putBoolean("on", on.isChecked).putString("key", key.text.toString().trim())
                .putString("nums", nums.text.toString()).putString("busy", busy.text.toString()).apply()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }
        btn("Grant permissions") {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG,
                Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.RECORD_AUDIO), 1)
        }
        btn("Open Jarvis voice") { startActivity(Intent(this, JarvisActivity::class.java)) }
        setContentView(ScrollView(this).apply { addView(col) })
    }
}
