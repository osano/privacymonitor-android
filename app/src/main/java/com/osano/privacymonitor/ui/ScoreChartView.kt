package com.osano.privacymonitor.ui

import android.view.View
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.osano.privacymonitor.R

class ScoreChartView : View {

    private lateinit var circleBackgroundPaint: Paint
    private lateinit var scoreCirclePaint: Paint
    private lateinit var previousScoreLinePaint: Paint
    private lateinit var circleRect: RectF

    @ColorInt
    private var circleBackgroundColor = 0
    private var thickness = 6.toFloat()

    private var score: Int = 0
    private var previousScore = 0

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun configureWithScore(score: Int, previousScore: Int = 0, scoreColor: Int) {
        this.score = score
        this.previousScore = previousScore

        scoreCirclePaint.color = ContextCompat.getColor(context, scoreColor)

        invalidate()
        requestLayout()
    }

    private fun init(context: Context) {
        circleRect = RectF()

        thickness = context.resources.displayMetrics.density * 6.0f

        circleBackgroundColor = ContextCompat.getColor(context, R.color.scoreChartBackground)

        setupPaints()
    }

    private fun setupPaints() {
        circleBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circleBackgroundPaint.style = Paint.Style.STROKE
        circleBackgroundPaint.strokeWidth = thickness
        circleBackgroundPaint.strokeCap = Paint.Cap.BUTT
        circleBackgroundPaint.strokeJoin = Paint.Join.BEVEL
        circleBackgroundPaint.isDither = true
        circleBackgroundPaint.color = circleBackgroundColor

        scoreCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        scoreCirclePaint.style = Paint.Style.STROKE
        scoreCirclePaint.strokeWidth = thickness
        scoreCirclePaint.strokeCap = Paint.Cap.BUTT
        scoreCirclePaint.strokeJoin = Paint.Join.BEVEL
        scoreCirclePaint.isDither = true

        previousScoreLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        previousScoreLinePaint.style = Paint.Style.STROKE
        previousScoreLinePaint.strokeWidth = thickness / 2
        previousScoreLinePaint.strokeCap = Paint.Cap.BUTT
        previousScoreLinePaint.strokeJoin = Paint.Join.BEVEL
        previousScoreLinePaint.isDither = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = (width / 2.0).toFloat()
        val startAngle = 270.0f
        val maxAngle = 360.0f

        // Background
        canvas.drawArc(circleRect, 0f, maxAngle, false, circleBackgroundPaint)

        val minScore = 300.0f
        val maxScore = 850.0f

        val scorePercentage = (score.toFloat() - minScore) / (maxScore - minScore)

        // Score
        canvas.drawArc(circleRect, startAngle, (maxAngle * scorePercentage), false, scoreCirclePaint)

        // Previous score
        if (previousScore > 0 && previousScore != score) {

            val previousScorePercentage = (previousScore.toFloat() - minScore) / (maxScore - minScore)
            val previousScoreAngle = (maxAngle * previousScorePercentage).toDouble()
            val previousScoreRadians = Math.toRadians(previousScoreAngle).toFloat()

            val startX = circleRect.centerX() + radius * Math.sin(previousScoreRadians.toDouble())
            val startY = circleRect.centerY() - radius * Math.cos(previousScoreRadians.toDouble())

            val stopX = circleRect.centerX() + (radius - thickness) * Math.sin(previousScoreRadians.toDouble())
            val stopY = circleRect.centerY() - (radius - thickness) * Math.cos(previousScoreRadians.toDouble())

            val previousScoreLineColor = if (previousScore > score) {
                R.color.previousScoreDecliningColor
            }
            else {
                R.color.previousScoreDefaultColor
            }

            previousScoreLinePaint.color = ContextCompat.getColor(context, previousScoreLineColor)

            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                stopX.toFloat(),
                stopY.toFloat(),
                previousScoreLinePaint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        val height = measuredHeight

        val widthWithoutPadding = width - paddingLeft - paddingRight
        val heightWithoutPadding = height - paddingLeft - paddingRight

        val size = if (widthWithoutPadding > heightWithoutPadding) {
            heightWithoutPadding
        }
        else {
            widthWithoutPadding
        }

        setMeasuredDimension(size + paddingLeft + paddingRight, size + paddingTop + paddingBottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        circleRect.set(
            thickness / 2,
            thickness / 2,
            width - thickness / 2,
            height - thickness / 2
        )
    }
}