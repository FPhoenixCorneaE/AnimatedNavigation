package com.fphoenixcorneae.navigation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import kotlin.math.abs

typealias OnItemClickListener = (itemView: ImageTextItem, position: Int) -> Unit

/**
 * @desc：AnimatedNavigation
 * @date：2022/08/12 17:53
 */
class AnimatedNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr), View.OnClickListener {

    companion object {
        // 默认动画时长
        private const val DEFAULT_ANIMATED_DURATION = 250L
    }

    private val mAnimatorSet by lazy { AnimatorSet() }

    /** is animator starting */
    private var isAnimatorStarting = false

    /** item 列表 */
    private var mItems = mutableListOf<ImageTextItem>()

    /** item 宽度 */
    private var mItemWidth = 0

    /** item click listener */
    private var mOnItemClickListener: OnItemClickListener? = null

    /** animated item */
    private val mAnimatedImageItem = AnimatedImageItem(context, attrs, defStyleAttr)

    /** concave view */
    private val mConcaveView = ConcaveView(context, attrs, defStyleAttr)

    /** current index */
    private var mCurrentIndex = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // measure children
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        when (widthSpecMode) {
            MeasureSpec.AT_MOST -> {
                // 相当于设置为wrap_content
                widthSpecSize = Resources.getSystem().displayMetrics.widthPixels
            }
            MeasureSpec.EXACTLY -> {
                // 相当于设置为match_parent或者为一个具体的值
            }
            MeasureSpec.UNSPECIFIED -> {}
        }
        when (heightSpecMode) {
            MeasureSpec.AT_MOST -> {
                // 相当于设置为wrap_content
                heightSpecSize = Utils.dp2Px(60f)
            }
            MeasureSpec.EXACTLY -> {
                // 相当于设置为match_parent或者为一个具体的值
            }
            MeasureSpec.UNSPECIFIED -> {}
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // set background
        mAnimatedImageItem.post {
            mConcaveView.setBackground(0f, (mAnimatedImageItem.left + mAnimatedImageItem.right) / 2f)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount <= 0) {
            return
        }
        if (mItems.isNotEmpty()) {
            mItemWidth = measuredWidth / mItems.size
        }
        mItems.forEachIndexed { index, view ->
            // 自定义ViewGroup添加TextView文字不居中的bug，
            // 解决方案为：1.设置minimumWidth和minimumHeight 2.执行measure(0, 0)
            view.minimumWidth = mItemWidth
            view.minimumHeight = view.measuredHeight
            view.measure(0, 0)
            view.layout(
                index * mItemWidth,
                (measuredHeight - view.measuredHeight) / 2,
                (index + 1) * mItemWidth,
                (measuredHeight + view.measuredHeight) / 2
            )
        }
        mAnimatedImageItem.let {
            it.layout(
                (mItemWidth - it.measuredWidth) / 2,
                -it.measuredHeight / 2,
                (mItemWidth - it.measuredWidth) / 2 + it.measuredWidth,
                it.measuredHeight / 2
            )
        }
        mConcaveView.let {
            it.layout(0, 0, it.measuredWidth, it.measuredHeight)
        }
    }

    override fun addView(child: View?) {
        // do nothing
    }

    override fun setBackgroundTintList(tint: ColorStateList?) {
        mConcaveView.backgroundTintList = tint
    }

    /**
     * item click
     */
    override fun onClick(p0: View?) {
        val position = mItems.indexOfFirst { it === p0 }
        if (isAnimatorStarting || mCurrentIndex == position || position == -1) {
            return
        }
        mAnimatedImageItem.run {
            // 位移动画
            val translationAnimator = ValueAnimator.ofObject(
                TranslationEvaluator(this@AnimatedNavigation.measuredHeight.toFloat()),
                translationX to translationY,
                mItemWidth * position.toFloat() to translationY
            ).apply {
                addUpdateListener {
                    val translation = it.animatedValue as Pair<*, *>
                    translationX = translation.first as Float
                    translationY = translation.second as Float

                    // 根据translationX的值，去设置item的透明度
                    mItems.forEach { imageTextItem ->
                        val d = abs(mItemWidth / 2f + translationX - (imageTextItem.left + imageTextItem.right) / 2f)
                        imageTextItem.alpha = when {
                            d <= mItemWidth * 0.75f -> 0f
                            d >= mItemWidth -> 1f
                            else -> (d - mItemWidth * 0.75f) / (mItemWidth * (1.0f - 0.75f))
                        }
                    }

                    // 更新凹陷曲线
                    mConcaveView.setBackground(
                        translationY / this@AnimatedNavigation.measuredHeight,
                        translationX + (left + right) / 2f
                    )
                }
            }

            // icon更新动画
            val iconUpdateAnimator = ValueAnimator().apply {
                setFloatValues(0f, 1f)
                var lastAnimatedFraction = 0f
                addUpdateListener {
                    val animatedFraction = it.animatedFraction
                    if (lastAnimatedFraction < 0.5f && animatedFraction >= 0.5f) {
                        loadData(mItems[position].getIconResource(), mItems[position].getSelectedColorTint())
                    }
                    lastAnimatedFraction = animatedFraction
                }
            }

            // 旋转动画
            val rotationAnimator = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("rotation", 360f))
            mAnimatorSet.apply {
                duration = DEFAULT_ANIMATED_DURATION
                interpolator = LinearInterpolator()
                play(translationAnimator).with(iconUpdateAnimator).before(rotationAnimator)
                removeAllListeners()
                addListener(
                    onStart = {
                        isAnimatorStarting = true
                        // 设置translationZ使ConcaveView覆盖AnimatedImageItem
                        translationZ = mConcaveView.elevation + mConcaveView.translationZ - elevation - 1
                    },
                    onEnd = {
                        isAnimatorStarting = false
                        mCurrentIndex = position
                        // 设置translationZ使AnimatedImageItem层级高于ImageTextItem，防止点击事件被ImageTextItem覆盖
                        translationZ = mConcaveView.elevation + mConcaveView.translationZ + 2
                        // 因为位置改变了，所以需要重新设置触摸事件代理
                        setTouchDelegate()
                    },
                    onCancel = {
                        translationX = 0f
                        translationY = 0f
                        rotation = 0f
                    }
                )
                start()
            }
        }
        mOnItemClickListener?.invoke(p0 as ImageTextItem, position)
    }

    /**
     * not to clip children to their bounds
     */
    private fun ViewGroup?.disableClipChildren() = run {
        var parentView: ViewGroup? = this
        while (parentView != null) {
            parentView.clipChildren = false
            parentView = (parentView.parent as? ViewGroup)
        }
    }

    /**
     * Set items
     */
    fun setImageTextItems(items: List<ImageTextItem>) {
        if (items.isEmpty()) {
            return
        }
        removeAllViews()
        mItems.clear()
        mItems.addAll(items)
        mConcaveView.let { super.addView(it) }
        items.forEachIndexed { index, imageTextItem ->
            imageTextItem.let {
                super.addView(it)
                // 设置translationZ使ImageTextItem层级高于ConcaveView，防止被ConcaveView覆盖
                it.translationZ = mConcaveView.elevation + mConcaveView.translationZ + 1
                it.setOnClickListener(this)
            }
            if (index == 0) {
                imageTextItem.alpha = 0f
                mAnimatedImageItem.loadData(
                    imageTextItem.getIconResource(),
                    imageTextItem.getSelectedColorTint()
                ).also {
                    super.addView(it)
                    // not to clip children to their bounds
                    disableClipChildren()
                    // 设置translationZ使AnimatedImageItem层级高于ImageTextItem，防止点击事件被ImageTextItem覆盖
                    it.translationZ = mConcaveView.elevation + mConcaveView.translationZ + 2
                    // 设置触摸事件代理
                    it.setTouchDelegate()
                }
            }
        }
        requestLayout()
    }

    /**
     * Get current index
     */
    fun getCurrentIndex() = mCurrentIndex

    /**
     * Set animated item size
     * @param size 大小, dp value
     */
    fun setAnimatedItemSize(size: Float) {
        mAnimatedImageItem.setItemSize(size)
    }

    /**
     * Set animated item content padding
     * @param size 大小, dp value
     */
    fun setAnimatedItemContentPadding(size: Float) = apply {
        mAnimatedImageItem.setContentPadding(size)
    }

    /**
     * Set animated item double click
     * @param validDuration 有效点击时间
     */
    fun setOnAnimatedItemDoubleClickListener(validDuration: Long = 1_000, block: (Int) -> Unit) = apply {
        mAnimatedImageItem.setOnDoubleClickListener(validDuration) {
            if (!isAnimatorStarting) {
                block(getCurrentIndex())
            }
        }
    }

    /**
     * Set item click
     */
    fun setOnItemClickListener(listener: OnItemClickListener) = apply {
        mOnItemClickListener = listener
    }
}