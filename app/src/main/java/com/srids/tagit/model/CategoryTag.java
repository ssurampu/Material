package com.srids.tagit.model;

/**
 * Created by surams on 7/7/2015.
 */
public class CategoryTag {
    int id;
    String tag_name;

    // constructors
    public CategoryTag() {

    }

    public CategoryTag(String tag_name) {
        this.tag_name = tag_name;
    }

    public CategoryTag(int id, String tag_name) {
        this.id = id;
        this.tag_name = tag_name;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setTagName(String tag_name) {
        this.tag_name = tag_name;
    }

    // getter
    public int getId() {
        return this.id;
    }

    public String getTagName() {
        return this.tag_name;
    }
}
