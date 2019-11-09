package pers.zy.bigimagetrans.translib

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import pers.zy.bigimagetrans.getScreenHeight
import kotlin.math.abs
import kotlin.math.max

/**
 * date: 2019-11-07   time: 14:57
 * author zy
 * Have a nice day :)
 **/
class ImageDetailFrameLayout(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    val MAX_MOVE_EXIT_LENGTH = getScreenHeight(context) / 4
    val MIN_SCALE = 0.4f

    private val orgPosition = PointF()
    private val currentPosition = PointF()
    private val topPosition = PointF()
    private var isIntercept = false
    private var moveExitListener: OnMoveExitListener? = null

    fun setOnMoveExitListener(moveExitListener: OnMoveExitListener) {
        this.moveExitListener = moveExitListener
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        if (event.pointerCount > 1) {
            return super.dispatchTouchEvent(event)
        }
        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                topPosition.x = x
                topPosition.y = y
                orgPosition.x = event.rawX
                orgPosition.y = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                currentPosition.x = event.rawX
                currentPosition.y = event.rawY
                if (isIntercept) {
                    x = currentPosition.x - orgPosition.x + topPosition.x
                    y = currentPosition.y - orgPosition.y + topPosition.y
                    val fraction = 1 - if (currentPosition.y - orgPosition.y <= 0) {
                        0f
                    } else {
                        (currentPosition.y - orgPosition.y) * 1f / MAX_MOVE_EXIT_LENGTH
                    }
                    val scale = minOf(1f, max(MIN_SCALE, fraction))
                    moveExitListener?.onMove(scale)
                    scaleX = scale
                    scaleY = scale
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount > 1) {
            return super.onInterceptTouchEvent(ev)
        }
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                return super.onInterceptTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                val scaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
                val yOffset = abs(currentPosition.y - orgPosition.y)
                val xOffset = abs(currentPosition.x - orgPosition.x)

                if ((yOffset >= scaledTouchSlop || xOffset < scaledTouchSlop)) {
                    if (yOffset > xOffset) {
                        isIntercept = true
                        return true
                    } else {
                        return super.onInterceptTouchEvent(ev)
                    }
                } else {
                    return super.onInterceptTouchEvent(ev)
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isIntercept = false
                val exit = abs(currentPosition.y - orgPosition.y) >= MAX_MOVE_EXIT_LENGTH
                if (exit) {
                    moveExitListener?.onExit()
                } else {
                    moveExitListener?.restore()
                    animate().setDuration(200).x(topPosition.x).y(topPosition.y).scaleX(1f).scaleY(1f).start()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    interface OnMoveExitListener {
        fun onMove(fraction: Float)
        fun restore()
        fun onExit()
    }
}