package com.kodakalaris.advisor.custom

import android.R
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CounterFab @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FloatingActionButton(context!!, attrs, defStyleAttr) {
    private val ANIMATION_PROPERTY: Property<CounterFab?, Float> =
        object : Property<CounterFab?, Float>(
            Float::class.java, "animation"
        ) {
            override fun set(`object`: CounterFab?, value: Float) {
                mAnimationFactor = value
                postInvalidateOnAnimation()
            }

            override fun get(`object`: CounterFab?): Float {
                return 0f
            }
        }
    private val mContentBounds: Rect
    private val mTextPaint: Paint
    private val mTextSize: Float
    private val mCirclePaint: Paint
    private var mCircleBounds: Rect? = null
    private val mMaskPaint: Paint
    private val mAnimationDuration: Int
    private var mAnimationFactor: Float
    private var mCount = 0
    private var mText: String? = null
    private val mTextHeight: Float
    private var mAnimator: ObjectAnimator? = null
    private val isSizeMini: Boolean
        get() = super.getSize() === com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_MINI

    /**
     * @return The current count value
     */
    /**
     * Set the count to show on badge
     *
     * @param count The count value starting from 0
     */
    var count: Int
        get() = mCount
        set(count) {
            if (count == mCount) return
            mCount = if (count > 0) count else 0
            onCountChanged()
            if (ViewCompat.isLaidOut(this)) {
                startAnimation()
            }
        }

    /**
     * Increase the current count value by 1
     */
    fun increase() {
        count = mCount + 1
    }

    /**
     * Decrease the current count value by 1
     */
    fun decrease() {
        count = if (mCount > 0) mCount - 1 else 0
    }

    private fun onCountChanged() {
        mText = if (isSizeMini) {
            if (mCount > MINI_MAX_COUNT) {
                MINI_MAX_COUNT_TEXT
            } else {
                mCount.toString()
            }
        } else {
            if (mCount > NORMAL_MAX_COUNT) {
                NORMAL_MAX_COUNT_TEXT
            } else {
                mCount.toString()
            }
        }
    }

    private fun startAnimation() {
        var start = 0f
        var end = 1f
        if (mCount == 0) {
            start = 1f
            end = 0f
        }
        if (isAnimating) {
            mAnimator!!.cancel()
        }
        mAnimator = ObjectAnimator.ofObject(this, ANIMATION_PROPERTY, null, start, end)
        mAnimator!!.interpolator = ANIMATION_INTERPOLATOR
        mAnimator!!.duration = mAnimationDuration.toLong()
        mAnimator!!.start()
    }

    private val isAnimating: Boolean
        private get() = mAnimator != null && mAnimator!!.isRunning

    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mCount > 0 || isAnimating) {
            if (getContentRect(mContentBounds)) {
                mCircleBounds!!.offsetTo(
                    mContentBounds.left + mContentBounds.width() - mCircleBounds!!.width(),
                    mContentBounds.top
                )
            }
            val cx = mCircleBounds!!.centerX().toFloat()
            val cy = mCircleBounds!!.centerY().toFloat()
            val radius = mCircleBounds!!.width() / 2f * mAnimationFactor
            // Solid circle
            canvas.drawCircle(cx, cy, radius, mCirclePaint)
            // Mask circle
            canvas.drawCircle(cx, cy, radius, mMaskPaint)
            // Count text
            mTextPaint.textSize = mTextSize * mAnimationFactor
            canvas.drawText(mText!!, cx, cy + mTextHeight / 2f, mTextPaint)
        }
    }

    private class SavedState : View.BaseSavedState {
        var count = 0

        /**
         * Constructor called from [CounterFab.onSaveInstanceState]
         */
        constructor(superState: Parcelable) : super(superState) {}

        /**
         * Constructor called from [.CREATOR]
         */
        private constructor(`in`: Parcel) : super(`in`) {
            count = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(count)
        }

        override fun toString(): String {
            return (CounterFab::class.java.simpleName + "." + SavedState::class.java.simpleName + "{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " count=" + count + "}")
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState?> =
                object : Parcelable.Creator<SavedState?> {
                    override fun createFromParcel(`in`: Parcel): SavedState? {
                        return SavedState(`in`)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState: Parcelable = super.onSaveInstanceState()!!
        val ss = SavedState(superState)
        ss.count = mCount
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        count = ss.count
        requestLayout()
    }

    companion object {
        private const val NORMAL_MAX_COUNT = 99
        private const val NORMAL_MAX_COUNT_TEXT = "99+"
        private const val MINI_MAX_COUNT = 9
        private const val MINI_MAX_COUNT_TEXT = "9+"
        private const val TEXT_SIZE_DP = 11
        private const val TEXT_PADDING_DP = 2
        private val MASK_COLOR =
            Color.parseColor("#ed0000") // Translucent black as mask color
        private val ANIMATION_INTERPOLATOR: Interpolator =
            OvershootInterpolator()
    }

    init {
        setUseCompatPadding(true)
        val density: Float = getResources().getDisplayMetrics().density
        mTextSize = TEXT_SIZE_DP * density
        val textPadding = TEXT_PADDING_DP * density
        mAnimationDuration = getResources().getInteger(R.integer.config_shortAnimTime)
        mAnimationFactor = 1f
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.style = Paint.Style.STROKE
        mTextPaint.color = Color.WHITE
        mTextPaint.textSize = mTextSize
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.typeface = Typeface.SANS_SERIF
        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePaint.style = Paint.Style.FILL
        val colorStateList: ColorStateList? = getBackgroundTintList()
        if (colorStateList != null) {
            mCirclePaint.color = colorStateList.defaultColor
        } else {
            val background: Drawable = getBackground()
            if (background is ColorDrawable) {
                mCirclePaint.color = background.color
            }
        }
        mMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMaskPaint.style = Paint.Style.FILL
        mMaskPaint.color = MASK_COLOR
        val textBounds = Rect()
        mTextPaint.getTextBounds(
            NORMAL_MAX_COUNT_TEXT,
            0,
            NORMAL_MAX_COUNT_TEXT.length,
            textBounds
        )
        mTextHeight = textBounds.height().toFloat()
        val textWidth =
            mTextPaint.measureText(NORMAL_MAX_COUNT_TEXT)
        val circleRadius =
            Math.max(textWidth, mTextHeight) / 2f + textPadding
        val circleEnd = (circleRadius * 2).toInt()
        mCircleBounds = if (isSizeMini) {
            val circleStart = (circleRadius / 2).toInt()
            Rect(circleStart, circleStart, circleEnd, circleEnd)
        } else {
            val circleStart = 0
            Rect(
                circleStart,
                circleStart,
                (circleRadius * 2).toInt(),
                (circleRadius * 2).toInt()
            )
        }
        mContentBounds = Rect()
        onCountChanged()
    }
}