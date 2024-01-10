package com.nicholasfragiskatos.feedme.ui.screens.home

import android.content.Intent
import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.ui.main.SettingsDialog
import com.nicholasfragiskatos.feedme.ui.main.TopAppBar
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem
import com.nicholasfragiskatos.feedme.ui.screens.home.draggable.DraggableFeedingItem
import com.nicholasfragiskatos.feedme.ui.screens.home.draggable.FeedingDeleteAction
import com.nicholasfragiskatos.feedme.ui.screens.home.draggable.FeedingDragAnchors
import com.nicholasfragiskatos.feedme.ui.screens.home.graph.CumulativeGoalGraph
import com.nicholasfragiskatos.feedme.ui.screens.home.listcontent.FeedingItem
import com.nicholasfragiskatos.feedme.ui.screens.home.listcontent.Header
import com.nicholasfragiskatos.feedme.utils.TransitionStateSaver
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.Date

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedingListScreen(
    navController: NavController,
    vm: FeedingListScreenViewModel = hiltViewModel(),
) {
    val groupState by vm.groupState.collectAsStateWithLifecycle()
    val preferences by vm.preferences.collectAsStateWithLifecycle()
    val graphPoints by vm.graphPoints.collectAsStateWithLifecycle()
    val daySummaryState by vm.daySummaryState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val density = LocalDensity.current
    val defaultActionSize = 80.dp
    val startActionSizePx = with(density) { defaultActionSize.toPx() }
    val animationState = rememberSaveable(saver = TransitionStateSaver) {
        MutableTransitionState(true)
    }
    var goalDialogOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    navController.currentBackStackEntry?.savedStateHandle?.let { savedStateHandle ->
        val exists = savedStateHandle.remove<Boolean>("ACTION_DELETE_KEY") ?: false
        if (exists) {
            scope.launch {
                val result = snackbarHostState
                    .showSnackbar(
                        message = "Feeding Deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                when (result) {
                    SnackbarResult.Dismissed -> {}
                    SnackbarResult.ActionPerformed -> {
                        vm.undoLastDelete()
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost (hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                navController = navController,
                hasBackButton = false,
                hasSettingsButton = true
            ) {
                goalDialogOpen = true
            }
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(
                    NavigationItem.Edit.buildRoute(0L),
                )
            }) {
                Icon(Icons.Filled.Edit, "Floating action button.")
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
                vm.writePreferences(pref, unit, displayUnit)
                goalDialogOpen = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    onClick = { animationState.targetState = !animationState.targetState },
                ) {
                    Text(text = "${if (animationState.currentState) "Hide" else "Show"} Today's Progress")
                }
            }

            AnimatedVisibility(visibleState = animationState) {
                CumulativeGoalGraph(
                    pointsData = graphPoints,
                    preferences = preferences,
                )
            }

            if (!groupState.isLoading && groupState.data.isEmpty()) {
                EmptyFeedingListView(navController)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = rememberLazyListState(),
                ) {
                    groupState.data.forEach { (date, feedings) ->

                        val dayTotal = feedings.sumOf { feeding ->
                            UnitUtils.convertMeasurement(
                                feeding.quantity,
                                feeding.unit,
                                preferences.displayUnit,
                            )
                        }

                        stickyHeader {
                            Header(
                                date = date,
                                dayTotal = "${
                                    UnitUtils.format(
                                        dayTotal,
                                        preferences.displayUnit,
                                    )
                                }${preferences.displayUnit.abbreviation}",
                                isLoading = daySummaryState.loading && daySummaryState.date == date,
                                onViewDayOverview = {
                                    val from =
                                        Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
                                    navController.navigate(
                                        NavigationItem.DayOverview.buildRoute(from.time)
                                    )
                                }
                            ) {
                                vm.generateDaySummary(
                                    date,
                                    DateFormat.is24HourFormat(context),
                                    preferences.displayUnit,
                                ) { summary ->
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            summary,
                                        )
                                    }
                                    val chooser = Intent.createChooser(intent, "Send Feeding via")
                                    context.startActivity(chooser)
                                }
                            }
                        }

                        itemsIndexed(
                            items = feedings,
                            key = { _, feeding -> feeding.id },
                        ) { index, feeding ->

                            val anchorState = remember {
                                AnchoredDraggableState(
                                    initialValue = FeedingDragAnchors.CENTER,
                                    anchors = DraggableAnchors {
                                        FeedingDragAnchors.START at -startActionSizePx
                                        FeedingDragAnchors.CENTER at 0f
                                    },
                                    positionalThreshold = { distance: Float -> distance * 0.5f },
                                    velocityThreshold = { with(density) { 100.dp.toPx() } },
                                    animationSpec = tween(),

                                    )
                            }

                            DraggableFeedingItem(
                                state = anchorState,
                                content = {
                                    FeedingItem(feeding = feeding, preferences.displayUnit) {
                                        navController.navigate(
                                            NavigationItem.Edit.buildRoute(
                                                feeding.id
                                            )
                                        )
                                    }
                                },
                                feedingStartAction = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .align(Alignment.CenterStart),
                                    ) {
                                        FeedingDeleteAction(
                                            modifier = Modifier
                                                .width(defaultActionSize)
                                                .fillMaxHeight(),
                                        ) {
                                            scope.launch {
                                                val result = snackbarHostState
                                                    .showSnackbar(
                                                        message = "Feeding Deleted",
                                                        actionLabel = "Undo",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                when (result) {
                                                    SnackbarResult.Dismissed -> {}
                                                    SnackbarResult.ActionPerformed -> {
                                                        vm.undoLastDelete()
                                                    }
                                                }
                                            }
                                            vm.deleteFeeding(feeding)
                                        }
                                    }
                                },
                            )

                            if (index < feedings.lastIndex) {
                                HorizontalDivider(thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}
