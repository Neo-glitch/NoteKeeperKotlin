package com.neo.notekeeperkotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import kotlin.math.roundToInt

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
    private var noColorDrawable: Drawable? = null        // drawable to set no color

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
    private var tickPositionVertical = 0f     // how far down from top of View, we draw each tickMark
    private var centerHorizontal = 0f
    private var centerVertical = 0f

    //View interaction values(values to handle touch events and making dial rotate)
    private var dragStartX = 0f    // X cord of touch event
    private var dragStartY = 0f
    private var dragging = false
    private var snapAngle = 0f           // angle move dial move to, based on nearest color to dragging angle
    private var selectedPosition = 0    // rep current selected pos and use to to rep color in colors list


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

    // member var used to set the color of the dial to that coming from note object, when opening noteActivity
    var selectedColorValue: Int = android.R.color.transparent
    set(value) {
        val index = colors.indexOf(value)
        selectedPosition = if(index == -1) 0 else index
        // e.g if index is 2 and angle btw is 45 deg then angle to snap to is 90 deg making dial to move to val at index
        snapAngle = (selectedPosition * angleBetweenColors)
        invalidate()
    }

    // listener
    private var listeners: ArrayList<(Int) -> Unit> = arrayListOf()

    // higherOrder func
    fun addListeners(function: (Int) -> Unit){
        listeners.add(function)
    }


    /**
     * runs the lambda functions in the listener
     */
    private fun broadcastColorChange(){
        listeners.forEach {
            if(selectedPosition > colors.size - 1){
                // run fun with color index as int passed
                it(colors[0])
            } else{
                it(colors[selectedPosition])
            }
        }
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
        canvas.rotate(snapAngle, centerHorizontal, centerVertical)
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragStartX = event.x
        dragStartY = event.y
        if (event.action == ACTION_DOWN || event.action == ACTION_MOVE) {
            dragging = true

            //Figure out snap angle
            if (getSnapAngle(dragStartX, dragStartY)) {
                broadcastColorChange()
                // calls onDraw() making view to be redrawn with new values
                invalidate()
            }
        }
        if (event.action == ACTION_UP) {
            dragging = false
        }
        return true
    }


    /**
     * compute snap angle i.e angle to rotate dial to to get to the nearest Color tick
     * ret true when snap angle is diff from colorSelected angle
     */
    private fun getSnapAngle(x: Float, y: Float): Boolean{
        // angle dial is rotated to based on where event occurs in normal cart cord and not android cart
        var dragAngle = cartesianToPolar(x - horizontalSize / 2, (verticalSize - y) - verticalSize / 2)
        val nearest: Int = (getNearestAngle(dragAngle) / angleBetweenColors).roundToInt()         // index of nearest color
        val newAngle: Float = nearest * angleBetweenColors                                        // angle to snap to

        var shouldUpdate = false    // set to true if change in selected angle
        if(newAngle != snapAngle){
            shouldUpdate = true
            selectedPosition = nearest
        }
        snapAngle = newAngle
        return shouldUpdate
    }

    /**
     * gets angle of nearest color based on user rotation movement
     */
    private fun getNearestAngle(dragAngle: Float): Float{
        // to account for polar cord starting from RHS and increasing antiClockwise and resolve with our dial drag angle
        // e.g if dragAngle is 90 in polar cords this will convert it to 0deg i.e useful for our working
        var adjustedAngle = (360 - dragAngle) + 90
        while (adjustedAngle > 360) adjustedAngle -= 360
        return adjustedAngle
    }


    /**
     * function used to conv cartesian cords of touch on dial to polar cord for rotating dial
     */
    private fun cartesianToPolar(x: Float, y: Float): Float {
        var angle = Math.toDegrees((Math.atan2(y.toDouble(), x.toDouble()))).toFloat()
        return when(angle){
            in 0..180 -> angle
            in -180..0 -> angle + 360
            else -> angle
        }

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
