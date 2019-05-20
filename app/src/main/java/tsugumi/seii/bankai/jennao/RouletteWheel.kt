package tsugumi.seii.bankai.jennao

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import android.util.TypedValue



// TODO on saved instance state
class RouletteWheel : View{

    private lateinit var mBallPaint: Paint
    private lateinit var mPrimaryPaint: Paint
    private lateinit var mSecondaryPaint: Paint
    private lateinit var mGoldPaint: Paint
    private lateinit var mDarkGoldPaint: Paint
    private lateinit var mOddPaint: Paint
    private lateinit var mBaseRect: RectF

    private var mOuterWheelSize: Float = 0f
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
        mBaseRect = RectF()

        mPrimaryPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSecondaryPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteSilver) }
        mGoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteGold) }
        mOddPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteGreen) }
        mDarkGoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.rouletteDarkGold)
            style = Paint.Style.STROKE
        }

        attrs?.let {
            with(context.obtainStyledAttributes(attrs, R.styleable.RouletteWheel)){
                mPrimaryPaint.apply {
                    color = getColor(R.styleable.RouletteWheel_primary_color,
                        context.getColor(R.color.rouletteRed))
                    style = Paint.Style.STROKE
                }
                mSecondaryPaint.apply {
                    color = getColor(R.styleable.RouletteWheel_secondary_color,
                        context.getColor(R.color.rouletteBlack))
                    style = Paint.Style.STROKE
                }
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minDiameterInDp = 200f
        val minDiameterInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minDiameterInDp, resources.displayMetrics)

        val resolvedWidth = resolveSize(Math.round(minDiameterInPx+paddingStart+paddingEnd), widthMeasureSpec)
        val resolvedHeight = resolveSize(Math.round(minDiameterInPx+paddingTop+paddingBottom), heightMeasureSpec)

        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val wheelDiameter: Float = (if(width < height) width else height).toFloat()/8*6

        with(mBaseRect){
            left = width/2 - wheelDiameter/2
            right = width/2 + wheelDiameter/2
            top = height/2 - wheelDiameter/2
            bottom = height/2 + wheelDiameter/2
        }

        configurePaintStrokeSizes(wheelDiameter)

        drawOuterWheel(canvas)

        // Rotate canvas before drawing stuff that will be rotated
        canvas.rotate(mWheelRotation, mBaseRect.centerX(), mBaseRect.centerY())

        drawBallBall(canvas, wheelDiameter/2)

        drawInnerBase(canvas, wheelDiameter/2)

        Log.i("rotate","$mWheelRotation")
    }

    private fun configurePaintStrokeSizes(wheelSize: Float){
        mOuterWheelSize = wheelSize/5

        listOf(mPrimaryPaint,mSecondaryPaint).forEach{
            it.apply {
                strokeWidth = mOuterWheelSize
            }
        }

        mDarkGoldPaint.strokeWidth = mOuterWheelSize/8
    }

    private fun drawBallBall(canvas: Canvas, wheelRadius: Float){
        canvas.drawCircle(mBaseRect.centerX() - wheelRadius, mBaseRect.centerY(),  mOuterWheelSize/6, mBallPaint)
    }

    private fun drawOuterWheel(canvas: Canvas){
        var usePrimaryColor = true
        val step = 30
        for(i in step/2..360-step/2 step step){
            canvas.drawArc(mBaseRect,i.toFloat(),step.toFloat(),
                false,if (usePrimaryColor) mPrimaryPaint else mSecondaryPaint)
            usePrimaryColor = !usePrimaryColor
        }
    }

    private fun drawInnerBase(canvas: Canvas, wheelRadius: Float){
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),
            wheelRadius - mOuterWheelSize*1.6f, mGoldPaint)
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),
            wheelRadius - mOuterWheelSize*1.6f, mDarkGoldPaint)
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),
            wheelRadius - mOuterWheelSize/2, mDarkGoldPaint)
    }

    fun spin(travelAngle: Long){
        val previousRotation = mWheelRotation
        val animator = ValueAnimator.ofFloat(0f, travelAngle.toFloat())
        animator.duration = travelAngle*10
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            mWheelRotation = previousRotation + animation.animatedValue as Float

            invalidate()
        }
        animator.addListener(onEnd = {
            mWheelRotation = previousRotation + travelAngle
        })

        if (!animator.isStarted) {
            animator.start()
        }
    }
}