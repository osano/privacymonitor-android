package com.osano.privacymonitor

import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.view.inputmethod.EditorInfo
import android.util.Patterns
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.*
import android.webkit.WebSettings.LOAD_NO_CACHE
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.osano.privacymonitor.models.Domain
import java.net.URLEncoder
import com.osano.privacymonitor.viewmodel.DomainViewModel
import com.osano.privacymonitor.application.PrivacyMonitorRepository
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.osano.privacymonitor.data.AppDatabase
import com.osano.privacymonitor.data.FavoritesRepository
import com.osano.privacymonitor.fragment.FavoritesFragment
import com.osano.privacymonitor.models.FavoriteURL
import com.osano.privacymonitor.networking.*
import com.osano.privacymonitor.util.*

class MainActivity : AppCompatActivity(), FavoritesFragment.OnListFragmentInteractionListener {

    private lateinit var domainViewModel: DomainViewModel
    private var scoreViewTopMargin = 0
    private var countDownTimer : CountDownTimer? = null
    private lateinit var privacyMonitorRepository: PrivacyMonitorRepository

    private var webViewLoadingFinished = true
    private var webViewRedirect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(searchToolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        configureSearchView()
        configureWebView()
        configureNavigationBar()

        closeScoreButton.setOnClickListener {
            collapseScoreView()
        }

        requestAnalysisButton.setOnClickListener {
            requestScoreAnalysis()
        }

        privacyMonitorRepository = PrivacyMonitorRepository(applicationContext)
    }

    override fun onPause() {
        super.onPause()
        stopScoreViewTimer()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopScoreViewTimer()
    }

    private fun configureSearchView() {
        searchEditText.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.clearFocus()
                search(view.text.toString())
            }
            false
        }

        searchEditText.setOnFocusChangeListener { _, _ ->
            webView.stopLoading()
            collapseScoreView()
        }

        searchButton.setOnClickListener {
            requestDomainScore()
        }
    }

    private fun configureWebView() {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                setSupportZoom(true)
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
                cacheMode = LOAD_NO_CACHE
                setAppCacheEnabled(false)
                clearHistory()
                clearCache(true)
            }
            webChromeClient = ProgressWebChromeClient()
            webViewClient = PrivacyMonitorWebViewClient()
        }

        CookieManager.getInstance().setAcceptCookie(false)

        webView.loadUrl(Constants.webViewDefaultURL)
    }

    private fun configureNavigationBar() {
        goBackButton.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
                updateURL(webView.url)
            }
        }

        goForwardButton.setOnClickListener {
            if (webView.canGoForward()) {
                webView.goForward()
                updateURL(webView.url)
            }
        }

        favoritesButton.setOnClickListener {
            val fragment = FavoritesFragment()

            val transaction = supportFragmentManager.beginTransaction()

            // Replace the fragment on container
            transaction.add(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)

            // Finishing the transition
            transaction.commit()
        }

        shareButton.setOnClickListener {
            showActionDialog()
        }

        updateNavigationButtons()
    }

    private fun updateNavigationButtons() {
        val goBackImage = if (webView.canGoBack()) R.mipmap.toolbar_back else R.mipmap.toolbar_back_disabled
        val goForwardImage = if (webView.canGoForward()) R.mipmap.toolbar_forward else R.mipmap.toolbar_forward_disabled

        goBackButton.setImageResource(goBackImage)
        goForwardButton.setImageResource(goForwardImage)
    }

    private fun requestDomainScore() {
        val url = webView.url ?: return
        val rootDomain = url.rootDomain() ?: return

        showSearchLoadingIndicator(true)

        privacyMonitorRepository.requestDomainScore(rootDomain) {

            showSearchLoadingIndicator(false)

            when(it) {
                is ApiSuccessResponse<Domain> -> {
                    domainViewModel = DomainViewModel(it.body)
                    updateScoreTooltip()
                    expandScoreView()
                }
                is ApiErrorResponse<Domain> -> {
                    when (it.networkError) {
                        is NetworkError.NotFound -> showRequestAnalysisMessage()
                    }
                }
            }
        }
    }

    private fun registerDomainVisit() {
        val url = webView.url ?: return
        val rootDomain = url.rootDomain() ?: return

        privacyMonitorRepository.registerDomainVisit(rootDomain) {
            domainViewModel = DomainViewModel(it)
            updateScoreTooltip()
            expandScoreView()
        }
    }

    private fun requestScoreAnalysis() {
        val url = webView.url ?: return
        val rootDomain = url.rootDomain() ?: return

        stopScoreViewTimer()

        showSearchLoadingIndicator(true)

        NetworkRepository.requestScoreAnalysis(rootDomain) {

            showSearchLoadingIndicator(false)

            when(it) {
                is ApiEmptyResponse<Void> -> {
                    requestAnalysisTextView.text = getString(R.string.scoreAnalysisSuccessText)
                    requestAnalysisButton.gone()
                }
                is ApiErrorResponse<Void> -> {
                    requestAnalysisTextView.text = getString(R.string.networkError)
                }
            }

            startScoreViewTimer()
        }
    }

    private fun showRequestAnalysisMessage() {
        requestAnalysisTextView.text = getString(R.string.scoreAnalysisNotFoundText)

        scoreContentView.invisible()
        requestAnalysis.visible()
        requestAnalysisButton.visible()
        expandScoreView()
    }

    private fun updateScoreTooltip() {
        if (requestAnalysis.isVisible) {
            requestAnalysis.invisible()
        }

        if (!scoreContentView.isVisible) {
            scoreContentView.visible()
        }

        scoreTextView.text = domainViewModel.scoreNumberDescription()

        scoreDescriptionTextView.text = domainViewModel.scoreDescription()
        scoreDescriptionTextView.setTextColor(ContextCompat.getColor(applicationContext, domainViewModel.scoreColor()))

        trendDescriptionTextView.text = domainViewModel.trendDescription()
        trendDescriptionTextView.setTextColor(ContextCompat.getColor(applicationContext, domainViewModel.trendColor()))

        trendImageView.setImageResource(domainViewModel.trendImage())
        ImageViewCompat.setImageTintList(trendImageView, ContextCompat.getColorStateList(applicationContext, domainViewModel.trendColor()))

        scoreChart.configureWithScore(domainViewModel.score(), previousScore = domainViewModel.previousScore(), scoreColor = domainViewModel.scoreColor())
    }

    private fun expandScoreView() {
        if (scoreView.visibility == View.VISIBLE) {
            return
        }

        scoreView.visibility = View.VISIBLE

        val params = scoreView.layoutParams as? FrameLayout.LayoutParams
        scoreViewTopMargin = params?.topMargin ?: 0
        val topMarginEnd = 0

        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val layoutParams = scoreView.layoutParams ?: return
                if (layoutParams is FrameLayout.LayoutParams) {
                    layoutParams.topMargin = scoreViewTopMargin + ((topMarginEnd - scoreViewTopMargin) * interpolatedTime).toInt()
                }
                scoreView.layoutParams = layoutParams
            }
        }

        a.duration = 400
        scoreView.startAnimation(a)

        startScoreViewTimer()
    }

    private fun stopScoreViewTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }

    private fun startScoreViewTimer() {
        stopScoreViewTimer()

        countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                collapseScoreView()
            }
        }.start()
    }

    private fun collapseScoreView() {
        if (scoreView.visibility == View.INVISIBLE) {
            return
        }

        stopScoreViewTimer()

        val topMarginStart = 0

        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime < 1.0) {
                    val params = scoreView.layoutParams ?: return
                    if (params is FrameLayout.LayoutParams) {
                        params.topMargin =
                            topMarginStart + ((scoreViewTopMargin - topMarginStart) * interpolatedTime).toInt()
                    }
                    scoreView.layoutParams = params
                }
                else {
                    scoreView.visibility = View.INVISIBLE
                }
            }
        }

        a.duration = 400
        scoreView.startAnimation(a)
    }

    private fun showSearchLoadingIndicator(show: Boolean) {
        if (show) {
            searchProgressBar.visible()
            searchButton.invisible()
        }
        else {
            searchProgressBar.invisible()
            searchButton.visible()
        }
    }

    private fun showActionDialog() {
        val items = arrayOf<CharSequence>(getString(R.string.share_url), getString(R.string.save_to_favorites))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(webView.url.rootDomain() ?: getString(R.string.dialog_title))
        builder.setItems(items) { _, which ->
            when (which) {
                0 -> showShareIntent()
                else -> saveToFavorites()
            }
        }
        builder.setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showShareIntent() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, webView.url)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.shareLinkUsing)))
    }

    private fun saveToFavorites() {
        val favoritesRepository = FavoritesRepository.getInstance(AppDatabase.getInstance(applicationContext).favoriteURLDao())
        favoritesRepository.insertUrl(FavoriteURL(url = webView.url))
        applicationContext.toast(getString(R.string.favoriteUrlAdded))
    }

    private fun urlAsHTTP(url: String): String {
        if (Patterns.WEB_URL.matcher(url).matches()) {
            return if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                url
            } else {
                "http://$url"
            }
        }

        return Constants.webViewRedirectURL + URLEncoder.encode(url, "utf-8")
    }

    private fun search(url: String) {
        if (url.isEmpty()) return

        webView.loadUrl(urlAsHTTP(url))
    }

    private fun updateURL(url: String?) = searchEditText.setText(url)

    private open inner class PrivacyMonitorWebViewClient : WebViewClient() {
        @Suppress("OverridingDeprecatedMember")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (!webViewLoadingFinished) {
                webViewRedirect = true
            }

            webViewLoadingFinished = false
            webView.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            updateURL(url)
            updateNavigationButtons()
            webViewLoadingFinished = false
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            updateURL(url)
            updateNavigationButtons()

            if (!webViewRedirect) {
                webViewLoadingFinished = true
            }

            if (webViewLoadingFinished && !webViewRedirect) {
                registerDomainVisit()
            }
            else {
                webViewRedirect = false
            }

            super.onPageFinished(view, url)
        }
    }

    private inner class ProgressWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress == 100) {
                webViewProgressBar.visibility = View.GONE
            }
            else {
                if (webViewProgressBar.visibility == View.GONE) {
                    webViewProgressBar.visibility = View.VISIBLE
                }
                webViewProgressBar.progress = newProgress
            }
            super.onProgressChanged(view, newProgress)
        }
    }

    override fun onListFragmentInteraction(item: FavoriteURL?) {
        supportFragmentManager.popBackStack()
        if (item != null) {
            collapseScoreView()
            webView.loadUrl(item.url)
        }
    }
}
