package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 17/4/2016.
 */
public class LoginFormBody {

    private String action, username, password;

    public LoginFormBody(String action, String username, String password) {
        this.action     =   action;
        this.username   =   username;
        this.password   =   password;
    }

    public String getAction() { return action; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }
}
