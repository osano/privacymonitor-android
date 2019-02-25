package com.osano.privacymonitor

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.osano.privacymonitor.application.App
import com.osano.privacymonitor.fragment.WelcomePageFragment
import com.osano.privacymonitor.fragment.WelcomePageFragmentCallback
import com.osano.privacymonitor.util.Constants
import android.app.Activity

class WelcomeActivity : AppCompatActivity(), WelcomePageFragmentCallback {

    private lateinit var pager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        pager = findViewById(R.id.viewPager)
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> WelcomePageFragment.newInstance(position)
                else -> WelcomePageFragment.newInstance(position)
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        if (fragment is WelcomePageFragment) {
            fragment.fragmentCallback = this
        }
    }

    override fun onNextButtonClick(page: Int, fragment: WelcomePageFragment) {
        when (page) {
            0 -> pager.currentItem = pager.currentItem + 1
            else -> presentWebViewActivity()
        }
    }

    private fun presentWebViewActivity() {
        PreferenceManager.getDefaultSharedPreferences(App.context).edit().putBoolean(Constants.preferencesHasSeenWelcomeScreenKey, true).apply()

        val returnIntent = Intent()
        returnIntent.putExtra("result", 1)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
