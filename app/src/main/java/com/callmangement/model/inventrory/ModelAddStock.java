package com.callmangement.model.inventrory;

import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ModelAddStock {
    boolean itemSelectFlag = false;
    String id;
    String itemName;
    String qty;
    List<ModelPartsList> spinnerItemList;
    int spinnerSelectedIndex = 0;
    RelativeLayout relativeLayout;
    boolean flag = false;

    public ModelAddStock(String id, String itemName, String qty, List<ModelPartsList> spinnerItemList) {
        this.id = id;
        this.itemName = itemName;
        this.qty = qty;
        this.spinnerItemList = spinnerItemList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public boolean isItemSelectFlag() {
        return itemSelectFlag;
    }

    public void setItemSelectFlag(boolean itemSelectFlag) {
        this.itemSelectFlag = itemSelectFlag;
    }

    public List<ModelPartsList> getSpinnerItemList() {
        if(spinnerItemList!= null) {
            return spinnerItemList;
        }else {
            return new ArrayList<>();
        }
    }

    public void setSpinnerItemList(List<ModelPartsList> spinnerItemList) {
        this.spinnerItemList = spinnerItemList;
    }

    public int getSpinnerSelectedIndex() {
        return spinnerSelectedIndex;
    }

    public void setSpinnerSelectedIndex(int spinnerSelectedIndex) {
        this.spinnerSelectedIndex = spinnerSelectedIndex;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
