/*
 * Designed and developed by aeccue.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aeccue.chroma.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.toColorInt


internal fun Char.isHex(): Boolean {
    val asciiCode = code
    return when {
        isDigit() -> true
        asciiCode in 65..70 -> true
        asciiCode in 97..102 -> true
        else -> false
    }
}

@JvmInline
internal value class Hue(val value: Float) {

    @Suppress("NOTHING_TO_INLINE")
    inline fun toColor(
        saturation: SaturationL = SaturationL(1f),
        lightness: Lightness = Lightness(0.5f)
    ) = Color(
        ColorUtils.HSLToColor(
            floatArrayOf(
                value,
                saturation.value,
                lightness.value
            )
        )
    )
}

@JvmInline
internal value class SaturationL(val value: Float) {

    constructor(l: Lightness, b: Brightness) : this(
        value = if (l.value == 0f || l.value == 1f) {
            0f
        } else {
            (b.value - l.value) / java.lang.Float.min(l.value, 1f - l.value)
        }
    )
}

@JvmInline
internal value class SaturationB(val value: Float) {

    constructor(b: Brightness, l: Lightness) : this(
        value = if (b.value == 0f) 0f else 2f * (1f - l.value / b.value)
    )
}

@JvmInline
internal value class Lightness(val value: Float) {

    @Suppress("NOTHING_TO_INLINE")
    inline fun toBrightness(saturation: SaturationL): Brightness =
        Brightness(value + saturation.value * java.lang.Float.min(value, 1 - value))
}

@JvmInline
internal value class Brightness(val value: Float) {

    @Suppress("NOTHING_TO_INLINE")
    inline fun toLightness(saturation: SaturationB): Lightness =
        Lightness(value * (1 - saturation.value / 2))
}

@JvmInline
internal value class Red(val value: Int)

@JvmInline
internal value class Green(val value: Int)

@JvmInline
internal value class Blue(val value: Int)


/**
 * @param extendForBlackAndWhite - Whether or not to extend the hue bar to include white on the very
 *                                 left and black on the very right
 */
@Composable
internal fun rememberHSB(
    initialColor: Color,
    extendForBlackAndWhite: Boolean = false
) = remember(initialColor) {
    when {
        // White will have a hue of -5
        extendForBlackAndWhite && initialColor == Color.White ->
            HSBState(Hue(-5f), SaturationB(0f), Brightness(1f))

        // Black will have a hue of 365
        extendForBlackAndWhite && initialColor == Color.Black ->
            HSBState(Hue(365f), SaturationB(0f), Brightness(0f))

        else -> FloatArray(3).let { outHSB ->
            android.graphics.Color.colorToHSV(initialColor.toArgb(), outHSB)
            HSBState(
                initialHue = Hue(outHSB[0]),
                initialSaturation = SaturationB(outHSB[1]),
                initialBrightness = Brightness(outHSB[2])
            )
        }
    }
}

internal interface HSB {

    val hue: Hue
    val saturation: SaturationB
    val brightness: Brightness

    val color: Color
}

internal class HSBState(
    initialHue: Hue,
    initialSaturation: SaturationB,
    initialBrightness: Brightness
) : HSB {

    override var hue by mutableStateOf(initialHue)
    override var saturation by mutableStateOf(initialSaturation)
    override var brightness by mutableStateOf(initialBrightness)

    override val color: Color by derivedStateOf {
        Color.hsv(
            hue = hue.value.coerceIn(0f, 360f),
            saturation = saturation.value,
            value = brightness.value
        )
    }

    fun update(rgb: RGB) {
        update(rgb.color)
    }

    fun update(color: Color) {
        FloatArray(3).let { outHSB ->
            android.graphics.Color.colorToHSV(color.toArgb(), outHSB)
            hue = Hue(outHSB[0])
            saturation = SaturationB(outHSB[1])
            brightness = Brightness(outHSB[2])
        }
    }
}

@Composable
internal fun rememberHSL(hsb: HSB): HSL = remember(hsb) { HSLState(hsb) }

internal interface HSL {

    val hue: Hue
    val saturation: SaturationL
    val lightness: Lightness
}

private class HSLState(hsb: HSB) : HSL {

    override val hue by derivedStateOf { Hue(hsb.hue.value.coerceIn(0f, 360f)) }
    override val lightness by derivedStateOf { hsb.brightness.toLightness(hsb.saturation) }
    override val saturation by derivedStateOf { SaturationL(l = lightness, b = hsb.brightness) }
}

@Composable
internal fun rememberRGB(hsb: HSB): State<RGB> = remember(hsb) {
    derivedStateOf {
        RGB(hsb.color.toArgb())
    }
}

internal data class RGB(
    val red: Red,
    val green: Green,
    val blue: Blue
) {

    constructor(color: Int) : this(
        red = Red(color.red),
        green = Green(color.green),
        blue = Blue(color.blue)
    )
}

@JvmInline
internal value class Hex(val value: String) {

    constructor(color: Color, includeAlpha: Boolean = false) : this(
        value = if (includeAlpha) {
            "%06X".format(color.toArgb())
        } else {
            "%06X".format(color.toArgb() and 0xFFFFFF)
        }
    )

    inline val color: Color
        get() = Color("#$value".toColorInt())

    inline val isComplete: Boolean
        get() = value.length == 6
}

internal inline val Color.lightness: Lightness
    get() = FloatArray(3).let { outHSL ->
        ColorUtils.colorToHSL(toArgb(), outHSL)
        Lightness(outHSL[2])
    }

private inline val RGB.color: Color
    get() = Color(
        red = red.value,
        green = green.value,
        blue = blue.value
    )
