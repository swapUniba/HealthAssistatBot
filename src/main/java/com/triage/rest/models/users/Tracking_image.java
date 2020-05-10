package com.triage.rest.models.users;

import com.triage.rest.enummodels.Status;

public class Tracking_image {
    private String link;
    private Status status;
    private int id;
    private String textextracted;

    public Tracking_image(int id,String link, Status status,String textextracted) {
        this.link = link;
        this.status = status;
        this.id=id;
        this.textextracted=textextracted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public Status getStatus() {
        return status;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTextextracted() {
        return textextracted;
    }

    public void setTextextracted(String textextracted) {
        this.textextracted = textextracted;
    }
}
