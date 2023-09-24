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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.aeccue.chroma.component.Brightness
import com.aeccue.chroma.component.FloatSlider
import com.aeccue.chroma.component.HSB
import com.aeccue.chroma.component.HSBArea
import com.aeccue.chroma.component.HSLSliders
import com.aeccue.chroma.component.HexValueInputField
import com.aeccue.chroma.component.Hue
import com.aeccue.chroma.component.HueSlider
import com.aeccue.chroma.component.LocalChromaPickerStyle
import com.aeccue.chroma.component.LocalContentColorState
import com.aeccue.chroma.component.RGBSliders
import com.aeccue.chroma.component.SaturationB
import com.aeccue.chroma.component.lightness
import com.aeccue.chroma.component.rememberHSB
import kotlinx.coroutines.flow.collectLatest

private enum class ColorSpace {
    HSL, RGB, HEX
}

@Composable
public fun ChromaPicker(
    initialColor: Color,
    modifier: Modifier = Modifier,
    style: ChromaPickerStyle = ChromaPickerStyle(
        textStyles = ChromaPickerStyle.TextStyles(LocalTextStyle.current)
    ),
    onPick: (Color) -> Unit
) {
    val hsb = rememberHSB(
        initialColor = initialColor,
        extendForBlackAndWhite = LocalChromaPickerStyle.current.hue.includeBlackAndWhite
    )
    val contentColorState = remember(hsb) {
        val contentColor =
            if (hsb.color.lightness.value >= 0.5f) Color.Black
            else Color.White
        mutableStateOf(contentColor)
    }

    val onPickState = rememberUpdatedState(newValue = onPick)
    LaunchedEffect(hsb) {
        snapshotFlow { hsb.color }.collectLatest { newColor ->
            contentColorState.value =
                if (newColor.lightness.value >= 0.5) {
                    Color.Black
                } else {
                    Color.White
                }
            onPickState.value.invoke(newColor)
        }
    }

    val currentColorSpace = remember { mutableStateOf(ColorSpace.HSL) }

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

@Composable
private fun ColorSpaceSelector(
    currentColorSpace: State<ColorSpace>,
    modifier: Modifier = Modifier,
    onSelect: (ColorSpace) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(LocalChromaPickerStyle.current.colorSpace.height),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (colorSpace in ColorSpace.entries) {
            ColorSpaceTitle(
                colorSpace = colorSpace,
                currentColorSpace = currentColorSpace
            ) {
                onSelect(colorSpace)
            }
        }
    }
}

@Composable
private fun ColorSpaceTitle(
    colorSpace: ColorSpace,
    currentColorSpace: State<ColorSpace>,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val isSelected by remember { derivedStateOf { currentColorSpace.value == colorSpace } }

    Text(
        text = colorSpace.name,
        modifier = modifier
            .clip(LocalChromaPickerStyle.current.colorSpace.titleIndicationShape)
            .clickable(onClick = onSelect)
            .padding(LocalChromaPickerStyle.current.colorSpace.titlePadding),
        color = LocalContentColorState.current.value.copy(
            alpha = if (isSelected) 1f else LocalChromaPickerStyle.current.deselectedAlpha
        ),
        fontWeight = if (isSelected) FontWeight.Bold else null,
        style = LocalChromaPickerStyle.current.textStyles.colorSpaceTitle
    )
}
