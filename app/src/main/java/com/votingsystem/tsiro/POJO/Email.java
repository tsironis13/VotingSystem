package com.votingsystem.tsiro.POJO;

/**
 * Created by user on 8/1/2016.
 */
public class Email {

    private boolean error;
    private int error_code;

    public Email() {}

    public boolean isError() { return error; }

    public void setError(boolean error) { this.error = error; }

    public int getError_code() { return error_code; }

    public void setError_code(int error_code) { this.error_code = error_code; }
}
