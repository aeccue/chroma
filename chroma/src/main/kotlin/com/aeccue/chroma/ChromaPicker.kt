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

package com.aeccue.chroma

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import com.aeccue.chroma.component.ColorSpace
import com.aeccue.chroma.component.ColorSpaceSelector
import com.aeccue.chroma.component.HSBArea
import com.aeccue.chroma.component.HSLSliders
import com.aeccue.chroma.component.HexValueInputField
import com.aeccue.chroma.component.HueSlider
import com.aeccue.chroma.component.LocalChromaPickerStyle
import com.aeccue.chroma.component.LocalContentColorState
import com.aeccue.chroma.component.RGBSliders
import com.aeccue.chroma.component.lightness
import com.aeccue.chroma.component.rememberHSB
import kotlinx.coroutines.flow.collectLatest

/**
 * @param initialColor - The initial color of the color picker
 * @param style - Styling options to control the look of the color picker
 * @param onPick - Callback for whenever the color is changed
 */
@Composable
public fun ChromaPicker(
    modifier: Modifier = Modifier,
    initialColor: Color = Color.White,
    style: ChromaPickerStyle = ChromaPickerStyle(
        textStyles = ChromaPickerStyle.TextStyles(LocalTextStyle.current)
    ),
    onPick: (Color) -> Unit
) {
    // HSB will be the main value that all sliders will track
    val hsb = rememberHSB(
        initialColor = initialColor,
        extendForBlackAndWhite = LocalChromaPickerStyle.current.hue.includeBlackAndWhite
    )

    // To keep track of color of content based on current chosen color
    val contentColorState = remember(hsb) {
        val contentColor =
            if (hsb.color.lightness.value >= 0.5f) Color.Black
            else Color.White
        mutableStateOf(contentColor)
    }

    val onPickState = rememberUpdatedState(newValue = onPick)
    LaunchedEffect(hsb) {
        // hsb is tracked to update content color and call callback
        snapshotFlow { hsb.color }.collectLatest { newColor ->
            contentColorState.value =
                // Switch content color to white if lightness is too low
                if (newColor.lightness.value >= 0.5) {
                    Color.Black
                } else {
                    Color.White
                }
            onPickState.value.invoke(newColor)
        }
    }

    val currentColorSpace = remember { mutableStateOf(ColorSpace.HSL) }

    // Provide content color and style as CompositionLocal to avoid passing as params
    CompositionLocalProvider(
        LocalContentColorState provides contentColorState,
        LocalChromaPickerStyle provides style
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .drawBehind {
                    drawRect(hsb.color)
                }
                .padding(horizontal = LocalChromaPickerStyle.current.horizontalMargin),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            HSBArea(
                hsb = hsb,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalChromaPickerStyle.current.area.height)
            ) { saturation, brightness ->
                hsb.saturation = saturation
                hsb.brightness = brightness
            }

            ColorSpaceSelector(currentColorSpace = currentColorSpace) { configurer ->
                currentColorSpace.value = configurer
            }

            when (currentColorSpace.value) {
                ColorSpace.HSL -> {
                    HSLSliders(
                        hsb = hsb,
                        onHueChange = { newHue ->
                            hsb.hue = newHue
                        },
                        onSaturationBrightnessChange = { newSaturation, newBrightness ->
                            hsb.saturation = newSaturation
                            hsb.brightness = newBrightness
                        },
                    )
                }

                ColorSpace.RGB -> {
                    RGBSliders(hsb = hsb) { rgb ->
                        hsb.update(rgb)
                    }
                }

                ColorSpace.HEX -> {
                    HexValueInputField(color = hsb.color) { newColor ->
                        hsb.update(newColor)
                    }
                }
            }

            HueSlider(hsb = hsb) { hue, saturation, brightness ->
                hsb.hue = hue
                hsb.saturation = saturation
                hsb.brightness = brightness
            }
        }
    }
}
