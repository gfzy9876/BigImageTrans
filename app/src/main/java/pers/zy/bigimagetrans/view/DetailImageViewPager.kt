package pers.zy.bigimagetrans.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * date: 2019-11-03   time: 11:40
 * author zy
 * Have a nice day :)
 **/
class DetailImageViewPager(context: Context, attr: AttributeSet? = null) : ViewPager(context, attr) {

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return try {
            super.onInterceptHoverEvent(event)
        } catch (e: Exception) {
            false
        }
    }
}