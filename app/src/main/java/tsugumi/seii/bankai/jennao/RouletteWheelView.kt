package tsugumi.seii.bankai.jennao

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toRect
import kotlin.math.roundToInt


class RouletteWheelView : View{
    companion object {
        private val ROULETTE_NUMBERS =
            listOf("00",27,10,25,29,12,8,19,31,18,6,21,33,16,4,23,35,14,2,0,28,9,26,30,11,7,20,32,17,5,22,34,15,3,24,36,13,1)

        private val STEP = 360f / ROULETTE_NUMBERS.size

        private const val MIN_WHEEL_DIAMETER_DP = 200f
    }

    private lateinit var mBallPaint: Paint
    private lateinit var mRedPaint: Paint
    private lateinit var mBlackPaint: Paint
    private lateinit var mGreenPaint: Paint
    private lateinit var mGoldPaint: Paint
    private lateinit var mDarkGoldPaint: Paint

    private lateinit var mMiddleTextPaint: TextPaint
    private lateinit var mNumberTextPaint: TextPaint

    private lateinit var mBaseRect: RectF
    private lateinit var mInnerRect: RectF
    private lateinit var mBoundingRect: Rect

    private lateinit var mMiddleLabel: String
    private var mWheelRimHeight: Float = 0f
    private var mWheelRotation: Float = 0f

    constructor(context: Context?) : super(context){
        init(null)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init(attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(attrs)
    }

    private fun init(attrs: AttributeSet?){
        mMiddleLabel = context.getString(R.string.roulette_wheel_centre_label)

        mBaseRect = RectF()
        mInnerRect = RectF()
        mBoundingRect = Rect()

        mRedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteRed) }
        mBlackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteBlack) }
        mBallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteSilver) }
        mGoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteGold) }
        mGreenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteGreen) }
        mDarkGoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteDarkGold) }
        mMiddleTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteDarkGold) }
        mNumberTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteWhite) }

        listOf(mRedPaint, mBlackPaint, mGreenPaint, mDarkGoldPaint).forEach{it.apply {
            style = Paint.Style.STROKE
        }}

        listOf(mMiddleTextPaint, mNumberTextPaint).forEach{it.apply {
            typeface = ResourcesCompat.getFont(context, R.font.playfair_display_bold)
        }}

        attrs?.let {
            with(context.obtainStyledAttributes(attrs, R.styleable.RouletteWheelView)){
                mRedPaint.apply {
                    color = getColor(R.styleable.RouletteWheelView_primary_color, context.getColor(R.color.rouletteRed))
                }
                mBlackPaint.apply {
                    color = getColor(R.styleable.RouletteWheelView_secondary_color, context.getColor(R.color.rouletteBlack))
                }
                recycle()
            }
        }
    }

    private fun Canvas.drawCenterText(text: String, centerX: Float, centerY:Float, paint:TextPaint, boundingRect: Rect){
        paint.getTextBounds(text,0, text.length,boundingRect)
        drawText(text,centerX-boundingRect.exactCenterX(), centerY-boundingRect.exactCenterY(),paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minDiameterInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_WHEEL_DIAMETER_DP, resources.displayMetrics)

        val resolvedWidth = resolveSize(Math.round(minDiameterInPx+paddingStart+paddingEnd), widthMeasureSpec)
        val resolvedHeight = resolveSize(Math.round(minDiameterInPx+paddingTop+paddingBottom), heightMeasureSpec)

        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val wheelDiameter: Float = (if(width < height) width else height).toFloat()/16*14
        mWheelRimHeight = wheelDiameter/10

        with(mBaseRect){
            left = width/2 - wheelDiameter/2
            right = width/2 + wheelDiameter/2
            top = height/2 - wheelDiameter/2
            bottom = height/2 + wheelDiameter/2

            mInnerRect.left = left + mWheelRimHeight
            mInnerRect.right = right - mWheelRimHeight
            mInnerRect.top = top + mWheelRimHeight
            mInnerRect.bottom = bottom - mWheelRimHeight
        }

        configurePaintSizes(wheelDiameter)

        drawWheelBase(canvas, wheelDiameter/2)
        drawWheelHead(canvas, wheelDiameter/2)

        // Rotate canvas before drawing stuff that will be rotated
        canvas.rotate(mWheelRotation, mBaseRect.centerX(), mBaseRect.centerY())

        drawBallBall(canvas, wheelDiameter/2)
    }

    private fun configurePaintSizes(wheelDiameter: Float){
        listOf(mRedPaint,mBlackPaint,mGreenPaint).forEach{
            it.apply {
                strokeWidth = mWheelRimHeight
            }
        }

        mDarkGoldPaint.strokeWidth = mWheelRimHeight/8

        mMiddleTextPaint.textSize = wheelDiameter/4

        mNumberTextPaint.textSize = mWheelRimHeight*2.3f/4
    }

    private fun drawBallBall(canvas: Canvas, wheelRadius: Float){
        canvas.drawCircle(mBaseRect.centerX() - wheelRadius, mBaseRect.centerY(),  mWheelRimHeight/6, mBallPaint)
    }

    private fun drawWheelHead(canvas: Canvas, wheelRadius: Float) {
        val arcAngleDisplacement = -90 - STEP / 2

        var drawingAngle = 0f
        var usePrimaryColor = true
        for (n in ROULETTE_NUMBERS) {
            val paintToUse: Paint
            if (listOf("0", "00").contains(n.toString())) {
                paintToUse = mGreenPaint
            } else {
                paintToUse = if (usePrimaryColor) mRedPaint else mBlackPaint
                usePrimaryColor = !usePrimaryColor
            }

            canvas.save()
            canvas.rotate(drawingAngle, mBaseRect.centerX(), mBaseRect.centerY())
            canvas.drawArc(mBaseRect, arcAngleDisplacement, STEP, false, paintToUse)
            canvas.drawCenterText(
                n.toString(), mBaseRect.centerX(), mBaseRect.centerY() - wheelRadius,
                mNumberTextPaint,mBoundingRect
            )
            canvas.restore()

            drawingAngle += STEP
        }
    }

    private fun drawWheelBase(canvas: Canvas, wheelRadius: Float){
        canvas.drawArc(mInnerRect,0f, 360f, false, mBlackPaint)
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),wheelRadius - mWheelRimHeight /2*3, mGoldPaint)
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),wheelRadius - mWheelRimHeight /2*3, mDarkGoldPaint)
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),wheelRadius + mWheelRimHeight /2, mDarkGoldPaint)
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),wheelRadius - mWheelRimHeight /2, mDarkGoldPaint)

        canvas.drawCenterText(mMiddleLabel,
            mBaseRect.centerX(), mBaseRect.centerY(), mMiddleTextPaint, mBoundingRect)
    }

    fun spin(travelAngle: Float){
        val initialRotation = mWheelRotation
        val adjustedTravelAngle = travelAngle - (mWheelRotation+travelAngle) % STEP + STEP/2

        val animator = ValueAnimator.ofFloat(0f, adjustedTravelAngle)
        animator.duration = travelAngle.toLong()*3
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            mWheelRotation = initialRotation + animation.animatedValue as Float
            invalidate()
        }
        animator.addListener(onEnd = {
            mWheelRotation = initialRotation + adjustedTravelAngle
            val pocketIndex = (((mWheelRotation - 90)% 360)/STEP)
            mMiddleLabel = ROULETTE_NUMBERS[pocketIndex.roundToInt()].toString()
            invalidate()
        })

        if (!animator.isStarted) {
            animator.start()
        }
    }
}