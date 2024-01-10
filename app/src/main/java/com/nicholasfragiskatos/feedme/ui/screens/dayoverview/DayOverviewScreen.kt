package com.nicholasfragiskatos.feedme.ui.screens.dayoverview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.ui.main.TopAppBar
import com.nicholasfragiskatos.feedme.ui.screens.home.graph.CumulativeGoalGraph
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import com.nicholasfragiskatos.feedme.utils.dates.DateFormatter

@Composable
fun DayOverviewScreen(
    navController: NavController,
    vm: DayOverviewScreenViewModel = hiltViewModel()
) {
    val date by vm.date.collectAsStateWithLifecycle()
    val graphPoints by vm.graphPoints.collectAsStateWithLifecycle()
    val preferences by vm.preferences.collectAsStateWithLifecycle()
    val smallestFeeding by vm.smallestFeeding.collectAsStateWithLifecycle()
    val largestFeeding by vm.largestFeeding.collectAsStateWithLifecycle()
    val averageFeeding by vm.averageFeeding.collectAsStateWithLifecycle()

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
                Text(text = DateFormatter.getFormattedDate(date), style = MaterialTheme.typography.headlineLarge)
            }


            CumulativeGoalGraph(
                pointsData = graphPoints,
                preferences = preferences,
                showGoalLine = false
            )

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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val formatted = UnitUtils.format(smallestFeeding, preferences.displayUnit)
                        Text(text = "Smallest", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "$formatted${preferences.displayUnit.abbreviation}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val formatted = UnitUtils.format(largestFeeding, preferences.displayUnit)
                        Text(text = "Largest", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "$formatted${preferences.displayUnit.abbreviation}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val formatted = UnitUtils.format(averageFeeding, preferences.displayUnit)
                        Text(text = "Average", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "$formatted${preferences.displayUnit.abbreviation}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

//            HorizontalDivider(
//                modifier = Modifier.padding(top = 16.dp),
//                thickness = 4.dp
//            )
//
//            Text(text = "Smallest = $smallestFeeding${preferences.displayUnit.abbreviation}")
//            Text(text = "Largest = $largestFeeding${preferences.displayUnit.abbreviation}")
        }
    }
}