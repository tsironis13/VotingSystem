package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by giannis on 29/3/2016.
 */
public class RegisterFormBody {

    private String action, firm_name;
    private List<RegisterFormField> fields;

    public RegisterFormBody(String action, List<RegisterFormField> fields, String firm_name) {
        this.action     =   action;
        this.fields     =   fields;
        this.firm_name  =   firm_name;
    }
}
