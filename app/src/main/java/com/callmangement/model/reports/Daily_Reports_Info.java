package com.callmangement.model.reports;

public class Daily_Reports_Info {
    private String date;
    private int not_resolved;
    private int resolved;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNot_resolved() {
        return not_resolved;
    }

    public void setNot_resolved(int not_resolved) {
        this.not_resolved = not_resolved;
    }

    public int getResolved() {
        return resolved;
    }

    public void setResolved(int resolved) {
        this.resolved = resolved;
    }
}
