package com.osano.privacymonitor.application

import android.app.Application
import android.content.Context

class App : Application() {
	companion object {
		@JvmStatic
		var context: Context? = null
	}

	override fun onCreate() {
		super.onCreate()

		context = applicationContext
	}
}
