package com.callmangement.model.WeighingDeliveryDetail;

public class weighingDelieryRoot {


    public weighingDeliveryData Data;
    public String message;
    public String status;


    public weighingDelieryRoot(weighingDeliveryData data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public weighingDeliveryData getData() {
        return Data;
    }

    public void setData(weighingDeliveryData data) {
        Data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
