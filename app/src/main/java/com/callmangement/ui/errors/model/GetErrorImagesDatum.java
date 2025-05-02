package com.callmangement.ui.errors.model;

public class GetErrorImagesDatum {
    public boolean status;
    public String message;
    public int id;
    public String contentType;
    public String contentSize;
    public Object uploadedOn;
    public boolean isActive;
    public int errorId;
    public String errorRegNo;
    public String imagePath;
    public String imageName;
    public Object uploadedBy;
    public String fileType;
    public Object srNo;


    public GetErrorImagesDatum(boolean status, String message, int id, String contentType, String contentSize, Object uploadedOn, boolean isActive, int errorId, String errorRegNo, String imagePath, String imageName, Object uploadedBy, String fileType, Object srNo) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.contentType = contentType;
        this.contentSize = contentSize;
        this.uploadedOn = uploadedOn;
        this.isActive = isActive;
        this.errorId = errorId;
        this.errorRegNo = errorRegNo;
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.uploadedBy = uploadedBy;
        this.fileType = fileType;
        this.srNo = srNo;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentSize() {
        return contentSize;
    }

    public void setContentSize(String contentSize) {
        this.contentSize = contentSize;
    }

    public Object getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(Object uploadedOn) {
        this.uploadedOn = uploadedOn;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public String getErrorRegNo() {
        return errorRegNo;
    }

    public void setErrorRegNo(String errorRegNo) {
        this.errorRegNo = errorRegNo;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Object getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Object uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Object getSrNo() {
        return srNo;
    }

    public void setSrNo(Object srNo) {
        this.srNo = srNo;
    }
}
