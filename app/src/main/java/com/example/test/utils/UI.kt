package com.example.test.utils

import android.animation.*
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.test.activity.ApplicationWrapper

fun View.toggleVisibility(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun TextView.setDrawableTop(drawable: Drawable) {
    this.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
}

fun TextView.setDrawableLeft(drawable: Drawable?) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.setDrawableLeft(drawableRes: Int) {
    val drawable = ContextCompat.getDrawable(this.context, drawableRes)
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.setDrawableRight(drawableRes: Int?) {
    var drawable: Drawable? = null
    if (drawableRes != null) {
        drawable = ContextCompat.getDrawable(this.context, drawableRes)
    }
    this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun TextView.setDrawableRight(drawable: Drawable) {
    this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun parseColor(color: String): Int {
    return Color.parseColor(color)
}

fun View.setPaddingBottom(paddingBottom: Int) {
    this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingTop(paddingTop: Int) {
    this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingLeft(paddingLeft: Int) {
    this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingRight(paddingRight: Int) {
    this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun View.setMarginTop(topMargin: Int) {
    val param = this.layoutParams as ViewGroup.MarginLayoutParams
    param.topMargin = topMargin
    layoutParams = param
}

fun View.setMarginBottom(bottomMargin: Int) {
    val param = this.layoutParams as ViewGroup.MarginLayoutParams
    param.bottomMargin = bottomMargin
    layoutParams = param
}

fun View.setMarginLeft(leftMargin: Int) {
    val param = this.layoutParams as ViewGroup.MarginLayoutParams
    param.leftMargin = leftMargin
    layoutParams = param
}

fun View.setMarginEnd(endMargin: Int) {
    val param = this.layoutParams as ViewGroup.MarginLayoutParams
    param.marginEnd = endMargin
    layoutParams = param
}

fun View.setMarginStart(startMargin: Int) {
    val param = this.layoutParams as ViewGroup.MarginLayoutParams
    param.marginStart = startMargin
    layoutParams = param
}

fun View.setMarginRight(rightMargin: Int) {
    val param = this.layoutParams as ViewGroup.MarginLayoutParams
    param.rightMargin = rightMargin
    layoutParams = param
}

fun Context.getDimension(res: Int): Int {
    return this.resources.getDimension(res).toInt()
}

fun Context.getFragmentActivity(): FragmentActivity? {
    var context: Context? = this
    var flag = 20
    while (context is ContextWrapper && flag-- > 0) {
        if (context is FragmentActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun alphaGone(view: View, duration: Long): ObjectAnimator? {
    if (view.visibility == View.GONE || !view.isEnabled) {
        return null
    }
    view.isEnabled = false
    val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1F, 0F)
    alpha.duration = duration
    alpha.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.visibility = View.GONE
            view.isEnabled = true
        }
    })
    alpha.start()
    return alpha
}

fun alphaVisible(view: View, duration: Long): ObjectAnimator? {
    if (view.visibility == View.VISIBLE) {
        return null
    }
    view.visibility = View.VISIBLE
    return alpha(view, 0F, 1F, duration)
}

fun alpha(view: View, start: Float, end: Float, duration: Long): ObjectAnimator {
    val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, start, end)
    alpha.duration = duration
    alpha.start()
    return alpha
}


fun buildScaleBreathAnimation(
    view: View,
    duration: Long = 2000,
    scaleX: Float = 0.85F,
    scaleY: Float = 0.9F
): AnimatorSet {
    val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", scaleX, 1F, scaleX)
    val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", scaleY, 1F, scaleY)
    scaleXAnimator.repeatCount = ValueAnimator.INFINITE
    scaleYAnimator.repeatCount = ValueAnimator.INFINITE
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
    animatorSet.duration = duration
    animatorSet.interpolator = AccelerateDecelerateInterpolator()
    return animatorSet
}

/**
 * 根据限制条件，截取两个字符串可显示最大长度
 * @param nameFirst 目标字符串1
 * @param nameSecond 目标字符串2
 * @param paint 用于测量宽度的paint
 * @param min 单个字符串最小显示长度，低于该长度不再截短，传0表示均分
 * @param limitWidth 可用最大长度
 * @param extraWidth 额外增加的长度，如两个名字间需要插入图片，则表示该图片宽度
 * @param replaceWidth 如截取后需要在字符串后增加后缀，表示该后缀长度，如添加省略号
 */
fun performClipText(
    nameFirst: String,
    nameSecond: String,
    paint: Paint,
    min: Int,
    limitWidth: Float,
    extraWidth: Float,
    replaceWidth: Float
): IntArray {
    val longger: String
    val shotter: String
    val measuredWidth = paint.measureText(nameFirst) + paint.measureText(nameSecond) + extraWidth
    if (measuredWidth < limitWidth) {
        return intArrayOf(nameFirst.length, nameSecond.length)
    }
    val beginAtFirst = nameFirst.length > nameSecond.length
    if (beginAtFirst) {
        longger = nameFirst
        shotter = nameSecond
    } else {
        longger = nameSecond
        shotter = nameFirst
    }
    val countFirst: Int
    val countSecond: Int
    var limit = limitWidth - replaceWidth
    if (min > 0) {
        val resFirst = performClipText(longger, min, paint, limit, measuredWidth, false)
        val clipedWidth = resFirst[1]
        countFirst = resFirst[0].toInt()
        countSecond = if (clipedWidth > limit) {
            if (measuredWidth - clipedWidth > 1) {
                limit -= replaceWidth
            }
            val clipSecond = performClipText(shotter, min, paint, limit, clipedWidth, true)
            clipSecond[0].toInt()
        } else {
            shotter.length
        }
    } else {
        val result = performClipText(longger, shotter, paint, limit, measuredWidth, replaceWidth)
        countFirst = result[0]
        countSecond = result[1]
    }
    val countRes = IntArray(2)
    if (beginAtFirst) {
        countRes[0] = countFirst
        countRes[1] = countSecond
    } else {
        countRes[0] = countSecond
        countRes[1] = countFirst
    }
    return countRes
}

private fun performClipText(
    longger: String,
    shorter: String,
    paint: Paint,
    limit: Float,
    measuredWidth: Float,
    replaceWidth: Float
): IntArray {
    val countRes = IntArray(2)
    val llength = longger.length
    val slength = shorter.length
    val widths = FloatArray(llength)
    paint.getTextWidths(longger, widths)
    var i = llength - 1
    var want = measuredWidth
    var clipCount = 0
    var hit = false
    while (i > 0 && (llength - clipCount >= slength)) {
        want -= widths[i]
        clipCount++
        if (want < limit) {
            hit = true
            break
        }
        i--
    }

    if (hit) {
        countRes[0] = llength - clipCount
        countRes[1] = slength
    } else {
        var lClip = 0
        var sClip = 0
        val swidths = FloatArray(llength)
        paint.getTextWidths(shorter, swidths)
        i = slength - 1
        val adjust = if (measuredWidth - want > 1) {
            limit - replaceWidth
        } else {
            limit
        }
        while (i > 0) {
            want -= swidths[i]
            sClip++
            if (want < adjust) {
                break
            }
            want -= widths[i - 1]
            lClip++
            if (want < adjust) {
                break
            }
            i--
        }
        countRes[0] = llength - clipCount - lClip
        countRes[1] = slength - sClip
    }
    return countRes
}

private fun performClipText(
    longger: String,
    min: Int,
    paint: Paint,
    limit: Float,
    measuredWidth: Float,
    forced: Boolean
): FloatArray {
    val length = longger.length
    val widths = FloatArray(length)
    paint.getTextWidths(longger, widths)
    var clipCount = 0
    var want = measuredWidth
    //left one char at least
    var i = length - 1
    while (i > 0 && (length - clipCount > min || forced)) {
        want -= widths[i]
        clipCount++
        if (want < limit) {
            break
        }
        i--
    }

    return floatArrayOf((length - clipCount).toFloat(), want)
}

const val scaleDuration: Long = 50
fun View.pressStateScale(scaleFactor: Float = 0.96f, duration: Long = scaleDuration) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                animate().setDuration(duration).scaleX(scaleFactor).scaleY(scaleFactor).start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                animate().setDuration(duration).scaleX(1f).scaleY(1f).start()
            }
            else -> {
            }
        }
        false
    }
}

fun View.pressStateScaleAndAlpha(scaleFactor: Float = 0.94f, alphaFactor: Float = 0.8f, duration: Long = scaleDuration) {
    val originAlpha = alpha.takeIf { it != 0F } ?: 1F
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                animate().setDuration(duration).scaleX(scaleFactor).scaleY(scaleFactor)
                    .alpha(originAlpha * alphaFactor).start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                animate().setDuration(duration).scaleX(1f).scaleY(1f).alpha(originAlpha)
                    .start()
            }
            else -> {
            }
        }
        false
    }
}

fun View.clearPressStateScaleAndAlpha() {
    setOnTouchListener { _, _ ->
        false
    }
}

fun View.pressAlpha(alpha: Float = 0.8f) {
    pressStateScaleAndAlpha(1f, alpha)
}

class EllipsizeText(
    val fix: Boolean, var text: String, var width: Int = 0, var showSuffix: Boolean = false
)


val appResource: Resources
    get() = ApplicationWrapper.instance.resources
val appDisplayMetrics: DisplayMetrics
    get() = appResource.displayMetrics
fun Int.dp(): Int = this.toFloat().dp()
fun Int.dpF(): Float = this.toFloat().dpF()

fun Float.dp(): Int = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, appDisplayMetrics) + 0.5f).toInt()
fun Float.dpF(): Float = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, appDisplayMetrics))

fun Int.dimenToPx(): Int = appResource.getDimensionPixelSize(this)
fun Int.spToPx(): Int = (this * appDisplayMetrics.scaledDensity + 0.5f).toInt()

fun Int.getColor(): Int = ContextCompat.getColor(ApplicationWrapper.instance, this)