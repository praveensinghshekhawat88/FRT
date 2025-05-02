package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CampDocInfo implements Serializable {

    @SerializedName("documentPath")
    private String documentPath;

    @SerializedName("documentName")
    private String documentName;

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}

