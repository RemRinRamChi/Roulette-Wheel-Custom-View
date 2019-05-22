package tsugumi.seii.bankai.jennao

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat


// TODO on saved instance state
class RouletteWheelView : View{
    companion object {
        private val ROULETTE_NUMBERS =
            listOf(0,28,9,26,30,11,7,20,32,17,5,22,34,15,3,24,36,13,1,"00",27,10,25,29,12,8,19,31,18,6,21,33,16,4,23,35,14,2)
    }

    private lateinit var mBallPaint: Paint
    private lateinit var mPrimaryPaint: Paint
    private lateinit var mSecondaryPaint: Paint
    private lateinit var mGoldPaint: Paint
    private lateinit var mDarkGoldPaint: Paint
    private lateinit var mOddPaint: Paint

    private lateinit var mDarkGoldTextPaint: TextPaint

    private lateinit var mBaseRect: RectF
    private lateinit var mBoundingRect: Rect

    private var mWheelHeadWidth: Float = 0f
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
        // Initialise non-configurable settings
        mBaseRect = RectF()
        mBoundingRect = Rect()

        mPrimaryPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSecondaryPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteSilver) }
        mGoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteGold) }
        mOddPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = context.getColor(R.color.rouletteGreen) }
        mDarkGoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.rouletteDarkGold)
            style = Paint.Style.STROKE
        }
        mDarkGoldTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.rouletteDarkGold)
            textAlign = Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(context, R.font.playfair_display_bold)
        }

        attrs?.let {
            with(context.obtainStyledAttributes(attrs, R.styleable.RouletteWheelView)){
                mPrimaryPaint.apply {
                    color = getColor(R.styleable.RouletteWheelView_primary_color,
                        context.getColor(R.color.rouletteRed))
                    style = Paint.Style.STROKE
                }
                mSecondaryPaint.apply {
                    color = getColor(R.styleable.RouletteWheelView_secondary_color,
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
        val wheelDiameter: Float = (if(width < height) width else height).toFloat()/16*14

        with(mBaseRect){
            left = width/2 - wheelDiameter/2
            right = width/2 + wheelDiameter/2
            top = height/2 - wheelDiameter/2
            bottom = height/2 + wheelDiameter/2
        }

        configurePaintStrokeSizes(wheelDiameter)


        drawWheelBase(canvas, wheelDiameter/2)
        drawWheelHead(canvas)

        // Rotate canvas before drawing stuff that will be rotated
        canvas.rotate(mWheelRotation, mBaseRect.centerX(), mBaseRect.centerY())

        drawBallBall(canvas, wheelDiameter/2)
        Log.i("rotate","$mWheelRotation")
    }

    private fun configurePaintStrokeSizes(wheelDiameter: Float){
        mWheelHeadWidth = wheelDiameter/10

        listOf(mPrimaryPaint,mSecondaryPaint).forEach{
            it.apply {
                strokeWidth = mWheelHeadWidth
            }
        }

        mDarkGoldPaint.strokeWidth = mWheelHeadWidth/8

        mDarkGoldTextPaint.textSize = wheelDiameter/4
    }

    private fun drawBallBall(canvas: Canvas, wheelRadius: Float){
        canvas.drawCircle(mBaseRect.centerX() - wheelRadius, mBaseRect.centerY(),  mWheelHeadWidth/6, mBallPaint)
    }

    private fun drawWheelHead(canvas: Canvas){
        val step = 360f/38

        var usePrimaryColor = true
        var angleCovered = -90 - step/2
        for(n in ROULETTE_NUMBERS){

        }
        while(angleCovered < 380){
            canvas.drawArc(mBaseRect, angleCovered, step,
                false,if (usePrimaryColor) mPrimaryPaint else mSecondaryPaint)
            usePrimaryColor = !usePrimaryColor
            angleCovered += step
        }
    }

    private fun drawWheelBase(canvas: Canvas, wheelRadius: Float){
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),wheelRadius - mWheelHeadWidth /2*3, mGoldPaint)
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),wheelRadius - mWheelHeadWidth /2*3, mDarkGoldPaint)
        canvas.drawCircle(mBaseRect.centerX(), mBaseRect.centerY(),wheelRadius - mWheelHeadWidth /2, mDarkGoldPaint)

        canvas.drawCenterText(context.getString(R.string.roulette_wheel_centre_label),
            mBaseRect.centerX(), mBaseRect.centerY(), mDarkGoldTextPaint, mBoundingRect)
    }

    private fun Canvas.drawCenterText(text: String, centerX: Float, centerY:Float, paint:TextPaint, boudingRect: Rect){
        paint.getTextBounds(text,0, text.length,boudingRect)
        drawText(text,centerX, centerY-boudingRect.exactCenterY(),paint)
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