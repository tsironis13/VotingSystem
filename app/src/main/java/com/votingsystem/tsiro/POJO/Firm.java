package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by user on 12/11/2015.
 */
public class Firm {

    private String firm_name, city, address, description, action;
    private boolean error;
    private List<FirmElement> firm_element;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getFirm_name() { return firm_name; }

    public void setFirm_name(String firm_name) { this.firm_name = firm_name; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Boolean getError() { return error; }

    public void setError(Boolean error) { this.error = error; }

    public void setFirm_element() { this.firm_element = firm_element; }

    public List<FirmElement> getFirm_element() { return firm_element; }

    @Override
    public String toString() {
        return String.format(" %s ", firm_name);
    }

    public class FirmElement{
        public String firm_name;

        public String getFirm_name() { return firm_name; }

        public void setFirm_name(String firm_name) { this.firm_name = firm_name; }

        @Override
        public String toString() {
            return String.format(" %s ", firm_name);
        }
    }
}
