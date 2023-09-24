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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public class ChromaPickerStyle(
    public val horizontalMargin: Dp = 24.dp,
    public val colorSpace: ColorSpace = ColorSpace(),
    public val hue: Hue = Hue(),
    public val area: Area = Area(),
    public val slider: Slider = Slider(),
    public val inputField: InputField = InputField(),
    public val deselectedAlpha: Float = 0.6f,
    public val textStyles: TextStyles = TextStyles()
) {

    public class ColorSpace(
        public val height: Dp = 48.dp,
        public val titlePadding: PaddingValues = PaddingValues(8.dp),
        public val titleIndicationShape: Shape = RoundedCornerShape(4.dp),
    )

    public class Hue(
        public val show: Boolean = true,
        public val includeBlackAndWhite: Boolean = true,
        public val dotColor: Color = Color.White.copy(0.6f)
    )

    public class Area(
        public val height: Dp = 192.dp,
        public val shape: Shape = RoundedCornerShape(12.dp),
        public val dotSize: Dp = 16.dp,
        public val dotColor: Color = Color.White.copy(alpha = 0.8f),
        public val dotElevation: Dp = 6.dp
    )

    public class Slider(
        public val height: Dp = 64.dp,
        public val labelTextStyle: TextStyle = TextStyle(),
        public val trackWidth: Dp = 4.dp,
        public val trackColor: Color = Color.White,
        public val trackElevation: Dp = 6.dp,
        public val dotSize: Dp = 16.dp,
        public val dotColor: Color = Color.White,
        public val dotElevation: Dp = 6.dp,
        public val inputGap: Dp = 24.dp
    )

    public class InputField(
        public val shape: Shape = RoundedCornerShape(12.dp),
        public val contentPadding: PaddingValues = PaddingValues(8.dp),
        public val borderWidth: Dp = 1.dp,
        public val iconSize: Dp = 24.dp,
        public val unfocusedAlpha: Float = 0.6f
    )

    public class TextStyles(
        public val colorSpaceTitle: TextStyle = TextStyle(),
        public val sliderLabel: TextStyle = TextStyle(),
        public val inputField: TextStyle = TextStyle()
    ) {

        public constructor(default: TextStyle) : this(
            colorSpaceTitle = default,
            sliderLabel = default,
            inputField = default
        )
    }
}
