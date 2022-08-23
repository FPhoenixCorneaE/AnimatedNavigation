package com.fphoenixcorneae.navigation

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

/**
 * @desc：SimpleTouchDelegate
 * @date：2021/07/30 13:43
 */
class SimpleTouchDelegate(
    targetBounds: Rect,
    /**
     * View that should receive forwarded touch events
     */
    private val mDelegateView: View,
) : TouchDelegate(targetBounds, mDelegateView) {
    /**
     * Bounds in local coordinates of the containing view that should be mapped to the delegate
     * view. This rect is used for initial hit testing.
     */
    private val mTargetBounds = Rect()

    /**
     * Bounds in local coordinates of the containing view that are actual bounds of the delegate
     * view. This rect is used for event coordinate mapping.
     */
    private val mActualBounds = Rect()

    /**
     * mTargetBounds inflated to include some slop. This rect is to track whether the motion events
     * should be considered to be be within the delegate view.
     */
    private val mSlopBounds = Rect()
    private val mSlop: Int = ViewConfiguration.get(mDelegateView.context).scaledTouchSlop

    /**
     * True if the delegate had been targeted on a down event (intersected mTargetBounds).
     */
    private var mDelegateTargeted = false

    fun setBounds(desiredBounds: Rect) {
        mTargetBounds.set(desiredBounds)
        mSlopBounds.set(desiredBounds)
        mSlopBounds.inset(-mSlop, -mSlop)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        var sendToDelegate = false
        var hit = true
        var handled = false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 1.Down事件，判断手指是否落在事件扩大范围内
                mDelegateTargeted = mTargetBounds.contains(x, y)
                sendToDelegate = mDelegateTargeted
            }
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_MOVE,
            -> {
                sendToDelegate = mDelegateTargeted
                if (sendToDelegate) {
                    // 2.非Down事件，判断手指是否超出了slopBounds，slopBounds是在mTargetBounds的区域上再扩大一定的范围，
                    // 如果超出，向mDelegateView发送一个负值事件坐标
                    if (!mSlopBounds.contains(x, y)) {
                        hit = false
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                sendToDelegate = mDelegateTargeted
                mDelegateTargeted = false
            }
            else -> {
            }
        }
        if (sendToDelegate) {
            if (hit) {
                // 3.将事件坐标设置为mDelegateView的中心点
                event.setLocation(mDelegateView.width / 2f, mDelegateView.height / 2f)
            } else {
                // 4.将事件坐标设置为负值
                val slop = mSlop
                event.setLocation(-(slop * 2).toFloat(), -(slop * 2).toFloat())
            }
            // 5.将事件交由mDelegateView分发
            handled = mDelegateView.dispatchTouchEvent(event)
        }
        return handled
    }

    init {
        setBounds(targetBounds)
    }
}