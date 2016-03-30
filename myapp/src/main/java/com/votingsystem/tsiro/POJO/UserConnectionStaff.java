package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by user on 10/1/2016.
 */
public class UserConnectionStaff {

    private String tag, hint, username, password, confirm_password, email, firm_name, firm_code;
    private int code;
    private List<Data> data;

    public UserConnectionStaff() {}

    public void setCode(int code) { this.code = code; }

    public int getCode() { return code; }

    public void setTag(String tag) { this.tag = tag; }

    public String getTag() { return tag; }

    public void setHint(String hint) { this.hint = hint; }

    public String getHint() { return hint; }

    public void setData(List<Data> data) { this.data = data; }

    public List<Data> getData() { return data; }
    /*public String getInputField() { return inputField; }

    public void setInputField(String inputField) { this.inputField = inputField; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) { this.confirm_password = confirm_password; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public String getFirm_name() {
        return firm_name;
    }

    public void setFirm_name(String firm_name) {
        this.firm_name = firm_name;
    }

    public String getFirm_code() {
        return firm_code;
    }

    public void setFirm_code(String firm_code) { this.firm_code = firm_code; }

    public int getError_code() { return error_code; }

    public void setError_code(int error_code) { this.error_code = error_code; }*/

    public class Data {
        private String tag;
        private int code, error_code;

        public Data() {}

        public String getTag() { return tag; }

        public void setTag(String tag) { this.tag = tag; }

        public int getCode() { return code; }

        public void setCode(int code) { this.code = code; }

        public int getError_code() { return error_code; }

        public void setError_code(int error_code) { this.error_code = error_code; }
    }
}
