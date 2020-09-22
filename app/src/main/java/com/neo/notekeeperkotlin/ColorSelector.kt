package com.neo.notekeeperkotlin

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.color_selector.view.*
import kotlinx.android.synthetic.main.content_main.view.*


/**
 * compound component class that extends LinearLayout
 * @ jvmOverloads annot is used since java instantiating our views doesn't accept concept of default params
 */
class ColorSelector @JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
    defRes: Int = 0
) :
    LinearLayout(context, attributeSet, defStyle, defRes) {

    private var listOfColors = listOf(Color.BLUE, Color.RED, Color.GREEN)

    // stores index of selected color in the list above
    private var selectedColorIndex = 0;

    var selectedColorValue : Int = android.R.color.transparent
        set(value) {
            var index = listOfColors.indexOf(value)
            if(index == -1){
                // index not in color list
                colorEnabled.isChecked = false
                index = 0
            } else{
                colorEnabled.isChecked = true
            }
            selectedColorIndex = index
            selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])
        }


    init {
        // looks through set of attr assoc with this view and pull out colorSelector styleable if available
        val typedArray = context.obtainStyledAttributes(
            attributeSet, R.styleable.ColorSelector
        )
        listOfColors = typedArray.getTextArray(R.styleable.ColorSelector_colors)
            .map {
                Color.parseColor(it.toString())
            }
        // frees the typed array after use to avoid memLeaks
        typedArray.recycle()

        orientation = LinearLayout.HORIZONTAL
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // inflates the layout designed in xml into this LinearLayout(customView)
        inflater.inflate(R.layout.color_selector, this)

        selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])

        colorSelectorArrowLeft.setOnClickListener {
            selectPreviousColor()
        }

        colorSelectorArrowRight.setOnClickListener {
            selectNextColor()
        }

        colorEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
            broadcastColor()
        }
    }

    // listener( listener is a function)
//    private var colorSelectListeners: ((Int) -> Unit)? = null

    // listener(ArrayList of functions)
    private var colorSelectListeners: ArrayList<(Int) -> Unit> = arrayListOf()


    // higher order function that accepts func that to receive the color as int and ret void
    fun addListener(function: (Int) -> Unit){
        this.colorSelectListeners.add(function)
    }



    private fun selectPreviousColor() {
        if (selectedColorIndex == 0) {
            // it at index zero it goes back to last color in list
            selectedColorIndex = listOfColors.lastIndex
        } else {
            selectedColorIndex--
        }
        selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])
        broadcastColor()
    }

    private fun selectNextColor() {
        if (selectedColorIndex == listOfColors.lastIndex) {
            selectedColorIndex = 0
        } else{
            selectedColorIndex++
        }
        selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])
        broadcastColor()
    }

    // method calls the interface fun
    private fun broadcastColor(){
        val color = if(colorEnabled.isChecked)
            listOfColors[selectedColorIndex]
        else
            Color.TRANSPARENT

        // runs the functions passed to the higherOrder function
        this.colorSelectListeners.forEach {
            function -> function(color)
        }
    }
}