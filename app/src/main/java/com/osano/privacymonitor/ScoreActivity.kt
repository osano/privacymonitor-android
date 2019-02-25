package com.osano.privacymonitor

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.osano.privacymonitor.networking.ApiErrorResponse
import com.osano.privacymonitor.networking.ApiSuccessResponse
import com.osano.privacymonitor.networking.NetworkError
import com.osano.privacymonitor.models.Domain
import com.osano.privacymonitor.networking.NetworkRepository
import com.osano.privacymonitor.util.rootDomain
import com.osano.privacymonitor.viewmodel.DomainViewModel
import kotlinx.android.synthetic.main.activity_score.*

class ScoreActivity : AppCompatActivity() {

    private lateinit var domainViewModel: DomainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        closeButton.setOnClickListener {
            onBackPressed()
        }

        val action = intent.action
        if (action.equals(Intent.ACTION_SEND, ignoreCase = true) && intent.hasExtra(Intent.EXTRA_TEXT)) {
            val url = intent.getStringExtra(Intent.EXTRA_TEXT)
            val rootDomain = url.rootDomain() ?: return

            requestDomainScore(rootDomain)
        }
    }

    private fun requestDomainScore(rootDomain: String) {
        NetworkRepository.fetchDomainScore(rootDomain) {
            when(it) {
                is ApiSuccessResponse<Domain> -> {
                    it.body.rootDomain = rootDomain
                    domainViewModel = DomainViewModel(it.body)
                    updateUI()
                }
                is ApiErrorResponse<Domain> -> {
                    when (it.networkError) {
                        is NetworkError.NotFound -> Toast.makeText(this, "Domain not found.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateUI() {
        progressBar.visibility = View.GONE
        scoreView.visibility = View.VISIBLE

        domainTextView.text = domainViewModel.rootDomain()

        scoreTextView.text = domainViewModel.scoreNumberDescription()

        scoreDescriptionTextView.text = domainViewModel.scoreDescription()
        scoreDescriptionTextView.setTextColor(ContextCompat.getColor(applicationContext, domainViewModel.scoreColor()))

        trendDescriptionTextView.text = domainViewModel.trendDescription()
        trendDescriptionTextView.setTextColor(ContextCompat.getColor(applicationContext, domainViewModel.trendColor()))

        trendImageView.setImageResource(domainViewModel.trendImage())
        ImageViewCompat.setImageTintList(trendImageView, ContextCompat.getColorStateList(applicationContext, domainViewModel.trendColor()))

        scoreChart.configureWithScore(domainViewModel.score(), previousScore = domainViewModel.previousScore(), scoreColor = domainViewModel.scoreColor())
    }

}
