package com.osano.privacymonitor

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.preference.PreferenceManager
import com.osano.privacymonitor.application.App
import com.osano.privacymonitor.util.Constants

class StartupActivity : Activity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		if (hasSeenWelcomeScreen()) {
			val intent = Intent(this, MainActivity::class.java)
			intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
			startActivity(intent)
			overridePendingTransition(0, 0)
			finish()
		}
		else {
			val intent = Intent(this, WelcomeActivity::class.java)
			startActivityForResult(intent, 0)
			overridePendingTransition(0, 0)
		}

		super.onCreate(savedInstanceState)
	}

	private fun hasSeenWelcomeScreen(): Boolean {
		return PreferenceManager.getDefaultSharedPreferences(App.context).getBoolean(Constants.preferencesHasSeenWelcomeScreenKey, false)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			val intent = Intent(this, MainActivity::class.java)
			intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
			startActivity(intent)
			overridePendingTransition(0, 0)
		}

		finish()
	}

}
