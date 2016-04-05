package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by giannis on 29/3/2016.
 */
public class RegisterFormBody {

    private String action, token;
    private int firm_id;
    private List<RegisterFormField> fields;

    public RegisterFormBody(String action, List<RegisterFormField> fields, int firm_id, String token) {
        this.action     =   action;
        this.fields     =   fields;
        this.firm_id    =   firm_id;
        this.token      =   token;
    }

    public String getAction() { return action; }

    public String getToken() { return token; }

    public int getFirm_id() { return firm_id; }

    public List<RegisterFormField> getFields() { return fields; }
}
