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

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.roundToInt

@Composable
internal fun FloatSlider(
    title: String?,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    keyboardInput: Boolean = true,
    maxDigits: Int = if (keyboardInput) range.endInclusive.toInt().numberOfDigits else -1,
    trackColor: Brush = SolidColor(LocalChromaPickerStyle.current.slider.trackColor),
    dotColor: Color = LocalChromaPickerStyle.current.slider.dotColor,
    onValueChange: (Float) -> Unit
) {
    val valueState = rememberUpdatedState(newValue = value)
    val onValueChangeState = rememberUpdatedState(newValue = onValueChange)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(LocalChromaPickerStyle.current.slider.height),
        horizontalArrangement = Arrangement.spacedBy(LocalChromaPickerStyle.current.slider.inputGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title != null) {
            Text(
                text = title,
                color = LocalContentColorState.current.value,
                style = LocalChromaPickerStyle.current.textStyles.sliderLabel
            )
        }

        BoxWithConstraints(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            val maxPx = constraints.maxWidth.toFloat()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalChromaPickerStyle.current.slider.trackWidth)
                    .clip(CircleShape)
                    .background(brush = trackColor)
                    .shadow(
                        elevation = LocalChromaPickerStyle.current.slider.trackElevation,
                        shape = CircleShape
                    )
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            val newValue =
                                (valueState.value + dragAmount / maxPx * range.length)
                                    .coerceIn(range)
                            onValueChangeState.value.invoke(newValue)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { newPosition ->
                                val newValue = (newPosition.x / maxPx * range.length)
                                    .coerceIn(range)
                                onValueChangeState.value.invoke(newValue)
                            }
                        )
                    }
            )

            val dotSize = LocalChromaPickerStyle.current.slider.dotSize
            Dot(
                size = dotSize,
                color = dotColor,
                modifier = Modifier
                    .absoluteOffset {
                        val dotHalfSizePx = dotSize.roundToPx() / 2
                        IntOffset(
                            x = ((valueState.value - range.start) / range.length * maxPx).roundToInt() - dotHalfSizePx,
                            y = 0
                        )
                    }
                    .shadow(
                        elevation = LocalChromaPickerStyle.current.slider.dotElevation,
                        shape = CircleShape
                    )
            )
        }

        if (keyboardInput) {
            val focusManager = LocalFocusManager.current
            val text by rememberUpdatedState(value.roundToInt().toString())
            var isTextEmpty by remember { mutableStateOf(false) }
            ValueInputField(
                value = if (isTextEmpty) "" else text,
                onValueChange = { newValue ->
                    if (newValue.isEmpty()) {
                        isTextEmpty = true
                    } else {
                        newValue.toIntOrNull()?.toFloat()?.let {
                            if (range.contains(it)) {
                                isTextEmpty = false
                                onValueChangeState.value.invoke(it)
                            }
                        }
                    }
                },
                modifier = Modifier.width(
                    with(LocalDensity.current) {
                        LocalChromaPickerStyle.current.textStyles.inputField.fontSize.toDp() * maxDigits
                    }
                ),
                textStyle = LocalChromaPickerStyle.current.textStyles.inputField.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            ) { focusState ->
                if (!focusState.isFocused && !focusState.hasFocus) {
                    if (isTextEmpty) {
                        isTextEmpty = false
                        onValueChangeState.value.invoke(value)
                    }
                }
            }
        }
    }
}

@Composable
internal fun IntSlider(
    title: String?,
    value: Int,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    keyboardInput: Boolean = true,
    maxDigits: Int = if (keyboardInput) range.endInclusive.toInt().numberOfDigits else -1,
    trackColor: Brush = SolidColor(LocalChromaPickerStyle.current.slider.trackColor),
    dotColor: Color = LocalChromaPickerStyle.current.slider.dotColor,
    onValueChange: (Int) -> Unit
) {
    val valueState = rememberUpdatedState(newValue = value)
    val onValueChangeState = rememberUpdatedState(newValue = onValueChange)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(LocalChromaPickerStyle.current.slider.height),
        horizontalArrangement = Arrangement.spacedBy(LocalChromaPickerStyle.current.slider.inputGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title != null) {
            Text(
                text = title,
                color = LocalContentColorState.current.value,
                style = LocalChromaPickerStyle.current.textStyles.sliderLabel
            )
        }

        BoxWithConstraints(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            val maxPx = constraints.maxWidth.toFloat()
            val rawOffset = remember {
                mutableFloatStateOf(value.toFloat() / range.endInclusive * maxPx)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalChromaPickerStyle.current.slider.trackWidth)
                    .clip(CircleShape)
                    .background(brush = trackColor)
                    .shadow(
                        elevation = LocalChromaPickerStyle.current.slider.trackElevation,
                        shape = CircleShape
                    )
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            val newOffset = (rawOffset.floatValue + dragAmount).coerceIn(0f, maxPx)
                            rawOffset.floatValue = newOffset
                            val newValue = (newOffset / maxPx * range.endInclusive).roundToInt()
                            if (newValue != valueState.value) {
                                onValueChangeState.value.invoke(newValue)
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { newPosition ->
                                rawOffset.floatValue =
                                    newPosition.x
                                        .coerceIn(0f, maxPx)
                                        .roundToInt()
                                        .toFloat()
                                val newValue = (rawOffset.floatValue / maxPx * range.endInclusive)
                                    .roundToInt()
                                if (newValue != valueState.value) {
                                    onValueChangeState.value.invoke(newValue)
                                }
                            }
                        )
                    }
            )

            val dotSize = LocalChromaPickerStyle.current.slider.dotSize
            Dot(
                size = dotSize,
                color = dotColor,
                modifier = Modifier
                    .absoluteOffset {
                        val dotHalfSizePx = dotSize.roundToPx() / 2
                        IntOffset(
                            x = (valueState.value.toFloat() / range.endInclusive * maxPx).roundToInt() - dotHalfSizePx,
                            y = 0
                        )
                    }
                    .shadow(
                        elevation = LocalChromaPickerStyle.current.slider.dotElevation,
                        shape = CircleShape
                    )
            )
        }

        if (keyboardInput) {
            val focusManager = LocalFocusManager.current
            val text by rememberUpdatedState(value.toString())
            var isTextEmpty by remember { mutableStateOf(false) }

            ValueInputField(
                value = if (isTextEmpty) "" else text,
                onValueChange = { newValue ->
                    if (newValue.isEmpty()) {
                        isTextEmpty = true
                    } else {
                        newValue.toIntOrNull()?.let {
                            if (range.contains(it.toFloat())) {
                                isTextEmpty = false
                                onValueChangeState.value.invoke(it)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(
                        with(LocalDensity.current) {
                            LocalChromaPickerStyle.current.textStyles.inputField.fontSize.toDp() * maxDigits
                        }
                    ),
                textStyle = LocalChromaPickerStyle.current.textStyles.inputField.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            ) { focusState ->
                if (!focusState.isFocused && !focusState.hasFocus) {
                    if (isTextEmpty) {
                        isTextEmpty = false
                        onValueChangeState.value.invoke(value)
                    }
                }
            }
        }
    }
}


private inline val ClosedFloatingPointRange<Float>.length: Float
    get() = endInclusive - start

private inline val Int.numberOfDigits: Int
    get() = when (this) {
        0 -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }
