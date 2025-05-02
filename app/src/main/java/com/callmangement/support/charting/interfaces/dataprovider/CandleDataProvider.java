package com.callmangement.support.charting.interfaces.dataprovider;

import com.callmangement.support.charting.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
