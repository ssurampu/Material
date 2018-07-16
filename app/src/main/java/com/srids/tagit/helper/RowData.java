package com.srids.tagit.helper;

/**
 * Created by surams on 7/7/2015.
 */
public class RowData {
    int id;
    String path;
    int type;
    String created_at;
    String info_path;
    float rating;

    // constructors
    public RowData() {
        this.rating = 0;
    }

    public RowData(String path, int type) {
        this.path = path;
        this.type = type;
        this.info_path = "";
    }

    public RowData(String path, String info, int type) {
        this.path = path;
        this.type = type;
        this.info_path = info;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCreatedAt(String created_at){
        this.created_at = created_at;
    }

    public void setInfoNote(String infopath) {
        this.info_path = infopath;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
    // getters
    public long getId() {
        return this.id;
    }

    public String getPath() {
        return this.path;
    }

    public int getType() {
        return this.type;
    }

    public String getInfoPath(){
        return this.info_path;
    }

    public float getRating() {
        return this.rating;
    }
}
