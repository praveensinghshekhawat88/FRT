package com.callmangement.ui.ins_weighing_scale.model.challan;

public class challanData {

    public String fileUrl;
    public String fileName;
    public boolean status;
    public String message;

    public challanData(String fileUrl, String fileName, boolean status, String message) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.status = status;
        this.message = message;
    }


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
