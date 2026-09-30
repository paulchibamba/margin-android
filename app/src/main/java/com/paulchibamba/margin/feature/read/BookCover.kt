package com.paulchibamba.margin.feature.read

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BookCover(
    imagePath: String?,
    shape: Shape,
    modifier: Modifier = Modifier,
    placeholder: @Composable (Modifier) -> Unit = { placeholderModifier -> StripedCover(shape, placeholderModifier) },
) {
    val image = rememberCoverImage(imagePath)
    if (image == null) {
        placeholder(modifier)
    } else {
        Image(image, contentDescription = null, modifier.clip(shape), contentScale = ContentScale.Crop)
    }
}

@Composable
private fun rememberCoverImage(imagePath: String?): ImageBitmap? {
    val image by produceState(CoverImageCache.cached(imagePath), imagePath) {
        value = imagePath?.let { path -> CoverImageCache.load(path) }
    }
    return image
}

@Preview
@Composable
private fun BookCoverPlaceholderPreview() {
    BookCover(imagePath = null, RoundedCornerShape(6.dp), Modifier.size(width = 42.dp, height = 58.dp))
}
