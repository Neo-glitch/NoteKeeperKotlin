package com.neo.notekeeperkotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * Fully CustomView Class
 */
class ColorDialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    // default colors list
    private var colors: ArrayList<Int> = arrayListOf(
        Color.RED, Color.YELLOW,
        Color.BLUE, Color.GREEN, Color.DKGRAY, Color.CYAN, Color.MAGENTA, Color.BLACK
    )

    // ic_dial drawable member
    private var dialDrawable: Drawable? = null
    private var dialDiameter = toDp(100)

    // drawable to set no color
    private var noColorDrawable: Drawable? = null

    private var paint = Paint().also {
        it.color = Color.BLUE
        it.isAntiAlias = true
    }

    private var extraPadding = toDp(30)             // padding
    private var tickSize = toDp(10).toFloat()       // TickMarks size
    private var angleBetweenColors = 0f                   // angle btw each tickMark, and angle needed to rotate canvas based on num_color

    // the scale value and the scaled tickMark Size
    private var scale = 1f
    private var tickSizeScaled = tickSize * scale
    private var scaleToFit = false

    // pre-computed padding values, for the drawable plus extra padding for color tickMarks
    // can be used to know where to draw the dial drawable
    private var totalLeftPadding = 0f
    private var totalTopPadding = 0f
    private var totalRightPadding = 0f
    private var totalBottomPadding = 0f

    // pre-computed helper values
    private var horizontalSize = 0f
    private var verticalSize = 0f

    // pre-computed pos values
    private var tickPositionVertical =
        0f                  // how far down from top of View, we draw each tickMark
    private var centerHorizontal = 0f
    private var centerVertical = 0f


    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorDialView)
        try {
            // grabs ColorDialView_colors attr
            val customColors = typedArray.getTextArray(R.styleable.ColorDialView_colors)?.map {
                Color.parseColor(it.toString())
            } as ArrayList<Int>?
            customColors?.let {
                colors = customColors
            }
            // grabs dial diameter custom attr, where def value is "toDp(100)"
            dialDiameter = typedArray.getDimension(R.styleable.ColorDialView_dialDiameter, toDp(100).toFloat()).toInt()
            extraPadding = typedArray.getDimension(R.styleable.ColorDialView_tickPadding, toDp(30).toFloat()).toInt()
            tickSize = typedArray.getDimension(R.styleable.ColorDialView_tickRadius, toDp(10).toFloat())
            scaleToFit = typedArray.getBoolean(R.styleable.ColorDialView_scaleToFit, false)
        } finally {
            typedArray.recycle()
        }

        dialDrawable = context.getDrawable(R.drawable.ic_dial).also {
            it?.bounds = getCenteredBounds((dialDiameter * scale).toInt())
            it?.setTint(Color.DKGRAY)
        }
        noColorDrawable = context.getDrawable(R.drawable.ic_no_color).also {
            it?.bounds = getCenteredBounds((tickSize * scale).toInt(), 2f)
        }
        colors.add(0, Color.TRANSPARENT)
        angleBetweenColors = 360f / colors.size
        refreshValues(true)
    }

    /**
     * ret the bounds or rect bound needed to draw an image based on val passed
     */
    private fun getCenteredBounds(size: Int, scalar: Float = 1f): Rect {
        // scalar param is just used as a scale factor
        val half = ((if (size > 0) size / 2 else 1) * scalar).toInt()
        return Rect(-half, -half, half, half)
    }


    // width and heightMeasureSpec params is width and height ret by the sys to us
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(scaleToFit){
            // updates all fim with default 1f scale, since it might fit
            refreshValues(false)
            val specWidth = MeasureSpec.getSize(widthMeasureSpec)
            val specHeight = MeasureSpec.getSize(heightMeasureSpec)

            // gets space left to work with after removing padding
            val workingWidth = specWidth - paddingLeft - paddingRight
            val workingHeight = specHeight - paddingTop - paddingBottom
            scale = if(workingWidth < workingHeight){
                // if statement is used since we want to get the shortest dim to use in getting scale
                (workingWidth) / (horizontalSize - paddingLeft - paddingRight)
            } else {
                (workingHeight) / (verticalSize - paddingTop - paddingBottom)
            }
            dialDrawable?.let {
                it.bounds = getCenteredBounds((dialDiameter * scale).toInt())
            }
            noColorDrawable?.let {
                it.bounds = getCenteredBounds((tickSize * scale).toInt(), 2f)
            }
            val width = resolveSizeAndState(
                (horizontalSize * scale).toInt(), widthMeasureSpec, 0
            )
            val height = resolveSizeAndState(
                (verticalSize * scale).toInt(), heightMeasureSpec, 0
            )
            refreshValues(true)
            setMeasuredDimension(width, height)
        } else{
            // resolve the width and height and then we set the desired dimension to resolved width and height
            val width = resolveSizeAndState(horizontalSize.toInt(), widthMeasureSpec, 0)
            val height = resolveSizeAndState(verticalSize.toInt(), heightMeasureSpec, 0)
            setMeasuredDimension(width, height)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        colors.forEachIndexed { i, color ->
            if (i == 0) {
                canvas.translate(centerHorizontal, tickPositionVertical)
                noColorDrawable?.draw(canvas)
                // resets canvas back to it's original pos for next op, using reverse
                canvas.translate(-centerHorizontal, -tickPositionVertical)
            } else {
                paint.color = colors[i]
                canvas.drawCircle(centerHorizontal, tickPositionVertical, tickSizeScaled, paint)
            }
            canvas.rotate(angleBetweenColors, centerHorizontal, centerVertical)
        }
        canvas.restoreToCount(saveCount)
        canvas.translate(centerHorizontal, centerVertical)
        dialDrawable?.draw(canvas)

    }


    /**
     * handles computing for drawable to be drawn in onDraw()
     */
    private fun refreshValues(withScale: Boolean) {
        // if true set this var to the member scale value as set as 1f
        val localeScale = if(withScale) scale else 1f

        // compute padding values
        this.totalLeftPadding = paddingLeft + extraPadding * localeScale
        this.totalTopPadding = paddingTop + extraPadding * localeScale
        this.totalRightPadding = paddingRight + extraPadding * localeScale
        this.totalBottomPadding = paddingBottom + extraPadding * localeScale

        //Computer Helper val
        this.horizontalSize =
            paddingLeft + paddingRight + (extraPadding * localeScale * 2) + dialDiameter * localeScale
        this.verticalSize = paddingTop + paddingBottom + (extraPadding * localeScale * 2) + dialDiameter * localeScale

        // computer the half way point or center in each dim
        this.tickPositionVertical = paddingTop + extraPadding * localeScale / 2f
        this.centerHorizontal =
            totalLeftPadding + (horizontalSize - totalLeftPadding - totalRightPadding) / 2f
        this.centerVertical =
            totalTopPadding + (verticalSize - totalTopPadding - totalBottomPadding) / 2f
        this.tickSizeScaled = tickSize * localeScale
    }

    /**
     * function for converting from dp to pixel for drawing use
     */
    private fun toDp(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}
