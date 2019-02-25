package com.osano.privacymonitor.viewmodel

import com.osano.privacymonitor.R
import com.osano.privacymonitor.models.Domain

enum class Trend {
    Undefined,
    NoChange,
    Declining,
    Improving
}

enum class Score {
    Unknown,
    VeryPoor,
    Fair,
    Good,
    VeryGood,
    Exceptional
}

class DomainViewModel(private val domain: Domain) {

    fun rootDomain(): String = domain.rootDomain
    fun score(): Int = domain.score
    fun previousScore(): Int = domain.previousScore
    fun scoreNumberDescription(): String = "Score: ${domain.score}"

    private fun scoreEnum(): Score {
        return when (domain.score) {
            in 0..579 -> Score.VeryPoor
            in 580..669 -> Score.Fair
            in 670..739 -> Score.Good
            in 740..799 -> Score.VeryGood
            in 800..850 -> Score.Exceptional
            else -> Score.Unknown
        }
    }

    fun scoreDescription(): String {
        return when (scoreEnum()) {
            Score.Unknown -> "Unknown"
            Score.VeryPoor -> "Very Poor"
            Score.Fair -> "Fair"
            Score.Good -> "Good"
            Score.VeryGood -> "Very Good"
            Score.Exceptional -> "Exceptional"
        }
    }

    fun scoreDescriptionLarge(): String = "${domain.score} - ${scoreDescription()}"

    fun scoreColor(): Int {
        return when (scoreEnum()) {
            Score.Unknown -> R.color.scoreUnknownColor
            Score.VeryPoor -> R.color.scoreVeryPoorColor
            Score.Fair -> R.color.scoreFairColor
            Score.Good -> R.color.scoreGoodColor
            Score.VeryGood -> R.color.scoreVeryGoodColor
            Score.Exceptional -> R.color.scoreExceptionalColor
        }
    }

    private fun trend(): Trend {
        if (domain.score == 0) return Trend.Undefined

        if (domain.score > domain.previousScore) {
            return Trend.Improving
        }
        else if (domain.score < domain.previousScore) {
            return Trend.Declining
        }

        return Trend.NoChange
    }

    fun trendDescription(): String {
        return when(trend()) {
            Trend.Undefined -> "No History"
            Trend.NoChange -> "No Change"
            Trend.Improving -> "Improving"
            Trend.Declining -> "Declining"
        }
    }

    fun trendColor(): Int {
        return when(trend()) {
            Trend.Undefined, Trend.NoChange -> R.color.trendNoChangeColor
            Trend.Improving -> R.color.trendImprovingColor
            Trend.Declining -> R.color.trendDecliningColor
        }
    }

    fun trendImage(): Int {
        return when(trend()) {
            Trend.Undefined, Trend.NoChange -> R.mipmap.trend_no_change_icon
            Trend.Improving -> R.mipmap.trend_improving_icon
            Trend.Declining -> R.mipmap.trend_declining_icon
        }
    }
}