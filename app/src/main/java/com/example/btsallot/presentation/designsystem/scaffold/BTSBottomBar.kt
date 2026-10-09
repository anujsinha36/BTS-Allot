package com.example.btsallot.presentation.designsystem.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.BlueLight

enum class BottomNavItem(val label: String, val icon: ImageVector){
    Calendar("Calendar", Icons.Default.CalendarMonth),
    Duties("My Duties", Icons.Default.ContentPaste),
    Profile("Profile", Icons.Default.Person)
}

@Composable
fun BTSBottomBar(
    currentScreen: BottomNavItem,
    onNavigate: (BottomNavItem) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
          //  .navigationBarsPadding()
            .height(80.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface, // Differentiates from background
        shadowElevation = 24.dp                  // Casts a top shadow
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem.entries.forEach { item ->
                    val isActive = item == currentScreen
                    BottomNavTab(
                        item = item,
                        isActive = isActive,
                        onClick = { onNavigate(item) }
                    )
                }
            }
    }

}


@Composable
fun BottomNavTab(
    item: BottomNavItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) BlueLight.copy(alpha = 0.8f) else Color.Transparent
    val labelColor = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable{onClick()}
            .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 10.dp)
    ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = labelColor,
                modifier = Modifier.size(24.dp)
            )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium,color = labelColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomBar(){
    BTSAllotTheme {
        BTSBottomBar(
            currentScreen = BottomNavItem.Calendar,
            onNavigate = {}
        )
    }
}