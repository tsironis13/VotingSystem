package com.votingsystem.tsiro.POJO;

/**
 * Created by giannis on 20/6/2016.
 */
public class AllSurveysBody {

    private String action, type;
    private int user_id, firm_id, limit ,offset;

    public AllSurveysBody(String action, int user_id, int firm_id, String type, int limit, int offset) {
        this.action     =   action;
        this.user_id    =   user_id;
        this.firm_id    =   firm_id;
        this.type       =   type;
        this.limit      =   limit;
        this.offset     =   offset;
    }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public int getFirm_id() { return firm_id; }

    public void setFirm_id(int firm_id) { this.firm_id = firm_id; }

    public int getOffset() { return offset; }
}
