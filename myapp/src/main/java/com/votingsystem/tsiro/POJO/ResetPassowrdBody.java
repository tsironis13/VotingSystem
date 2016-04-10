package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 10/4/2016.
 */
public class ResetPassowrdBody {

    private String action, email, token;

    public ResetPassowrdBody(String action, String email, String token) {
        this.action =   action;
        this.email  =   email;
        this.token  =   token;
    }

    public String getAction() { return action; }

    public String getEmail() { return email; }

    public String getToken() { return token; }
}
