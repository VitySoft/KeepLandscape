# KeepLandscape

An app to keep the screen on landscape.

    fun forceLandscape(context: Context) {
        Log.d(TAG, "forceLandscape")
        val cr = context.contentResolver
        // Disable auto rotation
        Settings.System.putInt(cr, Settings.System.ACCELEROMETER_ROTATION, 0)
        // Set the screen orientation to landscape
        Settings.System.putInt(cr, Settings.System.USER_ROTATION, 1)
    }

    // Draw an invisible Overlay to force the screen on landscape
    // When the device goes to sleep mode, the screen orientation will
    // automatically change to portrait on LineageOS 18.1(Android 11),
    // causing scrcpy to crash.
    private fun drawOverlay() {
        if (overlay != null) {
            removeOverlay()
        }
        overlay = View(this)
        overlay!!.setBackgroundColor(0x00000000)
        val flags = WinLayoutParams.FLAG_NOT_FOCUSABLE or
                WinLayoutParams.FLAG_NOT_TOUCH_MODAL or
                WinLayoutParams.FLAG_LAYOUT_IN_SCREEN
        val params = WinLayoutParams(
            WinLayoutParams.WRAP_CONTENT,
            WinLayoutParams.WRAP_CONTENT,
            getOverlayType(),
            flags,
            PixelFormat.TRANSLUCENT
        )
        params.x = 0
        params.y = 0
        params.width = 0
        params.height = 0
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        windowManager.addView(overlay, params)
    }
