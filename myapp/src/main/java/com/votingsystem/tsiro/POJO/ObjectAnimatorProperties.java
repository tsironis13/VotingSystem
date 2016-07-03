package com.votingsystem.tsiro.POJO;

import android.view.View;

/**
 * Created by giannis on 2/7/2016.
 */
public class ObjectAnimatorProperties {
    private View view;
    private String valueType, propertyName;
    private int valueFrom, valueTo, duration;

    public ObjectAnimatorProperties() {}

    public ObjectAnimatorProperties(View view, String valueType, String propertyName, int valueFrom, int valueTo) {
        this.view           =   view;
        this.valueType      =   valueType;
        this.propertyName   =   propertyName;
        this.valueFrom      =   valueFrom;
        this.valueTo        =   valueTo;
    }

    public View getView() { return view; }

    public void setView(View view) { this.view = view; }

    public int getValueTo() { return valueTo; }

    public void setValueTo(int valueTo) { this.valueTo = valueTo; }

    public String getPropertyName() { return propertyName; }

    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }

    public int getValueFrom() { return valueFrom; }

    public void setValueFrom(int valueFrom) { this.valueFrom = valueFrom; }

    public String getValueType() { return valueType; }

    public void setValueType(String valueType) { this.valueType = valueType; }
}
