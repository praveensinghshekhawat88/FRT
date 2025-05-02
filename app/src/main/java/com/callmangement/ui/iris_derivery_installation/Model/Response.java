package com.callmangement.ui.iris_derivery_installation.Model;

public class Response {
    private String message;
    private String ticketNo;
    private boolean status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String value) {
        this.ticketNo = value;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean value) {
        this.status = value;
    }
}
