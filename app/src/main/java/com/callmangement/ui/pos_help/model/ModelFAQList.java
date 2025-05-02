package com.callmangement.ui.pos_help.model;

import com.google.gson.annotations.SerializedName;

public class ModelFAQList {
    @SerializedName("id") private String id;
    @SerializedName("question") private String question;
    @SerializedName("answer") private String answer;
    boolean selectFlag = false;
    boolean visibleItemFlag = false;

    public String getId() {
        if (id == null)
            return "0";
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        if (question == null)
            return "";
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        if (answer == null)
            return "";
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }

    public boolean isVisibleItemFlag() {
        return visibleItemFlag;
    }

    public void setVisibleItemFlag(boolean visibleItemFlag) {
        this.visibleItemFlag = visibleItemFlag;
    }
}
