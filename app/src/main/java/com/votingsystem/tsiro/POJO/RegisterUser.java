package com.votingsystem.tsiro.POJO;

/**
 * Created by user on 2/1/2016.
 */
public class RegisterUser {

    String username, password, confirm_password, email, firm_name, firm_code;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirm_name() {
        return firm_name;
    }

    public void setFirm_name(String firm_name) {
        this.firm_name = firm_name;
    }

    public String getFirm_code() {
        return firm_code;
    }

    public void setFirm_code(String firm_code) {
        this.firm_code = firm_code;
    }
}
