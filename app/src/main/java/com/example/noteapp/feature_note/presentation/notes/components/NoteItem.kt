package com.example.noteapp.feature_note.presentation.notes.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.example.noteapp.core.util.TestTags

@Composable
fun NoteItem(
    note: com.example.noteapp.feature_note.domain.model.Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCornerSize: Dp = 30.dp,
    onDeleteClick: () -> Unit //when we click on delete fun we trigger it
) {
    Box(
        modifier = modifier
            .testTag(TestTags.NOTE_ITEM)
    ) {

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val clipPath = Path().apply {
                lineTo(
                    size.width - cutCornerSize.toPx(),
                    0f
                )   //Drawing one line minus 30.dp  x=70 y = 0
                lineTo(size.width, cutCornerSize.toPx())   //then line down to the end of note but y
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            clipPath(clipPath) {
                drawRoundRect( // making our note
                    color = Color(note.color),
                    size = size, // size of our canvas
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
                drawRoundRect(   //making little (savinuti) part in upper right corner of note
                    color = Color(
                        ColorUtils.blendARGB(
                            note.color,
                            0x000000,
                            0.2f
                        ) // mixing first color by adding 20% of second color.
                    ),
                    topLeft = Offset(size.width - cutCornerSize.toPx(), -100f),
                    size = Size(
                        cutCornerSize.toPx() + 100f,
                        cutCornerSize.toPx() + 100f
                    ), // size of our canvas
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(end = 32.dp) // to make sure text doesnt override out trash button in note.
        ) {
            Text(  // title
                text = note.title,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // text gets cut off if its to long
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text( //note content
                text = note.content,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis // text gets cut off if its to long
            )
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.align(Alignment.BottomEnd) //putting trash can in left down corner.
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete note"
            )
        }

    }
}