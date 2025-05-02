package com.callmangement.ui.pos_issue.model;

import com.google.gson.annotations.SerializedName;

public class ModelIssuesList {
    @SerializedName("id") private String id;
    @SerializedName("issueName") private String issueName;

    public String getId() {
        if (id == null)
            return "0";
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIssueName() {
        if (issueName == null)
            return "";
        return issueName;
    }

    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }

    @Override
    public String toString() {
        return issueName;
    }
}
