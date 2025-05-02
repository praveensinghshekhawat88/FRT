package com.callmangement.support.charting.interfaces.dataprovider;

import com.callmangement.support.charting.components.YAxis.AxisDependency;
import com.callmangement.support.charting.data.BarLineScatterCandleBubbleData;
import com.callmangement.support.charting.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
