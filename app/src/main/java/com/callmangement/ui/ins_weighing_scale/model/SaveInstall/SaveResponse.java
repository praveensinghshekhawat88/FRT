package com.callmangement.ui.ins_weighing_scale.model.SaveInstall;

public class SaveResponse {

    public String TicketNo;
    public String Message;
    public boolean status;


    public SaveResponse(String ticketNo, String message, boolean status) {
        TicketNo = ticketNo;
        Message = message;
        this.status = status;
    }


    public String getTicketNo() {
        return TicketNo;
    }

    public void setTicketNo(String ticketNo) {
        TicketNo = ticketNo;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
