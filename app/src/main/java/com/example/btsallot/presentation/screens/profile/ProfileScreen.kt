package com.example.btsallot.presentation.screens.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.domain.model.User
import com.example.btsallot.presentation.designsystem.scaffold.AppScaffold
import com.example.btsallot.presentation.designsystem.scaffold.BottomNavItem
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.BlueIconBackground
import com.example.btsallot.presentation.theme.BlueLight
import com.example.btsallot.presentation.theme.LogoutRed
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.dimensions

@Composable
fun ProfileScreen(
    profile: User,
    onLogout: () -> Unit,
    onNavigation: ((BottomNavItem) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val dimens = MaterialTheme.dimensions

    AppScaffold(
        title = "Profile",
        currentScreen = BottomNavItem.Profile,
        onNavigate = onNavigation
    ) {paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize().padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = dimens.medium)
                .padding(top = dimens.small, bottom = dimens.large)
        ) {
            // Scrollable content so small screens / large font scales don't clip
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(dimens.large))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(BlueLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(Modifier.height(dimens.medium))

                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(dimens.extraSmall))
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(dimens.large))

                ProfileInfoCard(
                    icon = Icons.Filled.Email,
                    label = "Email",
                    value = profile.email
                )
                Spacer(Modifier.height(dimens.small + dimens.extraSmall))
                ProfileInfoCard(
                    icon = Icons.Outlined.Group,
                    label = "Account Type",
                    value = profile.role
                )
            }

            Spacer(Modifier.height(dimens.medium))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogoutRed,
                    contentColor = SurfaceWhite
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(dimens.small))
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

    }


}

@Composable
private fun ProfileInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val dimens = MaterialTheme.dimensions

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(BlueIconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column() {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(dimens.extraSmall))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ProfileScreenPreview() {
    BTSAllotTheme {
        ProfileScreen(
            profile = User(
                uid = "",
                name = "Anuj Sinha",
                email = "anuj.sinha@gmail.com",
                role = "BTS",
                photoUrl = null
            ),
            onLogout = {},
            onNavigation = {},
        )
    }
}