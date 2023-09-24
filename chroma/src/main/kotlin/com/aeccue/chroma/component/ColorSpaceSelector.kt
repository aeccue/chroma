package com.aeccue.chroma.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight


internal enum class ColorSpace {
    HSL, RGB, HEX
}

@Composable
internal fun ColorSpaceSelector(
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
