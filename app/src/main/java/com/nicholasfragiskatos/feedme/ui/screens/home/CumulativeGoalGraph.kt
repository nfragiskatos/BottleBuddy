package com.nicholasfragiskatos.feedme.ui.screens.home

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.utils.UnitUtils

@Composable
fun CumulativeGoalGraph(
    pointsData: List<Point>,
    preferences: FeedMePreferences,
) {
    val normalizedGoal = UnitUtils.convertMeasurement(
        preferences.goal.toDouble(),
        preferences.goalUnit,
        preferences.displayUnit
    ).toFloat()

    val steps = 10
    val xAxisData = AxisData.Builder()
        .axisStepSize(1.dp)
        .steps(24)
        .labelData { i ->
            val normalized = i / 60
            if (normalized <= 12) {
                "${normalized}am"
            } else {
                "${normalized - 12}pm"
            }
        }
        .labelAndAxisLinePadding(15.dp)
        .axisLabelAngle(45f)
        .axisLineColor(MaterialTheme.colorScheme.primary)
        .shouldDrawAxisLineTillEnd(true)

        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelData { i ->
            val yMin = 0f
            val yMax = maxOf(normalizedGoal, pointsData.maxOf { it.y })

            val yScale = (yMax - yMin) / steps
            "${((i * yScale) + yMin).formatToSinglePrecision()}${preferences.displayUnit.abbreviation}"
        }
        .axisLineColor(MaterialTheme.colorScheme.primary)
        .labelAndAxisLinePadding(20.dp)
        .build()
    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(isDotted = true),
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                Color.Transparent
                            )
                        ), alpha = 0.3f
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = Color.Green
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = Color.Black,
                        backgroundStyle = Stroke(2f),
                        labelColor = Color.Red,
                        labelTypeface = Typeface.DEFAULT_BOLD
                    )
                ),
                Line(
                    dataPoints = MutableList(1440) { index ->
                        Point(index.toFloat(), normalizedGoal)
                    },
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(isDotted = true),
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = data
    )
}