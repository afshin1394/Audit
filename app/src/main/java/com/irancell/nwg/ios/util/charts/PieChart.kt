package com.irancell.nwg.ios.util.charts

import android.graphics.Color
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF


data class PieChartModel(val value: Float, val label: String, val color: Int)

fun createPieChart(
    language: String,
    pieChart: PieChart,
    pieChartModels: ArrayList<PieChartModel>,
    description: String,
    holeColor: Int
) {
    pieChart.description.isEnabled = false
    pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
    pieChart.dragDecelerationFrictionCoef = 0.95f
    pieChart.centerText = description
    pieChart.setCenterTextColor(Color.parseColor("#ffffff"))
    pieChart.center
    pieChart.isDrawHoleEnabled = true
    pieChart.setHoleColor(holeColor)
    pieChart.setTransparentCircleColor(Color.WHITE)
    pieChart.setTransparentCircleAlpha(110)
    pieChart.transparentCircleRadius = 62f
    pieChart.holeRadius = 60f
    pieChart.setDrawCenterText(true)
    pieChart.setCenterTextSize(20f)
    pieChart.rotationAngle = 0f
    // enable rotation of the chart by touch
    pieChart.isRotationEnabled = true
    pieChart.isHighlightPerTapEnabled = true
    pieChart.description.textColor = Color.WHITE


    pieChart.animateY(1400, Easing.EaseInOutQuad)
//    pieChart.spin(2000, 0f, 360f,Easing.EaseInOutQuad);
    val l = pieChart.legend
    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP

    if (language == "fa")
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    if (language == "en")
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT

    l.orientation = Legend.LegendOrientation.VERTICAL
    l.setDrawInside(false)
    l.xEntrySpace = 7f
    l.yEntrySpace = 0f
    l.xOffset = 17f
    l.yOffset = 0f
    l.textColor = Color.WHITE

    // entry label styling
    pieChart.setEntryLabelColor(Color.WHITE)
    pieChart.setEntryLabelTextSize(12f)

    val entries = ArrayList<PieEntry>()
    val colors = ArrayList<Int>()

    // NOTE: The order of the entries when being added to the entries array determines their position around the center of
    // the chart.
    for (i in 0 until pieChartModels.size) {
        entries.add(
            PieEntry(
                pieChartModels[i].value,
                pieChartModels[i].label
            )
        )
        colors.add(pieChartModels[i].color)
    }
    val dataSet = PieDataSet(entries, null)
    dataSet.setDrawIcons(false)
    dataSet.sliceSpace = 3f
    dataSet.iconsOffset = MPPointF(0f, 40f)
    dataSet.selectionShift = 5f


    dataSet.colors = colors
    //dataSet.setSelectionShift(0f);
    val data = PieData(dataSet)
    data.setValueFormatter(MyDecimalValueFormatter())
    data.setValueTextSize(11f)
    data.setValueTextColor(Color.WHITE)
    pieChart.data = data

    // undo all highlights
    pieChart.highlightValues(null)
    pieChart.invalidate()
}
