package com.goodaloe.recoder

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class RecordButton(context: Context, attr: AttributeSet) : AppCompatImageButton(context, attr) {
    init {
        setBackgroundResource(R.drawable.shape_oval_btn)
    }

    fun updateIconWithState(state: State) {

        when (state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop)
            }
        }

    }

}