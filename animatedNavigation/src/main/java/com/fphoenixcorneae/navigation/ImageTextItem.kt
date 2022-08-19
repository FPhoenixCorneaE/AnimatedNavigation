package com.fphoenixcorneae.navigation

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat

/**
 * @desc：ImageTextItem
 * @date：2022/08/15 11:27
 */
class ImageTextItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        private val DEFAULT_ICON_SIZE = Utils.dp2Px(28f)
        private val DEFAULT_ICON_TEXT_PADDING = Utils.dp2Px(4f)
        private const val DEFAULT_TEXT_SIZE = 16f
        private const val DEFAULT_TEXT_COLOR = Color.BLACK
    }

    private val mStates by lazy {
        arrayOf(intArrayOf(-android.R.attr.state_selected), intArrayOf(android.R.attr.state_selected))
    }
    private var mSelectedColorTint = 0
    private var mIconResource = 0

    init {
        gravity = Gravity.CENTER
        textSize = DEFAULT_TEXT_SIZE
        setTextColor(DEFAULT_TEXT_COLOR)
        compoundDrawablePadding = DEFAULT_ICON_TEXT_PADDING
    }

    /**
     * Set icon size
     * @param size 大小, dp value
     */
    fun setIconSize(size: Float) = apply {
        val iconSize = Utils.dp2Px(size)
        compoundDrawables.onEach {
            it?.setBounds(0, 0, iconSize, iconSize)
        }.also {
            setCompoundDrawables(it.getOrNull(0), it.getOrNull(1), it.getOrNull(2), it.getOrNull(3))
        }
    }

    /**
     * Set icon
     * @param id 图片资源id
     */
    fun setIconResource(id: Int) = apply {
        mIconResource = id
        ContextCompat.getDrawable(context, id)?.apply {
            setBounds(0, 0, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE)
        }?.let {
            setCompoundDrawables(null, it, null, null)
        }
    }

    /**
     * Set padding between icon and text
     * @param padding icon 与 text 之间间距, dp value
     */
    fun setIconTextPadding(padding: Float) = apply {
        compoundDrawablePadding = Utils.dp2Px(padding)
    }

    /**
     * Set normal state tint color and selected state tint color
     * @param colorTint         图片着色
     * @param selectedColorTint 图片选中时着色
     */
    fun setIconTextColor(@ColorInt colorTint: Int, @ColorInt selectedColorTint: Int) = apply {
        mSelectedColorTint = selectedColorTint
        val colors = intArrayOf(colorTint, selectedColorTint)
        TextViewCompat.setCompoundDrawableTintList(this@ImageTextItem, ColorStateList(mStates, colors))
        setTextColor(ColorStateList(mStates, colors))
    }

    fun getIconResource() = mIconResource

    fun getSelectedColorTint() = mSelectedColorTint
}