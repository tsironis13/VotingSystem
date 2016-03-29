package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 29/3/2016.
 */
public class RegisterFormField {

    private String tag, text;

    public RegisterFormField(String tag, String text) {
        this.tag    =   tag;
        this.text   =   text;
    }

    public String getTag() { return tag; }

    public String getText() { return text; }
}
