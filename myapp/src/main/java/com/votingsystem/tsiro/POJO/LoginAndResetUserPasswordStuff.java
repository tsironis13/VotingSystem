package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 10/4/2016.
 */
public class LoginAndResetUserPasswordStuff {

    private int code, user_id, firm_id;
    private String hint, retry_in, username, email;

    public LoginAndResetUserPasswordStuff() {}

    public String getHint() { return hint; }

    public void setHint(String hint) { this.hint = hint; }

    public int getUserId() { return user_id; }

    public void setUserId(int user_id) { this.user_id = user_id; }

    public int getFirmId() { return firm_id; }

    public void setFirmId(int firm_id) { this.firm_id = firm_id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getRetryIn() { return retry_in; }

    public void setRetryInSec(String retry_in) { this.retry_in = retry_in; }
}
