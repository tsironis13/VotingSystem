package com.votingsystem.tsiro.helperClasses;

/**
 * Created by user on 10/1/2016.
 */
public class CustomSpinnerItem {

    private String name;
    private int id;

    public CustomSpinnerItem(String name, int id) {
        this.name       = name;
        this.id         = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return name;
    }
}
