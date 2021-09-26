package com.ellie.billiardsgame

import android.app.Application
import android.content.Context

class GlobalApplication : Application() {

    //----------------------------------------------------------
    // Public interface.
    //

    override fun onCreate() {
        super.onCreate()
        APP_CONTEXT = applicationContext
    }

    companion object {
        private lateinit var APP_CONTEXT: Context

        val ballDiameter: Float by lazy { APP_CONTEXT.resources.getDimension(R.dimen.ball_diameter_size) }
        val ballRadius: Float by lazy { ballDiameter / 2 }
    }
}