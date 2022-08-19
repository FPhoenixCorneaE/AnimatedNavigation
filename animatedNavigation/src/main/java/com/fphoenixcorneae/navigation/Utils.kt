package com.fphoenixcorneae.navigation

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * @desc：Utils
 * @date：2022/08/15 11:37
 */
internal class Utils {

    companion object {
        fun dp2Px(value: Float) = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            Resources.getSystem().displayMetrics
        ).roundToInt()

        fun px2Dp(value: Float) = value / Resources.getSystem().displayMetrics.density + 0.5f

        /**
         * 不同屏幕尺寸的屏幕px转px
         * @param pxValue    screenSize屏幕下px值
         * @param screenSize 屏幕尺寸
         * @return 当前ScreenSize的px值
         */
        fun pxToPx(pxValue: Float, screenSize: Float): Float {
            return Resources.getSystem().displayMetrics.widthPixels * pxValue / screenSize
        }
    }
}