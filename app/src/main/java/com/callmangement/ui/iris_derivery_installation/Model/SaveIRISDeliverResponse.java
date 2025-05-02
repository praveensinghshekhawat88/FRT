package com.callmangement.ui.iris_derivery_installation.Model;

public class SaveIRISDeliverResponse {

    public SaveIRISResponse Response;
    public String status;

    public SaveIRISResponse getResponse() {
        return Response;
    }

    public void setResponse(SaveIRISResponse response) {
        Response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class SaveIRISResponse {

        public String TicketNo;
        public String Message;
        public boolean status;

        public SaveIRISResponse(String ticketNo, String message, boolean status) {
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


}
