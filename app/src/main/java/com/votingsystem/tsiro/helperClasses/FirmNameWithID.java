package com.votingsystem.tsiro.helperClasses;

/**
 * Created by user on 10/1/2016.
 */
public class FirmNameWithID {

    private String firm_name;
    private Object id;

    public FirmNameWithID(String firm_name, Object id) {
        this.firm_name  = firm_name;
        this.id         = id;
    }

    public String getFirm_name() { return firm_name; }

    public void setFirm_name(String firm_name) { this.firm_name = firm_name; }

    public Object getId() { return id; }

    public void setId(Object id) { this.id = id; }

    @Override
    public String toString() {
        return firm_name;
    }
}
