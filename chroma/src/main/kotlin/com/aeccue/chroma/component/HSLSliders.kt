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

@Composable
internal fun HSLSliders(
    hsb: HSBState,
    onHueChange: (Hue) -> Unit,
    onSaturationBrightnessChange: (SaturationB, Brightness) -> Unit
) {
    val hsl = rememberHSL(hsb)

    FloatSlider(
        title = "H",
        value = hsl.hue.value,
        range = 0f..360f,
        maxDigits = 3
    ) { newHue ->
        onHueChange(Hue(newHue))
    }

    FloatSlider(
        title = "S",
        value = hsl.saturation.value * 100,
        range = 0f..100f,
        maxDigits = 3
    ) { newSaturation ->
        val s = SaturationL(newSaturation / 100f)
        val brightness = hsl.lightness.toBrightness(s)
        val saturationB = SaturationB(b = brightness, l = hsl.lightness)
        onSaturationBrightnessChange(saturationB, brightness)
    }

    FloatSlider(
        title = "L",
        value = hsl.lightness.value * 100,
        range = 0f..100f,
        maxDigits = 3
    ) { newLightness ->
        val l = Lightness(newLightness / 100f)
        val brightness = l.toBrightness(hsl.saturation)
        val saturationB = SaturationB(b = brightness, l = l)
        onSaturationBrightnessChange(saturationB, brightness)
    }
}
