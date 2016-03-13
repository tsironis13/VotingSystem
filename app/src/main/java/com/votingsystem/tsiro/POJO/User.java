package com.votingsystem.tsiro.POJO;

/**
 * Created by user on 9/11/2015.
 */
public class User {

    private int id;
    private String username, email, password, error_msg;
    private boolean error;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getError_msg() { return error_msg; }

    public void setError_msg(String error_msg) { this.error_msg = error_msg; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
