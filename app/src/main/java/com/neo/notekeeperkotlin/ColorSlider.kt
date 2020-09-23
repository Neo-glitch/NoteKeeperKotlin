package com.neo.notekeeperkotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.SeekBar
import androidx.core.content.ContextCompat


/**
 * customView class that extends SeekBar view
 */
class ColorSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    // default must be set to this
    defStyleAttr: Int = R.attr.seekBarStyle,
    defStyleRes: Int = 0
) : SeekBar(context, attrs, defStyleAttr, defStyleRes) {


    // default list of color
    private var colors: ArrayList<Int> = arrayListOf(Color.RED, Color.YELLOW, Color.BLUE)

    private val w = getPixelValueFromDp(20f)  // width of color swatch
    private val h = getPixelValueFromDp(20f)  // height of color swatch
    private val halfW = if (w >= 0) w / 2f else 1f
    private val halfH = if (h >= 0) h / 2f else 1f
    val paint = Paint()

    var w2 = 0
    private var h2 = 0
    private var halfW2 = 1
    private var halfH2 = 1

    private var noColorDrawable : Drawable? = null
    set(value) {
        w2 = value?.intrinsicWidth ?: 0
        h2 = value?.intrinsicHeight ?: 0
        halfW2 = if (w >= 0) w2 / 2 else 1
        halfH2 = if (h >= 0) h2 / 2 else 1
        value?.setBounds(-halfW2, -halfH2, halfW2, halfH2)
        field = value        // makes noColorDrawable == value passed
    }


    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorSlider)
        try {
            colors = typedArray.getTextArray(R.styleable.ColorSlider_colors)
                .map {
                    Color.parseColor(it.toString())
                } as ArrayList<Int>
        } finally {
            typedArray.recycle()
        }

        // color added so user can choose this, if no color needed
        colors.add(0, android.R.color.transparent)

        // sets values for some of SeekBar class attr
        max = colors.size - 1
        progressBackgroundTintList =
            ContextCompat.getColorStateList(context, android.R.color.transparent)
        progressTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        splitTrack = false
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + getPixelValueFromDp(20f).toInt())
        thumb = context.getDrawable(R.drawable.ic_color_slider_thumb)
        noColorDrawable = context.getDrawable(R.drawable.ic_no_color)

        // listener that fires when thumb pos changes
        setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listeners.forEach {
                    // runs each function attach to the listener, progress is selected tickmark on slider
                    it(colors[progress])
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    // member var that updates color of color slider depending on value passed by the activity when
    // item is clicked on itemActivity rv
    var selectedColorValue: Int = android.R.color.transparent
        set(value) {
            var index = colors.indexOf(value)
            if (index == -1) {
                progress = 0
            } else {
                progress = index
            }
        }

    private var listeners: ArrayList<(Int) -> Unit> = arrayListOf()

    // higherOrder func for comm to host activity
    fun addListener(function: (Int) -> Unit) {
        listeners.add(function)
    }

    override fun onDraw(canvas: Canvas?) {
        // where drawing takes place
        super.onDraw(canvas)
        drawTickMarks(canvas)
    }


    private fun drawTickMarks(canvas: Canvas?) {
        canvas?.let {
            // runs when canvas != null
            val count = colors.size
            val saveCount = canvas.save()

            canvas.translate(paddingLeft.toFloat(), (height / 2).toFloat() + getPixelValueFromDp(20f))
            if (count > 1) {
                // calc spacing in between each square
                val spacing = (width - paddingLeft - paddingRight) / (count - 1).toFloat()
                for (i in 0 until count) {
                    // drawing process
                    if (i == 0) {
                        // drawing first tick mark, and draw drawable "X"
                        noColorDrawable?.draw(canvas)
                    } else {
                        paint.color = colors[i]
                        canvas.drawRect(-halfW, -halfH, halfW, halfH, paint)
                    }
                    // moves canvas to next point
                    canvas.translate(spacing, 0f)
                }
                // restore to the bookmarked pos
                canvas.restoreToCount(saveCount)
            }
        }
    }


    // fun converts from dp to pixel
    private fun getPixelValueFromDp(value: Float): Float{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.resources.displayMetrics)
    }

}
