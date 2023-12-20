package com.nicholasfragiskatos.feedme.utils

import co.yml.charts.common.model.Point

object DataUtils {

    fun getLineChartData(listSize: Int, start: Int = 0, maxRange: Int): List<Point> {
        val list = arrayListOf<Point>()
        for (index in 0 until listSize) {
            list.add(
                Point(
                    index.toFloat(),
                    (start until maxRange).random().toFloat()
                )
            )
        }
        return list
    }

    fun getLineChartDateData(listSize: Int, start: Int = 0, maxRange: Int): List<Point> {
        val list = MutableList(48) { index -> Point(index.toFloat(), 0.0f) }
        var hour = 0
        var minute = 0

        for (index in 0 until 48) {
            hour = index //(0 until 24).random()
            minute = (0 until 60).random()

//            val absoluteMinute = (hour * 60) + minute
//            val x = hour.toFloat() + (minute.toFloat() / 60f)
            val x = index.toFloat() / 2f
            val y = 5f//(start until maxRange).random().toFloat()


            list[index] = Point(x, y)
        }

        for (index in 1 until list.size) {
            val cur = list[index].y
            val prev = list[index - 1].y
            list[index] = list[index].copy(y = prev + cur)
        }

        return list
    }
}