package com.willowtreeapps.android.shared.test;

public class PhotoItem {

    private String url;
    String title;
    String description;

    public void setDescription(String desc) {
        this.description = desc.replace("<![CDATA[", "").replace("]]>", "");
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }
}
