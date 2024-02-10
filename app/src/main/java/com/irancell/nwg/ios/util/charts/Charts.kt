package com.irancell.nwg.ios.util.charts

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.util.constants.*
import java.text.DecimalFormat


data class  BarChartModel(val index : Int,val label : String,val value : Float,val color : String)

fun createBarChart(context: Context,barChart: BarChart , title : String, valueList : ArrayList<BarChartModel>) {
    val entries: ArrayList<BarEntry> = ArrayList()
    val labels: ArrayList<String> = arrayListOf()
    val states : ArrayList<Int> = arrayListOf()
    for (i in 0 until valueList.size)
    {
        entries.add(BarEntry(valueList[i].index.toFloat(), valueList[i].value))
        labels.add( valueList[i].label)
        states.add(valueList[i].index)
    }

    var xAxis = barChart.xAxis;
    xAxis.position = XAxis.XAxisPosition.BOTTOM;
    xAxis.setDrawGridLines(false);
    xAxis.textSize = 12f;
    xAxis.axisMinimum = 0f;
    xAxis.granularity = 1f;
    xAxis.spaceMin = 30f
    xAxis.axisMinimum = -0.5f
    xAxis.valueFormatter = MyDecimalValueFormatter()

    val leftAxis = barChart.axisLeft
    leftAxis.setLabelCount(5, false)
    leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
    leftAxis.spaceTop = 20f
    leftAxis.granularity = 1f
    leftAxis.axisMinimum = 0f
//    leftAxis.valueFormatter = MyDecimalValueFormatter()


    val rightAxis = barChart.axisRight
    rightAxis.setDrawGridLines(false)
    rightAxis.setLabelCount(5, false)
    rightAxis.granularity = 1f
    rightAxis.spaceTop = 20f
    rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

    rightAxis.setDrawLabels(false)
//    rightAxis.valueFormatter = MyDecimalValueFormatter()

    barChart.setFitBars(true)
    barChart.animateY(2500)
    val barDataSet = MyBarDataset(entries,title,states)
    barDataSet.setColors(ContextCompat.getColor(context, R.color.Scheduled),
        ContextCompat.getColor(context,R.color.InProgress),
        ContextCompat.getColor(context,R.color.Visited),
        ContextCompat.getColor(context,R.color.Problematic))
    val data = BarData(barDataSet)
    data.setValueTextSize(12f);
    data.setDrawValues(false);
    data.barWidth = 0.6f;
//    data.setValueFormatter(MyDecimalValueFormatter())
    barChart.data = data
    barChart.description.isEnabled = false;
    barChart.invalidate()
}


class MyBarDataset(
    yVals: List<BarEntry?>?,
    label: String?,
    private val states: List<Int>
) :
    BarDataSet(yVals, label) {
    override fun getEntryIndex(e: BarEntry?): Int {
       return super.getEntryIndex(e)
    }

    override fun getColor(index: Int): Int {
         when (states[index]) {
            Scheduled -> {
                return mColors[0]
            }
            InProgress -> {
                return mColors[1]
            }
            Visited -> {
                return mColors[2]
            }
            Problematic -> {
                return mColors[3]
            }

        }
        return mColors[0]
    }
}



fun getTextState(context: Context,state : Int): String
{
    when(state){
        Scheduled->{
            return context.resources.getString(R.string.Scheduled)
        }
        InProgress->{
            return context.resources.getString(R.string.InProgress)

        }
        Visited->{
            return context.resources.getString(R.string.Visited)

        }
        Problematic->{
            return context.resources.getString(R.string.Problematic)

        }
        Reported->{
            return context.resources.getString(R.string.Reported)

        }
    }
    return ""

}
class MyDecimalValueFormatter : ValueFormatter() {
    private val mFormat: DecimalFormat = DecimalFormat("#")

    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value)
    }
}

