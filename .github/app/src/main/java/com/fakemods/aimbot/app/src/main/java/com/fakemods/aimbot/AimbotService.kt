package com.fakemods.aimbot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper

class AimbotService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {
        val x = event?.extras?.getInt("tapX") ?: -1
        val y = event?.extras?.getInt("tapY") ?: -1
        if (x > 0 && y > 0) {
            Handler(Looper.getMainLooper()).post {
                performTap(x.toFloat(), y.toFloat())
            }
        }
    }

    private fun performTap(x: Float, y: Float) {
        val builder = GestureDescription.Builder()
        val path = Path().apply { moveTo(x, y) }
        builder.addStroke(GestureDescription.StrokeDescription(path, 0, 1))
        dispatchGesture(builder.build(), null, null)
    }

    override fun onInterrupt() {}
}
