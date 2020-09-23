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
    private var angleBetweenColors =
        0f                   // angle btw each tickMark, and angle needed to rotate canvas based on num_color

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
        dialDrawable = context.getDrawable(R.drawable.ic_dial).also {
            // sets the bounds of drawable and the tint
            it?.bounds = getCenteredBounds(dialDiameter)
            it?.setTint(Color.DKGRAY)
        }
        noColorDrawable = context.getDrawable(R.drawable.ic_no_color).also {
            it?.bounds = getCenteredBounds(tickSize.toInt(), 2f)
        }
        colors.add(0, Color.TRANSPARENT)
        angleBetweenColors = 360f / colors.size
        refreshValues()
    }

    /**
     * ret the bounds or rect bound needed to draw an image based on val passed
     */
    private fun getCenteredBounds(size: Int, scalar: Float = 1f): Rect {
        // scalar param is just used as a scale factor
        val half = ((if (size > 0) size / 2 else 1) * scalar).toInt()
        return Rect(-half, -half, half, half)
    }

    override fun onDraw(canvas: Canvas) {
        val saveCount = canvas.save()

        // loops through each color getting the index and value for the color
        colors.forEachIndexed { i, color ->
            if (i == 0) {
                canvas.translate(centerHorizontal, tickPositionVertical)
                noColorDrawable?.draw(canvas)
                // resets canvas back to it's original pos for next op, using reverse
                canvas.translate(-centerHorizontal, -tickPositionVertical)
            } else {
                paint.color = colors[i]
                canvas.drawCircle(centerHorizontal, tickPositionVertical, tickSize, paint)
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
    private fun refreshValues() {
        // compute padding values
        this.totalLeftPadding = (paddingLeft + extraPadding).toFloat()
        this.totalTopPadding = (paddingTop + extraPadding).toFloat()
        this.totalRightPadding = (paddingRight + extraPadding).toFloat()
        this.totalBottomPadding = (paddingBottom + extraPadding).toFloat()


        //Computer Helper val
        this.horizontalSize =
            paddingLeft + paddingRight + (extraPadding * 2) + dialDiameter.toFloat()
        this.verticalSize = paddingTop + paddingBottom + (extraPadding * 2) + dialDiameter.toFloat()

        // computer the half way point or center in each dim
        this.tickPositionVertical = paddingTop + extraPadding / 2f
        this.centerHorizontal =
            totalLeftPadding + (horizontalSize - totalLeftPadding - totalRightPadding) / 2f
        this.centerVertical =
            totalTopPadding + (verticalSize - totalTopPadding - totalBottomPadding) / 2f
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
