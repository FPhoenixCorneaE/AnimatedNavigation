package com.fphoenixcorneae.navigation

import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup

/**
 * @desc：ViewGroupUtils
 * @date：2022/08/23 16:54
 */
object ViewGroupUtils {
    private val sMatrix: ThreadLocal<Matrix?> = ThreadLocal<Matrix?>()
    private val sRectF = ThreadLocal<RectF>()

    /**
     * Retrieve the transformed bounding rect of an arbitrary descendant view.
     * This does not need to be a direct child.
     *
     * @param descendant descendant view to reference
     * @param out rect to set to the bounds of the descendant view
     */
    fun getDescendantRect(parent: ViewGroup?, descendant: View, out: Rect) {
        out.set(0, 0, descendant.width, descendant.height)
        offsetDescendantRect(parent, descendant, out)
    }

    /**
     * This is a port of the common
     * [ViewGroup.offsetDescendantRectToMyCoords]
     * from the framework, but adapted to take transformations into account. The result
     * will be the bounding rect of the real transformed rect.
     *
     * @param descendant view defining the original coordinate system of rect
     * @param rect (in/out) the rect to offset from descendant to this view's coordinate system
     */
    fun offsetDescendantRect(parent: ViewGroup?, descendant: View, rect: Rect) {
        var m: Matrix? = sMatrix.get()
        if (m == null) {
            m = Matrix()
            sMatrix.set(m)
        } else {
            m.reset()
        }
        offsetDescendantMatrix(parent, descendant, m)
        var rectF = sRectF.get()
        if (rectF == null) {
            rectF = RectF()
            sRectF.set(rectF)
        }
        rectF.set(rect)
        m.mapRect(rectF)
        rect.set((rectF.left + 0.5f).toInt(),
            (rectF.top + 0.5f).toInt(),
            (rectF.right + 0.5f).toInt(),
            (rectF.bottom + 0.5f).toInt())
    }

    private fun offsetDescendantMatrix(target: ViewGroup?, view: View, m: Matrix) {
        val parent = view.parent
        if (parent is View && parent !== target) {
            val vp = parent as View
            offsetDescendantMatrix(target, vp, m)
            m.preTranslate(-vp.scrollX.toFloat(), -vp.scrollY.toFloat())
        }
        m.preTranslate(view.left.toFloat(), view.top.toFloat())
        if (!view.matrix.isIdentity) {
            m.preConcat(view.matrix)
        }
    }
}