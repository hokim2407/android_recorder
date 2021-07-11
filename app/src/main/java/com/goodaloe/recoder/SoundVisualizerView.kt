package com.goodaloe.recoder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

class SoundVisualizerView(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }
    private var drawingWidth: Int = 0;
    private var drawingHeight: Int = 0;
    private var drawingAmps: List<Int> = emptyList()
    private var isReplaying : Boolean = false
    private var replayingPosition : Int = 0;
    var onRequestCurrentAmp: (()->Int) ? = null

    private val visualizeRepeatAction : Runnable = object : Runnable{
        override fun run() {
            if(!isReplaying) {
                val currentAmp = onRequestCurrentAmp?.invoke() ?: 0
                drawingAmps = listOf(currentAmp) + drawingAmps
            }
            else
                replayingPosition++
            invalidate()

            handler?.postDelayed(this,ACTION_INTERVAL)
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w;
        drawingHeight = h;
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        val centerY = drawingHeight / 2f
        var offsetX = drawingWidth.toFloat()

        drawingAmps
            .let{ amps ->
                if(isReplaying)
                    amps.takeLast(replayingPosition)
                else
                    amps
            }
            .forEach { amp ->
            val lineLength = amp / MAX_AMP * drawingHeight * 0.8f
            offsetX -= LINE_SPACE
            if (offsetX < 0)
                return@forEach
            canvas.drawLine(
                offsetX,
                centerY - lineLength / 2,
                offsetX,
                centerY + lineLength / 2,
                amplitudePaint
            )
        }
    }

    fun startVisualizing(isReplaying:Boolean){
        this.isReplaying = isReplaying
        if(isReplaying)
            replayingPosition = 0
        handler?.post (visualizeRepeatAction)
    }

    fun stopVisualizing(){
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualizing(){
       drawingAmps = emptyList()
        invalidate()
    }

    companion object {
        private const val LINE_WIDTH = 10f
        private const val LINE_SPACE = 15f
        private const val MAX_AMP = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }
}