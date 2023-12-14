package com.nicholasfragiskatos.feedme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nicholasfragiskatos.feedme.ui.screens.home.FeedingListScreen
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem
import com.nicholasfragiskatos.feedme.ui.screens.edit.EditScreen
import com.nicholasfragiskatos.feedme.ui.theme.FeedMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedMeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val title = remember { mutableStateOf("Home") }

                    val navController = rememberNavController()

                    val navBackstackEntry by navController.currentBackStackEntryAsState()

                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(title = {
                                Text(text = title.value)
                            })
                        },
                        floatingActionButton = {
                            val showFab =
                                navBackstackEntry?.destination?.route == NavigationItem.Home.route
                            if (showFab) {
                                LargeFloatingActionButton(onClick = {
                                    navController.navigate(
                                        NavigationItem.Edit.buildRoute(0L),
                                    )
                                }) {
                                    Icon(Icons.Filled.Edit, "Floating action button.")
                                }
                            }
                        },
                    ) {
                        Column(
                            modifier = Modifier.padding(it).fillMaxSize(),
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = NavigationItem.Home.route,
                            ) {
                                composable(route = NavigationItem.Home.route) {
                                    FeedingListScreen(navController)
                                    title.value = "Home"
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
                                    title.value = "Edit"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
