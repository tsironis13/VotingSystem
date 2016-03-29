package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by giannis on 29/3/2016.
 */
public class RegisterFormBody {

    private String action;
    private int firm_id;
    private List<RegisterFormField> fields;

    public RegisterFormBody(String action, List<RegisterFormField> fields, int firm_id) {
        this.action     =   action;
        this.fields     =   fields;
        this.firm_id    =   firm_id;
    }
}
