package com.callmangement.ui.errors.model;

public class GetErrortypesDatumtwo {
    public String errorType;
    public int errorTypeId;


    public GetErrortypesDatumtwo(String errorType, int errorTypeId) {
        this.errorType = errorType;
        this.errorTypeId = errorTypeId;
    }


    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public int getErrorTypeId() {
        return errorTypeId;
    }

    public void setErrorTypeId(int errorTypeId) {
        this.errorTypeId = errorTypeId;
    }
}
