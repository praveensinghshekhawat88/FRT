package com.callmangement.support.charting.interfaces.dataprovider;

import com.callmangement.support.charting.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
