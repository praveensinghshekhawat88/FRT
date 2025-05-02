package com.callmangement.ui.biometric_delivery.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SensorSummaryResponse implements Serializable {

    public ArrayList<Data> Data;
    public String message;
    public String status;

    public ArrayList<Data> getData() {
        return Data;
    }

    public void setData(ArrayList<Data> data) {
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

    public class Data implements Serializable {

        public String total_Distributed_BiometricSensor;
        public String total_Mapped_BiometricSensor;
        public String total_L0_Machine;
        public String total_Pending;
        public String districtId;
        public String district;

        public String getTotal_Distributed_BiometricSensor() {
            return total_Distributed_BiometricSensor;
        }

        public void setTotal_Distributed_BiometricSensor(String total_Distributed_BiometricSensor) {
            this.total_Distributed_BiometricSensor = total_Distributed_BiometricSensor;
        }

        public String getTotal_Mapped_BiometricSensor() {
            return total_Mapped_BiometricSensor;
        }

        public void setTotal_Mapped_BiometricSensor(String total_Mapped_BiometricSensor) {
            this.total_Mapped_BiometricSensor = total_Mapped_BiometricSensor;
        }

        public String getTotal_L0_Machine() {
            return total_L0_Machine;
        }

        public void setTotal_L0_Machine(String total_L0_Machine) {
            this.total_L0_Machine = total_L0_Machine;
        }

        public String getTotal_Pending() {
            return total_Pending;
        }

        public void setTotal_Pending(String total_Pending) {
            this.total_Pending = total_Pending;
        }

        public String getDistrictId() {
            return districtId;
        }

        public void setDistrictId(String districtId) {
            this.districtId = districtId;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }
    }
}
