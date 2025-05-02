package com.callmangement.support.charting.interfaces.dataprovider;

import com.callmangement.support.charting.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
