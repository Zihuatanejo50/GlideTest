package com.nelson.glidetest.model;

/**
 * Created by Nelson on 2018/1/29.
 */

public class RecyclerItem {

    private String mType;
    private String mAction;

    public RecyclerItem(String type, String action) {
        mType = type;
        mAction = action;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        mAction = action;
    }
}
