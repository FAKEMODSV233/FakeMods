package com.fakemods.aimbot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_OVERLAY = 1001
    private val REQUEST_CODE_MEDIA_PROJECTION = 1002
    private var aimbotActive = false
    private var espActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_aimbot).setOnClickListener {
            aimbotActive = !aimbotActive
            val text = if (aimbotActive) "🎯 DESACTIVAR AIMBOT" else "🎯 ACTIVAR AIMBOT"
            findViewById<Button>(R.id.btn_aimbot).text = text
            Toast.makeText(this, if (aimbotActive) "Aimbot activado" else "Aimbot desactivado", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_esp).setOnClickListener {
            espActive = !espActive
            val text = if (espActive) "👁️ DESACTIVAR ESP" else "👁️ ACTIVAR ESP"
            findViewById<Button>(R.id.btn_esp).text = text
            Toast.makeText(this, if (espActive) "ESP activado" else "ESP desactivado", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            checkPermissionsAndStart()
        }
    }

    private fun checkPermissionsAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE_OVERLAY)
            return
        }

        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "🔓 Activa el servicio de accesibilidad", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            return
        }

        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_MEDIA_PROJECTION)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return enabledServices?.contains(packageName) == true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK) {
            val intent = Intent(this, ScreenCaptureService::class.java)
            intent.putExtra("resultCode", resultCode)
            intent.putExtra("data", data)
            intent.putExtra("aimbotActive", aimbotActive)
            intent.putExtra("espActive", espActive)
            startForegroundService(intent)
            Toast.makeText(this, "🔥 Servicio iniciado", Toast.LENGTH_SHORT).show()
        }
    }
}
