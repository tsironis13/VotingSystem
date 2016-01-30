package com.votingsystem.tsiro.helperClasses;

/**
 * Created by user on 10/1/2016.
 */
public class FirmNameWithID {

    private String firm_name;
    private int id;

    public FirmNameWithID(String firm_name, int id) {
        this.firm_name  = firm_name;
        this.id         = id;
    }

    public String getFirm_name() { return firm_name; }

    public void setFirm_name(String firm_name) { this.firm_name = firm_name; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return firm_name;
    }
}
