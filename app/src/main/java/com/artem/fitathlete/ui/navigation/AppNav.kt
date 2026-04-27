
package com.artem.fitathlete.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.SportsGymnastics
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.artem.fitathlete.ui.screens.AnalyticsScreen
import com.artem.fitathlete.ui.screens.FavoritesScreen
import com.artem.fitathlete.ui.screens.HomeScreen
import com.artem.fitathlete.ui.screens.MealsScreen
import com.artem.fitathlete.ui.screens.WorkoutsScreen
import com.artem.fitathlete.viewmodel.MainViewModel
import kotlinx.coroutines.launch

data class BottomDestination(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FitAthleteAppRoot(
    viewModel: MainViewModel,
    isWatermelonTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val items = listOf(
        BottomDestination("Главная", Icons.Rounded.Home),
        BottomDestination("Тренировки", Icons.Rounded.SportsGymnastics),
        BottomDestination("Питание", Icons.Rounded.Restaurant),
        BottomDestination("Аналитика", Icons.Rounded.BarChart),
        BottomDestination("Избранное", Icons.Rounded.Favorite)
    )
    val pagerState = rememberPagerState(pageCount = { items.size })
    val scope = rememberCoroutineScope()

    val message = viewModel.lastMessage
    LaunchedEffect(message) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(padding)
        ) { page ->
            when (page) {
                0 -> HomeScreen(viewModel, isWatermelonTheme, onToggleTheme)
                1 -> WorkoutsScreen(viewModel)
                2 -> MealsScreen(viewModel)
                3 -> AnalyticsScreen(viewModel)
                else -> FavoritesScreen(viewModel)
            }
        }
    }
}
