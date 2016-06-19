package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by user on 12/11/2015.
 */
public class Firm {
    private String action;
    private int code;
    private List<FirmElement> firm_element;

    public Firm() {}

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public int getCode() { return code; }

    public void setStatus(int status) { this.code = status; }

    public void setFirm_element() { this.firm_element = firm_element; }

    public List<FirmElement> getFirm_element() { return firm_element; }


    public class FirmElement{
        private int firm_id;
        private String firm_name, city, address;

        public void setFirm_id() { this.firm_id = firm_id; }

        public int getFirm_id() { return this.firm_id; }

        public String getFirm_name() { return firm_name; }

        public void setFirm_name(String firm_name) { this.firm_name = firm_name; }

        @Override
        public String toString() {
            return String.format(" %s ", firm_name);
        }
    }
}
