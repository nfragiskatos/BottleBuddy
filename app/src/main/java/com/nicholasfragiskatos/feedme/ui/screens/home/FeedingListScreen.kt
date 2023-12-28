package com.nicholasfragiskatos.feedme.ui.screens.home

import android.content.Intent
import android.text.format.DateFormat
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem
import com.nicholasfragiskatos.feedme.ui.screens.home.draggable.DraggableFeedingItem
import com.nicholasfragiskatos.feedme.ui.screens.home.draggable.FeedingDeleteAction
import com.nicholasfragiskatos.feedme.ui.screens.home.draggable.FeedingDragAnchors
import com.nicholasfragiskatos.feedme.ui.screens.home.graph.CumulativeGoalGraph
import com.nicholasfragiskatos.feedme.ui.screens.home.listcontent.FeedingItem
import com.nicholasfragiskatos.feedme.ui.screens.home.listcontent.Header
import com.nicholasfragiskatos.feedme.utils.DateUtils
import com.nicholasfragiskatos.feedme.utils.UnitUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedingListScreen(
    navController: NavController,
    vm: FeedingListScreenViewModel = hiltViewModel(),
) {
    val grouping by vm.grouping.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val preferences by vm.preferences.collectAsStateWithLifecycle()
    var isProgressExpanded by remember { mutableStateOf(true) }
    val graphPoints by vm.graphPoints.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val density = LocalDensity.current
    val defaultActionSize = 80.dp
    val startActionSizePx = with(density) { defaultActionSize.toPx() }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { isProgressExpanded = !isProgressExpanded }) {
                Text(text = "${if (isProgressExpanded) "Hide" else "Show"} Today's Progress")
            }
        }
        if (isProgressExpanded) {
            CumulativeGoalGraph(
                pointsData = graphPoints,
                preferences = preferences,
            )
        }
        if (grouping.isEmpty() && !loading) {
            EmptyFeedingListView(navController)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyListState()
            ) {
                grouping.forEach { (date, feedings) ->

                    val dayTotal = feedings.sumOf { feeding ->
                        UnitUtils.convertMeasurement(
                            feeding.quantity,
                            feeding.unit,
                            preferences.displayUnit
                        )
                    }

                    stickyHeader {
                        Header(
                            date = date,
                            dayTotal = "Total: %.2f${preferences.displayUnit.abbreviation}".format(
                                dayTotal
                            )
                        ) {
                            val is24HourFormat = DateFormat.is24HourFormat(context)
                            val total = "%.2f".format(dayTotal)
                            val sb =
                                StringBuilder("Summary for ${DateUtils.getFormattedDate(date)}\n")
                            for (index in feedings.indices.reversed()) {
                                val feeding = feedings[index]
                                val quantity = "%.2f".format(
                                    UnitUtils.convertMeasurement(
                                        feeding.quantity,
                                        feeding.unit,
                                        preferences.displayUnit
                                    )
                                )
                                sb.append(
                                    "\n${
                                        DateUtils.getFormattedTime(
                                            feeding.date,
                                            is24HourFormat
                                        )
                                    } - $quantity${preferences.displayUnit.abbreviation}"
                                )
                            }
                            sb.append("\n------------")
                            sb.append("\nTotal: $total${preferences.displayUnit.abbreviation}")

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    sb.toString()
                                )
                            }
                            val chooser = Intent.createChooser(intent, "Send Feeding via")
                            context.startActivity(chooser)
                        }
                    }

                    items(
                        items = feedings,
                        key = { it.id }
                    ) { feeding ->

                        val anchorState = remember {
                            AnchoredDraggableState(
                                initialValue = FeedingDragAnchors.CENTER,
                                anchors = DraggableAnchors {
                                    FeedingDragAnchors.START at -startActionSizePx
                                    FeedingDragAnchors.CENTER at 0f
                                },
                                positionalThreshold = { distance: Float -> distance * 0.5f },
                                velocityThreshold = { with(density) { 100.dp.toPx() } },
                                animationSpec = tween()

                            )
                        }

                        DraggableFeedingItem(
                            state = anchorState,
                            content = {
                                FeedingItem(feeding = feeding, preferences.displayUnit) {
                                    navController.navigate(NavigationItem.Edit.buildRoute(feeding.id))
                                }
                            },
                            feedingStartAction = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .align(Alignment.CenterStart)
                                ) {
                                    FeedingDeleteAction(
                                        modifier = Modifier
                                            .width(defaultActionSize)
                                            .fillMaxHeight()
                                    ) {
                                        vm.deleteFeeding(feeding)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
