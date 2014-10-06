package com.wowo.model;

/**
 * Created by tinyao on 10/3/14.
 */
public enum Category {
    all("全部"), nearby("附近"), life("生活"), entertainment("娱乐"), tech("科技"), travel("旅行"), work("工作"), others("其他");
    private String mDisplayName;

    Category(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
