package com.aeccue.chroma.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
internal fun HueSlider(
    hsb: HSB,
    onPick: (Hue, SaturationB, Brightness) -> Unit
) {
    val saturationCache = remember { mutableStateOf(SaturationB(0.35f)) }
    val brightnessCache = remember { mutableStateOf(Brightness(0.65f)) }

    FloatSlider(
        title = null,
        value = hsb.hue.value,
        range = if (LocalChromaPickerStyle.current.hue.includeBlackAndWhite) -5f..365f else 0f..360f,
        keyboardInput = false,
        trackColor = Brush.horizontalGradient(RainbowHues),
        dotColor = LocalChromaPickerStyle.current.hue.dotColor
    ) { newHue ->
        when {
            newHue < 0f -> {
                if (hsb.hue.value >= 0f) {
                    saturationCache.value = hsb.saturation
                    brightnessCache.value = hsb.brightness
                }
                onPick(Hue(newHue), SaturationB(0f), Brightness(1f))
            }

            newHue > 360f -> {
                if (hsb.hue.value <= 360) {
                    saturationCache.value = hsb.saturation
                    brightnessCache.value = hsb.brightness
                }
                onPick(Hue(newHue), SaturationB(0f), Brightness(0f))
            }

            else -> {
                if (hsb.hue.value < 0f || hsb.hue.value > 360f) {
                    onPick(Hue(newHue), saturationCache.value, brightnessCache.value)
                } else {
                    onPick(Hue(newHue), hsb.saturation, hsb.brightness)
                }
            }
        }
    }
}

private val RainbowHues = listOf(
    Color.Red,
    Color.Yellow,
    Color.Green,
    Color.Cyan,
    Color.Blue,
    Color.Magenta,
    Color.Red
)
