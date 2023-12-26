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
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import kotlin.math.roundToInt

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
        .axisLineColor(MaterialTheme.colorScheme.onSurface)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .shouldDrawAxisLineTillEnd(true)

        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelData { i ->
            val yMin = 0f
            val yMax = maxOf(normalizedGoal, pointsData.maxOf { it.y })

            val yScale = (yMax - yMin) / steps
            if (i == 0) {
                "0${preferences.displayUnit.abbreviation}"
            } else {
                if (preferences.displayUnit == UnitOfMeasurement.MILLILITER) {
                    ((i * yScale) + yMin).roundToInt().toString()
                } else {
                    ((i * yScale) + yMin).formatToSinglePrecision()
                }

            }

        }
        .axisLineColor(MaterialTheme.colorScheme.onSurface)
        .labelAndAxisLinePadding(20.dp)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
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
                        color = MaterialTheme.colorScheme.primary
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        backgroundStyle = Stroke(2f),
                        labelColor = MaterialTheme.colorScheme.primary,
                        labelTypeface = Typeface.DEFAULT_BOLD,
                        popUpLabel = {x, y ->
                            var hour = (x / 60).toInt()
                            val minutes = (x % 60).toInt()
                            val minutesDisplay = if (minutes < 10) "0$minutes" else minutes
                            var ampm = "am"
                            if (hour > 12) {
                                hour -= 12
                                ampm = "pm"
                            }
                            val yval = "%.2f".format(pointsData[x.toInt()].y)
                            "$hour:$minutesDisplay$ampm, y = $yval${preferences.displayUnit.abbreviation}"

                        }
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
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface
    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = data,
    )
}