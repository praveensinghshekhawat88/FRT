package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelReceiveMaterialListDataList implements Serializable {
    @SerializedName("id") private String id;
    @SerializedName("product_name") private String product_name;
    @SerializedName("product_id") private String product_id;
    @SerializedName("district_id") private String district_id;
    @SerializedName("quantity") private String quantity;
    @SerializedName("created_date") private String created_date;
    @SerializedName("updated_date") private String updated_date;
    @SerializedName("status") private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
