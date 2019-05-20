package tsugumi.seii.bankai.jennao

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View


class DecisionWheel : View{

    private lateinit var mPrimaryWheelPaint: Paint
    private lateinit var mSecondaryWheelPaint: Paint
    private lateinit var mRect: RectF

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
        mPrimaryWheelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSecondaryWheelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRect = RectF()

        attrs?.let {
            with(context.obtainStyledAttributes(attrs, R.styleable.DecisionWheel)){

                mPrimaryWheelPaint.color = getColor(R.styleable.DecisionWheel_primary_color,Color.GREEN)
                mSecondaryWheelPaint.color = getColor(R.styleable.DecisionWheel_secondary_color,Color.RED)

                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(mRect){
            left = width.toFloat()*1/8
            right = width.toFloat()*7/8
            top = height.toFloat()/2-width.toFloat()*3/8
            bottom = height.toFloat()/2+width.toFloat()*3/8
        }

        var usePrimaryColor = true
        for(i in 0..360 step 30){
            canvas.drawArc(mRect,i.toFloat(),30.toFloat(),
                true,if (usePrimaryColor) mPrimaryWheelPaint else mSecondaryWheelPaint)
            usePrimaryColor = !usePrimaryColor
        }
    }
}