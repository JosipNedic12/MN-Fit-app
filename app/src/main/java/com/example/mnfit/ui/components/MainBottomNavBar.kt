package com.example.mnfit.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mnfit.navigation.BottomNavScreen
import androidx.compose.foundation.layout.size
import com.example.mnfit.ui.theme.gym_Coral
import com.example.mnfit.ui.theme.gym_LightGray
import com.example.mnfit.ui.theme.gym_Mauve
import com.example.mnfit.ui.theme.gym_PastelYellow
import com.example.mnfit.ui.theme.gym_Plum

@Composable
fun MainBottomNavBar(navController: NavController, items: List<BottomNavScreen>) {
    NavigationBar(
        containerColor = gym_Plum
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        screen.icon,
                        contentDescription = screen.label,
                        modifier = Modifier.size(32.dp)
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(indicatorColor = gym_Mauve)
            )
        }
    }
}
