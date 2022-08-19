package com.fphoenixcorneae.navigation

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

/**
 * @desc：贝塞尔凹形曲线
 * @date：2022/08/19 15:14
 */
internal class ConcaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    init {
        backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        // 设置阴影效果
        elevation = Utils.dp2Px(16f).toFloat()
    }

    /**
     * Set background
     */
    fun setBackground(fraction: Float, centerX: Float) {
        background = MaterialShapeDrawable(
            ShapeAppearanceModel.builder()
                .setTopEdge(BottomBarTopEdgeTreatment(fraction, centerX))
                .build()
        ).apply {
            tintList = backgroundTintList
            paintStyle = Paint.Style.FILL
        }
    }
}