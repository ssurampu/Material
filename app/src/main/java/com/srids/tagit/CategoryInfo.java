package com.srids.tagit;

/**
 * Created by surams on 4/11/2016.
 */
public class CategoryInfo {
    String categoryName;
    int categoryTagCount;
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getCategoryName() {
        return  this.categoryName;
    }

    public void setCategoryTagCount(int categoryTagCount){
        this.categoryTagCount = categoryTagCount;
    }

    public int getCategoryTagCount() {
        return this.categoryTagCount;
    }
}
