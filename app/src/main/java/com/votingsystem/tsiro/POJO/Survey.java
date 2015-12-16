package com.votingsystem.tsiro.POJO;

/**
 * Created by user on 22/11/2015.
 */
public class Survey {

    private String title, description, error;

    public Survey(String title, String description) {
        this.title        = title;
        this.description  = description;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getError() { return error; }

    public void setError(String error) { this.error = error; }
}
