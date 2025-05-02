package com.callmangement.model.WeighingDeliveryDetail;

public class weighingDeliveryImagesDetail {

    public boolean status;
    public String message;
    public int id;
    public String contentType;
    public Object ticketNo;
    public boolean isActive;
    public String contentSize;
    public int photoTypeId;
    public int deliveryId;
    public String imagePath;
    public String imageName;
    public Object uploadedBy;
    public String fileType;
    public Object srNo;
    public String photoType;
    public Object uploadedOn;


    public weighingDeliveryImagesDetail(boolean status, String message, int id, String contentType, Object ticketNo, boolean isActive, String contentSize, int photoTypeId, int deliveryId, String imagePath, String imageName, Object uploadedBy, String fileType, Object srNo, String photoType, Object uploadedOn) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.contentType = contentType;
        this.ticketNo = ticketNo;
        this.isActive = isActive;
        this.contentSize = contentSize;
        this.photoTypeId = photoTypeId;
        this.deliveryId = deliveryId;
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.uploadedBy = uploadedBy;
        this.fileType = fileType;
        this.srNo = srNo;
        this.photoType = photoType;
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

    public Object getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(Object ticketNo) {
        this.ticketNo = ticketNo;
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

    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    public Object getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(Object uploadedOn) {
        this.uploadedOn = uploadedOn;
    }
}
