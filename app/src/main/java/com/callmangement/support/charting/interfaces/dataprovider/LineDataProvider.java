package com.callmangement.support.charting.interfaces.dataprovider;

import com.callmangement.support.charting.components.YAxis;
import com.callmangement.support.charting.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
