import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mnfit.navigation.BottomNavScreen
import com.example.mnfit.ui.theme.gym_Blue
import com.example.mnfit.ui.theme.gym_LightBlue
import com.example.mnfit.ui.theme.gym_LightGray

@Composable
fun MainBottomNavBar(navController: NavController, items: List<BottomNavScreen>) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(gym_Blue, Color.Transparent)
                ),
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        NavigationBar(
            containerColor = Color.Black.copy(alpha = 0.7f)
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
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = gym_Blue,
                        selectedTextColor = gym_Blue,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    }
}
