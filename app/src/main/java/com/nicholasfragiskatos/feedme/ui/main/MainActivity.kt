package com.nicholasfragiskatos.feedme.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem
import com.nicholasfragiskatos.feedme.ui.screens.edit.EditScreen
import com.nicholasfragiskatos.feedme.ui.screens.home.FeedingListScreen
import com.nicholasfragiskatos.feedme.ui.theme.FeedMeTheme
import com.nicholasfragiskatos.feedme.utils.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


// TODO: convert all collectAsState -> collectAsStateWithLifecycle
// TODO: convert all = remember to by remember

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedMeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    val navBackstackEntry by navController.currentBackStackEntryAsState()

                    var goalDialogOpen by remember { mutableStateOf(false) }

                    val preferences by remember {
                        preferenceManager.getPreferences()
                    }.collectAsStateWithLifecycle(
                        initialValue = FeedMePreferences(
                            50f,
                            UnitOfMeasurement.MILLILITER,
                            UnitOfMeasurement.MILLILITER
                        )
                    )

                    val scope = rememberCoroutineScope()

                    Scaffold(
                        topBar = {
                            val route = navBackstackEntry?.destination?.route
                            val showBackButton = route != null && route != NavigationItem.Home.route
                            TopAppBar(navController, showBackButton, !showBackButton) {
                                goalDialogOpen = true
                            }
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

                        if (goalDialogOpen) {
                            SettingsDialog(
                                preferences.goal.toString(),
                                preferences.goalUnit,
                                preferences.displayUnit,
                                onDismissRequest = {
                                    goalDialogOpen = false
                                }) { pref, unit, displayUnit ->
                                scope.launch(Dispatchers.IO) {
                                    preferenceManager.writeData(
                                        PreferenceManager.GOAL_KEY_DATA_STORE,
                                        pref
                                    )
                                    preferenceManager.writeData(
                                        PreferenceManager.UNIT_KEY_DATA_STORE,
                                        unit.name
                                    )
                                    preferenceManager.writeData(
                                        PreferenceManager.PREFERRED_UNIT_KEY_DATA_STORE,
                                        displayUnit.name
                                    )
                                }
                                goalDialogOpen = false
                            }
                        }

                        Column(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize(),
                        ) {
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
    }
}
