package com.osano.privacymonitor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.osano.privacymonitor.R

private const val ARG_PAGE = "page"

interface WelcomePageFragmentCallback {
    fun onNextButtonClick(page: Int, fragment: WelcomePageFragment)
}

class WelcomePageFragment : Fragment() {

    private var page: Int? = null
    var fragmentCallback : WelcomePageFragmentCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            page = it.getInt(ARG_PAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = when (page) {
            0 -> inflater.inflate(R.layout.welcome_page1, container, false)
            else -> inflater.inflate(R.layout.welcome_page2, container, false)
        }

        val nextFinishButton = view.findViewById<View>(R.id.nextFinishButton)
        nextFinishButton.setOnClickListener {
            fragmentCallback?.onNextButtonClick(page ?: 0, this)
        }

        return view
    }

    override fun onDetach() {
        fragmentCallback = null
        super.onDetach()
    }

    companion object {

        @JvmStatic
        fun newInstance(page: Int) =
            WelcomePageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PAGE, page)
                }
            }
    }
}