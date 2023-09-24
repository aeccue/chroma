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
import androidx.compose.runtime.getValue

@Composable
internal fun RGBSliders(
    hsb: HSB,
    onChange: (RGB) -> Unit
) {
    val rgb by rememberRGB(hsb = hsb)
    IntSlider(
        title = "R",
        value = rgb.red.value,
        range = 0f..255f,
        maxDigits = 3
    ) { newRed ->
        onChange(rgb.copy(red = Red(newRed)))
    }

    IntSlider(
        title = "G",
        value = rgb.green.value,
        range = 0f..255f,
        maxDigits = 3
    ) { newGreen ->
        onChange(rgb.copy(green = Green(newGreen)))
    }

    IntSlider(
        title = "B",
        value = rgb.blue.value,
        range = 0f..255f,
        maxDigits = 3
    ) { newBlue ->
        onChange(rgb.copy(blue = Blue(newBlue)))
    }
}
