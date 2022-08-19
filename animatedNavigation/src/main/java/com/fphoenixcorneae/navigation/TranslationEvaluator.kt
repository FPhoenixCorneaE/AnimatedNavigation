package com.fphoenixcorneae.navigation

import android.animation.TypeEvaluator
import kotlin.math.cos

/**
 * @desc：TranslationEvaluator
 * @date：2022/08/18 16:46
 */
internal class TranslationEvaluator(
    /** 小球最大向下位移距离 */
    private val maxTranslationY: Float,
) : TypeEvaluator<Any?> {
    override fun evaluate(fraction: Float, startValue: Any?, endValue: Any?): Any {
        val startTranslation = startValue as? Pair<Float, Float>
        val endTranslation = endValue as? Pair<Float, Float>
        return if (startTranslation != null && endTranslation != null) {
            // x方向为三角函数曲线的运动轨迹
            val translationX =
                (-0.5f * cos(fraction * Math.PI).toFloat() + 0.5f) * (endTranslation.first - startTranslation.first) + startTranslation.first

            val v0 = fraction * 2.0f - 1.0f
            //y方向为抛物线的运动轨迹
            val translationY = (-v0 * v0 + 1.0f) * (maxTranslationY - 0)
            translationX to translationY
        } else {
            0f to 0f
        }
    }
}