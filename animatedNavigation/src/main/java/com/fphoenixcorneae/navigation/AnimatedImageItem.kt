package com.fphoenixcorneae.navigation

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

/**
 * @desc：AnimatedImageItem
 * @date：2022/08/15 11:28
 */
internal class AnimatedImageItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ShapeableImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_CONTENT_PADDING = 8f
    }

    private var mItemSize = Utils.dp2Px(52f)

    init {
        setContentPadding(DEFAULT_CONTENT_PADDING)
        backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        // 设置阴影效果
        elevation = Utils.dp2Px(4f).toFloat()
        // 剪裁圆形阴影
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setOval(0, 0, view.width, view.height)
            }
        }
        clipToOutline = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mItemSize, mItemSize)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        background = MaterialShapeDrawable(
            ShapeAppearanceModel.builder()
                .setAllCornerSizes(ShapeAppearanceModel.PILL)
                .build()
        ).apply {
            tintList = backgroundTintList
            paintStyle = Paint.Style.FILL
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        // do nothing
    }

    /**
     * Set item size
     * @param size 大小, dp value
     */
    fun setItemSize(size: Float) = apply {
        mItemSize = Utils.dp2Px(size)
    }

    /**
     * Set content padding
     * @param size 大小, dp value
     */
    fun setContentPadding(size: Float) = apply {
        val padding = Utils.dp2Px(size)
        setContentPadding(padding, padding, padding, padding)
    }

    /**
     * Set double click
     * @param validDuration 有效点击时间
     */
    fun setOnDoubleClickListener(validDuration: Long, block: () -> Unit) = apply {
        var hits = LongArray(3)
        setOnClickListener {
            // 将hits数组内的所有元素左移一个位置
            System.arraycopy(hits, 1, hits, 0, hits.size - 1)
            // 获得当前系统已经启动的时间
            hits[hits.lastIndex] = SystemClock.uptimeMillis()
            if (hits.last() - hits[1] <= validDuration && hits.last() - hits.first() >= validDuration) {
                // 相关逻辑操作
                block()
                // 初始化数组点击时间为上一次点击时间
                hits = hits.flatMap { listOf(hits.last()) }.toLongArray()
            }
        }
    }

    /**
     * @param id   图片资源id
     * @param tint 图片着色
     */
    fun loadData(id: Int, @ColorInt tint: Int? = null) = apply {
        setImageDrawable(ContextCompat.getDrawable(context, id))
        tint?.let {
            imageTintList = ColorStateList.valueOf(it)
        }
    }

    /**
     * Set touch delegate
     */
    fun setTouchDelegate() {
        // 解决 clipChildren 超出 parent 部分无法响应点击问题
        post {
            val hitRect = Rect()
            // 获取祖父布局中当前有效可点击区域
            ViewGroupUtils.getDescendantRect(this.parent.parent as ViewGroup?, this, hitRect)
            // 扩大布局点击区域
            hitRect.top -= measuredHeight / 2
            // 拦截事件分发，将AnimatedImageItem的touch事件委托给父视图
            (this.parent.parent as ViewGroup?)?.touchDelegate = SimpleTouchDelegate(hitRect, this)
        }
    }
}