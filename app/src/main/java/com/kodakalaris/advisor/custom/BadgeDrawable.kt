package com.kodakalaris.advisor.custom

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import androidx.annotation.IntDef
import androidx.annotation.NonNull

class BadgeDrawable private constructor(config: Config) :
    Drawable() {
    @IntDef(*[TYPE_NUMBER, TYPE_ONLY_ONE_TEXT, TYPE_WITH_TWO_TEXT, TYPE_WITH_TWO_TEXT_COMPLEMENTARY])
    annotation class BadgeType

    private class Config {
        var badgeType = TYPE_NUMBER
        var number = 0
        var text1 = ""
        var text2 = ""
        var textSize = spToPixels(4f)
        var badgeColor = -0x130000
        var textColor = -0x1
        var typeface = Typeface.DEFAULT
        var cornerRadius = dipToPixels(2f)
        var paddingLeft = dipToPixels(2f)
        var paddingTop = dipToPixels(2f)
        var paddingRight = dipToPixels(2f)
        var paddingBottom = dipToPixels(2f)
        var paddingCenter = dipToPixels(3f)
        var storkeWidth = dipToPixels(1f).toInt()
    }

    private val config: Config
    private val backgroundDrawable: ShapeDrawable
    private val backgroundDrawableOfText2: ShapeDrawable
    private val backgroundDrawableOfText1: ShapeDrawable
    private var badgeWidth = 0
    private var badgeHeight = 0
    private val outerR = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val outerROfText1 = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val outerROfText2 = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val paint: Paint
    private var fontMetrics: Paint.FontMetrics? = null
    private var text1Width = 0
    private var text2Width = 0

    class Builder {
        private val config: Config
        fun type(@BadgeType type: Int): Builder {
            config.badgeType = type
            return this
        }

        fun number(number: Int): Builder {
            config.number = number
            return this
        }

        fun text1(text1: String): Builder {
            config.text1 = text1
            return this
        }

        fun text2(text2: String): Builder {
            config.text2 = text2
            return this
        }

        fun textSize(size: Float): Builder {
            config.textSize = size
            return this
        }

        fun badgeColor(color: Int): Builder {
            config.badgeColor = color
            return this
        }

        fun textColor(color: Int): Builder {
            config.textColor = color
            return this
        }

        fun typeFace(typeface: Typeface): Builder {
            config.typeface = typeface
            return this
        }

        fun cornerRadius(radius: Float): Builder {
            config.cornerRadius = radius
            return this
        }

        fun padding(l: Float, t: Float, r: Float, b: Float, c: Float): Builder {
            config.paddingLeft = l
            config.paddingTop = t
            config.paddingRight = r
            config.paddingBottom = b
            config.paddingCenter = c
            return this
        }

        fun strokeWidth(width: Int): Builder {
            config.storkeWidth = width
            return this
        }

        fun build(): BadgeDrawable {
            return BadgeDrawable(config)
        }

        init {
            config = Config()
        }
    }

    fun setBadgeType(@BadgeType type: Int) {
        config.badgeType = type
        measureBadge()
    }

    var number: Int
        get() = config.number
        set(number) {
            config.number = number
        }

    var text1: String
        get() = config.text1
        set(text1) {
            config.text1 = text1
            measureBadge()
        }

    var text2: String
        get() = config.text2
        set(text2) {
            config.text2 = text2
            measureBadge()
        }

    var textSize: Float
        get() = config.textSize
        private set(textSize) {
            config.textSize = textSize
            paint.textSize = textSize
            fontMetrics = paint.fontMetrics
            measureBadge()
        }

    var badgeColor: Int
        get() = config.badgeColor
        set(color) {
            config.badgeColor = color
        }

    var textColor: Int
        get() = config.textColor
        set(color) {
            config.textColor = color
        }

    private fun setCornerRadius(radius: Float) {
        config.cornerRadius = radius
        outerR[7] = radius
        outerR[6] = outerR[7]
        outerR[5] = outerR[6]
        outerR[4] = outerR[5]
        outerR[3] = outerR[4]
        outerR[2] = outerR[3]
        outerR[1] = outerR[2]
        outerR[0] = outerR[1]
        outerROfText1[7] = radius
        outerROfText1[6] = outerROfText1[7]
        outerROfText1[1] = outerROfText1[6]
        outerROfText1[0] = outerROfText1[1]
        outerROfText1[5] = 0f
        outerROfText1[4] = outerROfText1[5]
        outerROfText1[3] = outerROfText1[4]
        outerROfText1[2] = outerROfText1[3]
        outerROfText2[7] = 0f
        outerROfText2[6] = outerROfText2[7]
        outerROfText2[1] = outerROfText2[6]
        outerROfText2[0] = outerROfText2[1]
        outerROfText2[5] = radius
        outerROfText2[4] = outerROfText2[5]
        outerROfText2[3] = outerROfText2[4]
        outerROfText2[2] = outerROfText2[3]
    }

    fun setPadding(l: Float, t: Float, r: Float, b: Float, c: Float) {
        config.paddingLeft = l
        config.paddingTop = t
        config.paddingRight = r
        config.paddingBottom = b
        config.paddingCenter = c
        measureBadge()
    }

    fun setStrokeWidth(width: Int) {
        config.storkeWidth = width
    }

    private fun measureBadge() {
        badgeHeight = (config.textSize + config.paddingTop + config.paddingBottom).toInt()
        when (config.badgeType) {
            TYPE_ONLY_ONE_TEXT -> {
                text1Width = paint.measureText(config.text1).toInt()
                badgeWidth = (text1Width + config.paddingLeft + config.paddingRight).toInt()
                setCornerRadius(config.cornerRadius)
            }
            TYPE_WITH_TWO_TEXT -> {
                text1Width = paint.measureText(config.text1).toInt()
                text2Width = paint.measureText(config.text2).toInt()
                badgeWidth =
                    (text1Width + text2Width + config.paddingLeft + config.paddingRight + config.paddingCenter).toInt()
                setCornerRadius(config.cornerRadius)
            }
            TYPE_WITH_TWO_TEXT_COMPLEMENTARY -> {
                text1Width = paint.measureText(config.text1).toInt()
                text2Width = paint.measureText(config.text2).toInt()
                badgeWidth =
                    (text1Width + text2Width + config.paddingLeft + config.paddingRight + config.paddingCenter).toInt()
                setCornerRadius(config.cornerRadius)
            }
            else -> {
                badgeWidth = (config.textSize + config.paddingLeft + config.paddingRight).toInt()
                setCornerRadius(badgeHeight.toFloat())
            }
        }
        val boundsWidth = bounds.width()
        if (boundsWidth > 0) { // If the bounds has been set, adjust the badge size
            when (config.badgeType) {
                TYPE_ONLY_ONE_TEXT -> if (boundsWidth < badgeWidth) {
                    text1Width = (boundsWidth - config.paddingLeft - config.paddingRight).toInt()
                    text1Width = if (text1Width > 0) text1Width else 0
                    badgeWidth = boundsWidth
                }
                TYPE_WITH_TWO_TEXT, TYPE_WITH_TWO_TEXT_COMPLEMENTARY -> if (boundsWidth < badgeWidth) {
                    if (boundsWidth < text1Width + config.paddingLeft + config.paddingRight) {
                        text1Width =
                            (boundsWidth - config.paddingLeft - config.paddingRight).toInt()
                        text1Width = if (text1Width > 0) text1Width else 0
                        text2Width = 0
                    } else {
                        text2Width =
                            (boundsWidth - text1Width - config.paddingLeft - config.paddingRight - config.paddingCenter).toInt()
                        text2Width = if (text2Width > 0) text2Width else 0
                    }
                    badgeWidth = boundsWidth
                }
            }
        }
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        measureBadge()
    }

    override fun draw(@NonNull canvas: Canvas) {
        val bounds = bounds
        val marginTopAndBottom = ((bounds.height() - badgeHeight) / 2f).toInt()
        val marginLeftAndRight = ((bounds.width() - badgeWidth) / 2f).toInt()
        backgroundDrawable.setBounds(
            bounds.left + marginLeftAndRight,
            bounds.top + marginTopAndBottom,
            bounds.right - marginLeftAndRight,
            bounds.bottom - marginTopAndBottom
        )
        backgroundDrawable.paint.color = config.badgeColor
        backgroundDrawable.draw(canvas)
        val textCx = bounds.centerX().toFloat()
        val textCy = bounds.centerY() - (fontMetrics!!.bottom + fontMetrics!!.top) / 2f
        when (config.badgeType) {
            TYPE_ONLY_ONE_TEXT -> {
                paint.color = config.textColor
                canvas.drawText(
                    cutText(config.text1, text1Width),
                    textCx,
                    textCy,
                    paint
                )
            }
            TYPE_WITH_TWO_TEXT_COMPLEMENTARY -> {
                paint.color = config.textColor
                canvas.drawText(
                    config.text1,
                    marginLeftAndRight + config.paddingLeft + text1Width / 2f,
                    textCy,
                    paint
                )
                backgroundDrawableOfText2.setBounds(
                    (bounds.left + marginLeftAndRight + config.paddingLeft +
                            text1Width + config.paddingCenter / 2f).toInt(),
                    bounds.top + marginTopAndBottom + config.storkeWidth,
                    bounds.width() - marginLeftAndRight - config.storkeWidth,
                    bounds.bottom - marginTopAndBottom - config.storkeWidth
                )
                backgroundDrawableOfText2.paint.color = config.textColor
                backgroundDrawableOfText2.draw(canvas)
                paint.color = config.badgeColor
                canvas.drawText(
                    cutText(config.text2, text2Width),
                    bounds.width() - marginLeftAndRight - config.paddingRight - text2Width / 2f,
                    textCy,
                    paint
                )
            }
            TYPE_WITH_TWO_TEXT -> {
                backgroundDrawableOfText1.setBounds(
                    bounds.left + marginLeftAndRight + config.storkeWidth,
                    bounds.top + marginTopAndBottom + config.storkeWidth,
                    (bounds.left + marginLeftAndRight + config.paddingLeft +
                            text1Width + config.paddingCenter / 2f - config.storkeWidth / 2f).toInt(),
                    bounds.bottom - marginTopAndBottom - config.storkeWidth
                )
                backgroundDrawableOfText1.paint.color = -0x1
                backgroundDrawableOfText1.draw(canvas)
                paint.color = config.badgeColor
                canvas.drawText(
                    config.text1,
                    text1Width / 2f + marginLeftAndRight + config.paddingLeft,
                    textCy,
                    paint
                )
                backgroundDrawableOfText2.setBounds(
                    (bounds.left + marginLeftAndRight + config.paddingLeft +
                            text1Width + config.paddingCenter / 2f + config.storkeWidth / 2f).toInt(),
                    bounds.top + marginTopAndBottom + config.storkeWidth,
                    bounds.width() - marginLeftAndRight - config.storkeWidth,
                    bounds.bottom - marginTopAndBottom - config.storkeWidth
                )
                backgroundDrawableOfText2.paint.color = -0x1
                backgroundDrawableOfText2.draw(canvas)
                paint.color = config.badgeColor
                canvas.drawText(
                    cutText(config.text2, text2Width),
                    bounds.width() - marginLeftAndRight - config.paddingRight - text2Width / 2f,
                    textCy,
                    paint
                )
            }
            else -> {
                paint.color = config.textColor
                canvas.drawText(
                    cutNumber(config.number, badgeWidth),
                    textCx,
                    textCy,
                    paint
                )
            }
        }
    }

    override fun getIntrinsicWidth(): Int {
        return badgeWidth
    }

    override fun getIntrinsicHeight(): Int {
        return badgeHeight
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    private fun cutNumber(number: Int, width: Int): String {
        val text = number.toString()
        return if (paint.measureText(text) < width) text else "…"
    }

    private fun cutText(text: String, width: Int): String {
        var text = text
        if (paint.measureText(text) <= width) return text
        var suffix = "..."
        while (paint.measureText(text + suffix) > width) {
            if (text.length > 0) text = text.substring(0, text.length - 1)
            if (text.length == 0) {
                suffix = suffix.substring(0, suffix.length - 1)
                if (suffix.length == 0) break
            }
        }
        return text + suffix
    }

    fun toSpannable(): SpannableString {
        val spanStr = SpannableString(" ")
        spanStr.setSpan(
            ImageSpan(this, ImageSpan.ALIGN_BOTTOM),
            0,
            1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        return spanStr
    }

    companion object {
        const val TYPE_NUMBER = 1
        private const val TYPE_ONLY_ONE_TEXT = 1 shl 1
        private const val TYPE_WITH_TWO_TEXT = 1 shl 2
        private const val TYPE_WITH_TWO_TEXT_COMPLEMENTARY = 1 shl 3
        private fun dipToPixels(dipValue: Float): Float {
            val scale =
                Resources.getSystem().displayMetrics.density
            return dipValue * scale + 0.5f
        }

        private fun spToPixels(spValue: Float): Float {
            val fontScale =
                Resources.getSystem().displayMetrics.scaledDensity
            return spValue * fontScale + 0.5f
        }
    }

    init {
        paint = Paint()
        paint.isAntiAlias = true
        paint.typeface = config.typeface
        paint.textAlign = Paint.Align.CENTER
        paint.style = Paint.Style.FILL
        paint.alpha = 255
        this.config = config
        setCornerRadius(config.cornerRadius)
        var shape = RoundRectShape(outerR, null, null)
        backgroundDrawable = ShapeDrawable(shape)
        shape = RoundRectShape(outerROfText1, null, null)
        backgroundDrawableOfText1 = ShapeDrawable(shape)
        shape = RoundRectShape(outerROfText2, null, null)
        backgroundDrawableOfText2 = ShapeDrawable(shape)
        textSize = config.textSize
        measureBadge()
    }
}