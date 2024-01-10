package com.nicholasfragiskatos.feedme.ui.screens.dayoverview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.ui.main.TopAppBar
import com.nicholasfragiskatos.feedme.ui.screens.dayoverview.statistics.StatisticRowNew
import com.nicholasfragiskatos.feedme.ui.screens.home.graph.CumulativeGoalGraph
import com.nicholasfragiskatos.feedme.ui.screens.home.listcontent.FeedingItem
import com.nicholasfragiskatos.feedme.utils.TransitionStateSaver
import com.nicholasfragiskatos.feedme.utils.dates.DateFormatter

@Composable
fun DayOverviewScreen(
    navController: NavController,
    vm: DayOverviewScreenViewModel = hiltViewModel()
) {
    val date by vm.date.collectAsStateWithLifecycle()
    val feedingsForDay by vm.feedingsForDay.collectAsStateWithLifecycle()
    val graphPoints by vm.graphPoints.collectAsStateWithLifecycle()
    val preferences by vm.preferences.collectAsStateWithLifecycle()
    val smallestFeeding by vm.smallestFeeding.collectAsStateWithLifecycle()
    val largestFeeding by vm.largestFeeding.collectAsStateWithLifecycle()
    val averageFeeding by vm.averageFeeding.collectAsStateWithLifecycle()
    val perFeeding by vm.perFeeding.collectAsStateWithLifecycle()
    val perHour by vm.perHour.collectAsStateWithLifecycle()
    val total by vm.total.collectAsStateWithLifecycle()

    val animationState = rememberSaveable(saver = TransitionStateSaver) {
        MutableTransitionState(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navController = navController,
                hasBackButton = true,
                hasSettingsButton = false,
                hasShareButton = false
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateFormatter.getFormattedDate(date),
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    onClick = { animationState.targetState = !animationState.targetState },
                ) {
                    Text(text = "${if (animationState.currentState) "Hide" else "Show"} Chart")
                }
            }

            AnimatedVisibility(visibleState = animationState) {
                // TODO: Figure out why y-axis is off (being based on the goal line even when it's turned off)
                CumulativeGoalGraph(
                    pointsData = graphPoints,
                    preferences = preferences,
                    showGoalLine = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    StatisticRowNew(statistic = smallestFeeding)
                    StatisticRowNew(statistic = largestFeeding)
                    StatisticRowNew(statistic = averageFeeding)
                    StatisticRowNew(statistic = perFeeding)
                    StatisticRowNew(statistic = perHour)
                    StatisticRowNew(statistic = total)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                state = rememberLazyListState()
            ) {
                itemsIndexed(
                    items = feedingsForDay,
                    key = { _, feeding -> feeding.id }
                ) { index, feeding ->
                    FeedingItem(
                        feeding = feeding,
                        displayUnit = preferences.displayUnit,
                        onClick = {}
                    )
                    if (index < feedingsForDay.size) {
                        HorizontalDivider(thickness = 1.dp)
                    }
                }
            }
        }
    }
}