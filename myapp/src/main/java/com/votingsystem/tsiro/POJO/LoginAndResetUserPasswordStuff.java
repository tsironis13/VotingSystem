package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 10/4/2016.
 */
public class LoginAndResetUserPasswordStuff {

    private int code;
    private String hint, retry_in;

    public LoginAndResetUserPasswordStuff() {}

    public String getHint() { return hint; }

    public void setHint(String hint) { this.hint = hint; }

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getRetry_in() { return retry_in; }

    public void setRetry_in_sec(String retry_in) { this.retry_in = retry_in; }
}
