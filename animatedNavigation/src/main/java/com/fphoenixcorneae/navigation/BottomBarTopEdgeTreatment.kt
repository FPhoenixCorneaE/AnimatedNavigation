package com.fphoenixcorneae.navigation

import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.ShapePath

/**
 * 根据球的上下位置，画贝塞尔曲线。仔细观察移动的过程中，凹的形状是不一样的：
 * 球在上面的时候凹形大，球在下面的时候凹形状小
 * @desc：BottomBarTopEdgeTreatment
 * @date：2022/08/18 11:42
 */
internal class BottomBarTopEdgeTreatment(
    private val fraction: Float,
    private val centerX: Float,
) : EdgeTreatment() {

    companion object {
        /** 凹陷最大宽度 */
        private val MAX_WIDTH = Utils.pxToPx(300f, 1080f)

        /** 凹陷最小宽度 */
        private val MIN_WIDTH = Utils.pxToPx(240f, 1080f)

        /** 凹陷最大深度 */
        private val MAX_DEPTH = Utils.pxToPx(120f, 1080f)

        /** 凹陷最小深度 */
        private val MIN_DEPTH = Utils.pxToPx(100f, 1080f)

        /** 控制点A */
        private val CONTROL_A0 = Utils.pxToPx(60f, 1080f)
        private val CONTROL_A1 = Utils.pxToPx(30f, 1080f)

        /** 控制点B */
        private val CONTROL_B0 = Utils.pxToPx(100f, 1080f)
        private val CONTROL_B1 = Utils.pxToPx(40f, 1080f)
    }

    override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
        // 凹陷半径
        val r = (MAX_WIDTH + fraction * (MIN_WIDTH - MAX_WIDTH)) / 2f
        // 凹陷深度
        val depth = MAX_DEPTH + fraction * (MIN_DEPTH - MAX_DEPTH)
        val controlA = CONTROL_A0 + fraction * (CONTROL_A1 - CONTROL_A0)
        val controlB = CONTROL_B0 + fraction * (CONTROL_B1 - CONTROL_B0)
        // left
        val cl = centerX - r
        // right
        val cr = centerX + r
        // 第一部分，直线，画到凹陷开始的地方
        shapePath.lineTo(cl, 0f)
        // 凹陷开始的地方的转角
        shapePath.cubicToPoint(cl + controlA, 0f, centerX - controlB, depth, centerX, depth)
        // 凹陷结束的地方的转角
        shapePath.cubicToPoint(centerX + controlB, depth, cr - controlA, 0f, cr, 0f)
        // 最后一部分的直线，画到底
        shapePath.lineTo(length, 0f)
    }
}