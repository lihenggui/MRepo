package com.sanmer.mrepo.ui.screens.settings.workingmode

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R

@Composable
fun WorkingModeItem(
    title: String,
    desc1: String,
    desc2: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) = Box(
    modifier = modifier.fillMaxWidth(0.65f)
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        tonalElevation = if (selected) 4.dp else 0.dp,
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = desc1,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = desc2,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (selected) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp, end = 8.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.circle_check_filled),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}