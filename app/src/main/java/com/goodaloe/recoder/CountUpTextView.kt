package com.goodaloe.recoder

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpTextView(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var startTimeStamp: Long = 0L
    private val countUpAction: Runnable = object : Runnable {
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()
            val timeSecond = ((currentTimeStamp - startTimeStamp) / 1000L).toInt()
            updateCountTime(timeSecond)
            handler.postDelayed(this,1000L)
        }
    }

    fun startCountUp() {
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }

    fun stopCountUp() {
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountUp(){
        updateCountTime(0)
    }

    private fun updateCountTime(timeSecond: Int) {
        val min = timeSecond / 60;
        val sec = timeSecond % 60;
        text = "%02d:%02d".format(min, sec)

    }
}