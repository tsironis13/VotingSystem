package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 29/3/2016.
 */
public class RegisterFormField {

    private String tag, text, hint;

    public RegisterFormField(String tag, String text, String hint) {
        this.tag    =   tag;
        this.text   =   text;
        this.hint   =   hint;
    }

    public String getTag() { return tag; }

    public String getText() { return text; }

    public String getHint() { return hint; }
}
