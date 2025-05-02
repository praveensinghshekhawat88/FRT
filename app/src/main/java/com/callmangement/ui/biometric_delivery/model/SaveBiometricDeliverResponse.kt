package com.callmangement.ui.biometric_delivery.model;


public class SaveBiometricDeliverResponse {

    public SaveBiometricResponse Response;
    public String status;

    public SaveBiometricResponse getResponse() {
        return Response;
    }

    public void setResponse(SaveBiometricResponse response) {
        Response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class SaveBiometricResponse {

        public String TicketNo;
        public String Message;
        public boolean status;

        public SaveBiometricResponse(String ticketNo, String message, boolean status) {
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

