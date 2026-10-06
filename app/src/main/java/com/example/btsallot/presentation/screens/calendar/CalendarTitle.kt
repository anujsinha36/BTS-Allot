package com.example.btsallot.presentation.screens.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.btsallot.R
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.BlueLight
import com.example.btsallot.presentation.theme.SurfaceWhite
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
    isHorizontal: Boolean = true,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlueLight.copy(0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)

    ) {
        Row(
            modifier = modifier.height(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CalendarNavigationIcon(
                icon = R.drawable.ic_chevron_left_24dp ,
                contentDescription = "Previous",
                onClick = goToPrevious,
                isHorizontal = isHorizontal,
            )
            // format to have month title in String like June 2026
            val monthTitle = currentMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
            Text(
                modifier = Modifier
                    .weight(1f)
                    .testTag("MonthTitle"),
                text = monthTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
            )
            CalendarNavigationIcon(
                icon = R.drawable.ic_chevron_right_24dp,
                contentDescription = "Next",
                onClick = goToNext,
                isHorizontal = isHorizontal,
            )

        }
    }
}

@Composable
private fun CalendarNavigationIcon(
    icon: Int,
    contentDescription: String,
    isHorizontal: Boolean = true,
    onClick: () -> Unit,
) = Box(
    modifier = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .clickable(role = Role.Button, onClick = onClick),
) {
    val rotation by animateFloatAsState(
        targetValue = if (isHorizontal) 0f else 90f,
        label = "CalendarNavigationIconAnimation",
    )
    Icon(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .align(Alignment.Center)
            .rotate(rotation),
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Preview(showBackground = true)
@Composable
private fun CalendarTitlePreview() {
    BTSAllotTheme() {
        CalendarTitle(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            currentMonth = YearMonth.now(),
            goToPrevious = {},
            goToNext = {},
        )
    }
}
