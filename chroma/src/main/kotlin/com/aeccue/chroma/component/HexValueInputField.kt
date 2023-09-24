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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.aeccue.chroma.R

@Composable
internal fun HexValueInputField(
    color: Color,
    modifier: Modifier = Modifier,
    onPick: (Color) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var hex by remember(color) { mutableStateOf(Hex(color)) }
    val contentColor = LocalContentColorState.current.value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(LocalChromaPickerStyle.current.slider.height * 3),
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = hex.value,
            onValueChange = { value ->
                val newHex = value.filter {
                    it.isHex()
                }
                hex = Hex(
                    value = when {
                        newHex.length <= 6 -> newHex.uppercase()
                        else -> newHex.take(6).uppercase()
                    }
                )

                if (hex.isComplete) {
                    try {
                        val newColor = hex.color
                        onPick(newColor)
                    } catch (ignored: IllegalArgumentException) {
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .onFocusChanged { state ->
                    if (!state.isFocused && !state.hasFocus) {
                        if (!hex.isComplete) {
                            hex = Hex(color)
                        }
                    }
                },
            textStyle = LocalChromaPickerStyle.current.textStyles.inputField.copy(textAlign = TextAlign.Center),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tag),
                    contentDescription = null,
                    modifier = Modifier.size(LocalChromaPickerStyle.current.inputField.iconSize)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            shape = LocalChromaPickerStyle.current.inputField.shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = contentColor,
                focusedBorderColor = contentColor,
                cursorColor = contentColor,
                unfocusedTextColor = contentColor.copy(alpha = LocalChromaPickerStyle.current.inputField.unfocusedAlpha),
                unfocusedBorderColor = contentColor.copy(alpha = LocalChromaPickerStyle.current.inputField.unfocusedAlpha),
                focusedLeadingIconColor = contentColor,
                unfocusedLeadingIconColor = contentColor.copy(alpha = LocalChromaPickerStyle.current.inputField.unfocusedAlpha)
            )
        )
    }
}
