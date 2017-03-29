package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 25/3/2017.
 */

public class DashboardBody {

    private String action;
    private int user_id;
    private int firm_id;

    public DashboardBody(String action, int user_id, int firm_id) {
        this.action = action;
        this.user_id = user_id;
        this.firm_id = firm_id;
    }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public int getUserId() { return user_id; }

    public void setUserId(int user_id) { this.user_id = user_id; }

    public int getFirmId() { return firm_id; }

    public void setFirmId(int firm_id) { this.firm_id = firm_id; }
}
