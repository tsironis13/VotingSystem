package com.votingsystem.tsiro.POJO;

import java.util.List;

/**
 * Created by user on 10/1/2016.
 */
public class RegisterUserStuff {

    private String tag, hint;
    private int code;
    private List<Data> data;

    public RegisterUserStuff() {}

    public void setCode(int code) { this.code = code; }

    public int getCode() { return code; }

    public String getTag() { return tag; }

    public void setHint(String hint) { this.hint = hint; }

    public String getHint() { return hint; }

    public void setData(List<Data> data) { this.data = data; }

    public List<Data> getData() { return data; }

    public class Data {
        private String tag;
        private int code, error_code;

        public Data() {}

        public int getCode() { return code; }

        public void setCode(int code) { this.code = code; }
    }
}
