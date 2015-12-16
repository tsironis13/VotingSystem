package com.votingsystem.tsiro.rest;

/**
 * Created by user on 16/11/2015.
 */
public class RequestBody {

    private String action;

    public RequestBody(String action) {
        this.action = action;
    }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }
}
