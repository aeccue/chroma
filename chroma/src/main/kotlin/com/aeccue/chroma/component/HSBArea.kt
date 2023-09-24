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

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.times
import kotlin.math.roundToInt

@Composable
internal fun HSBArea(
    hsb: HSB,
    modifier: Modifier = Modifier,
    onChange: (SaturationB, Brightness) -> Unit
) {
    val saturationBrush by remember(hsb) {
        derivedStateOf {
            Brush.horizontalGradient(listOf(Color.White, hsb.hue.toColor()))
        }
    }
    val brightnessBrush = remember {
        Brush.verticalGradient(listOf(Color.Transparent, Color.Black))
    }

    val onUpdateState: State<(size: Size, newPosition: Offset) -> Unit> =
        rememberUpdatedState(newValue = { size, newPosition ->
            val newBrightness = Brightness(
                1f - (newPosition.y / size.height).coerceIn(0f, 1f)
            )
            val newSaturationB = SaturationB(
                (newPosition.x / size.width).coerceIn(0f, 1f)
            )
            onChange(newSaturationB, newBrightness)
        })

    BoxWithConstraints(modifier = modifier) {
        val size = remember(constraints) {
            Size(
                width = constraints.maxWidth.toFloat(),
                height = constraints.maxHeight.toFloat()
            )
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .clip(LocalChromaPickerStyle.current.area.shape)
            .drawBehind {
                drawRect(saturationBrush)
                drawRect(brightnessBrush)
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    onUpdateState.value.invoke(size, change.position)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onPress = { newPosition ->
                    onUpdateState.value.invoke(size, newPosition)
                })
            })

        val dotSize = LocalChromaPickerStyle.current.area.dotSize
        val dotOffset by remember(hsb, dotSize) {
            derivedStateOf {
                val dotHalfSize = dotSize / 2
                DpOffset(
                    x = (hsb.saturation.value * maxWidth) - dotHalfSize,
                    y = ((1f - hsb.brightness.value) * maxHeight) - dotHalfSize
                )
            }
        }

        Dot(
            size = LocalChromaPickerStyle.current.area.dotSize,
            color = LocalChromaPickerStyle.current.area.dotColor,
            modifier = Modifier
                .absoluteOffset(x = dotOffset.x, y = dotOffset.y)
                .shadow(
                    elevation = LocalChromaPickerStyle.current.area.dotElevation,
                    shape = CircleShape
                ))
    }
}
