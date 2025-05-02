package com.callmangement.ui.iris_derivery_installation.Model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ReplacementTypesResponse implements Serializable{
    @SerializedName("ReplacementTypes_List")
    public ArrayList<ReplacementTypesList> replacementTypes_List;
    public String message;
    public String status;

    public ArrayList<ReplacementTypesList> getReplacementTypes_List() {
        return replacementTypes_List;
    }

    public void setReplacementTypes_List(ArrayList<ReplacementTypesList> replacementTypes_List) {
        this.replacementTypes_List = replacementTypes_List;
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

    public class ReplacementTypesList implements Serializable {
        public boolean status;
        public String createdBy;
        public String replaceType;
        public String replaceTypeDesc;
        public boolean isActive;
        public String replaceTypeId;
        public String createdOn;
        public String updatedOn;
        public String updatedBy;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getReplaceType() {
            return replaceType;
        }

        public void setReplaceType(String replaceType) {
            this.replaceType = replaceType;
        }

        public String getReplaceTypeDesc() {
            return replaceTypeDesc;
        }

        public void setReplaceTypeDesc(String replaceTypeDesc) {
            this.replaceTypeDesc = replaceTypeDesc;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public String getReplaceTypeId() {
            return replaceTypeId;
        }

        public void setReplaceTypeId(String replaceTypeId) {
            this.replaceTypeId = replaceTypeId;
        }

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        public String getUpdatedOn() {
            return updatedOn;
        }

        public void setUpdatedOn(String updatedOn) {
            this.updatedOn = updatedOn;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }
    }
}


