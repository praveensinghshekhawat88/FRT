package com.callmangement.ui.iris_derivery_installation.Model;

public class IrisInstalledImagesDtl {

    public boolean status;
    public String message;
    public int id;
    public String contentType;
    public String photoType;
    public Object uploadedBy;
    public Object srNo;
    public boolean isActive;
    public String contentSize;
    public Object ticketNo;
    public String fileType;
    public int photoTypeId;
    public int deliveryId;
    public String imageName;
    public String imagePath;
    public Object uploadedOn;


    public IrisInstalledImagesDtl(boolean status, String message, int id, String contentType, String photoType, Object uploadedBy, Object srNo, boolean isActive, String contentSize, Object ticketNo, String fileType, int photoTypeId, int deliveryId, String imageName, String imagePath, Object uploadedOn) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.contentType = contentType;
        this.photoType = photoType;
        this.uploadedBy = uploadedBy;
        this.srNo = srNo;
        this.isActive = isActive;
        this.contentSize = contentSize;
        this.ticketNo = ticketNo;
        this.fileType = fileType;
        this.photoTypeId = photoTypeId;
        this.deliveryId = deliveryId;
        this.imageName = imageName;
        this.imagePath = imagePath;
        this.uploadedOn = uploadedOn;
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

    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    public Object getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Object uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Object getSrNo() {
        return srNo;
    }

    public void setSrNo(Object srNo) {
        this.srNo = srNo;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getContentSize() {
        return contentSize;
    }

    public void setContentSize(String contentSize) {
        this.contentSize = contentSize;
    }

    public Object getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(Object ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getPhotoTypeId() {
        return photoTypeId;
    }

    public void setPhotoTypeId(int photoTypeId) {
        this.photoTypeId = photoTypeId;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Object getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(Object uploadedOn) {
        this.uploadedOn = uploadedOn;
    }
}
