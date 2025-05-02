package com.callmangement.ui.errors.model;

public class UpdateErrorStatusResponse {
    public boolean status;
    public String message;
    public String ErrorRegNo;


    public UpdateErrorStatusResponse(boolean status, String message, String errorRegNo) {
        this.status = status;
        this.message = message;
        ErrorRegNo = errorRegNo;
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

    public String getErrorRegNo() {
        return ErrorRegNo;
    }

    public void setErrorRegNo(String errorRegNo) {
        ErrorRegNo = errorRegNo;
    }
}
