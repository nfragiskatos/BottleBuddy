package com.nicholasfragiskatos.feedme.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem
import com.nicholasfragiskatos.feedme.ui.screens.edit.EditScreen
import com.nicholasfragiskatos.feedme.ui.screens.home.FeedingListScreen
import com.nicholasfragiskatos.feedme.ui.theme.FeedMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            FeedMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = NavigationItem.Home.route,
                    ) {
                        composable(route = NavigationItem.Home.route) {
                            FeedingListScreen(navController)
                        }
                        composable(
                            route = NavigationItem.Edit.route + "/{feedingId}",
                            arguments = listOf(
                                navArgument("feedingId") {
                                    type = NavType.LongType
                                    defaultValue = 0L
                                },
                            ),
                        ) {
                            EditScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
