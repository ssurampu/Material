package com.srids.tagit;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by surams on 6/28/2015.
 */
public class MyPrefs {

    public static final String MY_PREF = "MyPreferences";
    public static final String NAV_DRAWER_LIST = "NavDrawerList";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String nwlist[] = null;

    Context context;
    public static MyPrefs prefsInstance = null;

    public static synchronized MyPrefs getInstance(Context context) {
        if(prefsInstance == null) {
            prefsInstance = new MyPrefs(context.getApplicationContext());
        }

        prefsInstance.sharedPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        prefsInstance.editor = prefsInstance.sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(MyGlobals.DEFAULT_CATEGORY);
        prefsInstance.editor.putString(NAV_DRAWER_LIST, jsonArray.toString());
        prefsInstance.editor.commit();

        return prefsInstance;
    }

    private MyPrefs(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(MyGlobals.DEFAULT_CATEGORY);
        this.editor.putString(NAV_DRAWER_LIST, jsonArray.toString());
        this.editor.putBoolean(MyGlobals.SHOW_HIDE_LOCATION, true);
        this.editor.commit();
    }
    public String getString(int position) {
        return nwlist[position];
    }
    public String[] getNWList(){
        JSONArray jsonArray2 = null;
        try {
            jsonArray2 = new JSONArray(sharedPreferences.getString(NAV_DRAWER_LIST, "[]"));
            nwlist = new String[jsonArray2.length()];
            for (int i = 0; i < jsonArray2.length(); i++) {
                nwlist[i] = jsonArray2.getString(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nwlist;
    }

    public void setBool(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
    }

    public boolean getBool(String key) {
        return this.sharedPreferences.getBoolean(key, false);
    }

    public void set(String key, String value) {

        this.editor.putString(key, value);
        this.editor.commit();
    }

    public String get(String key) {
        return this.sharedPreferences.getString(key, null);
    }

    public void clear(String key) {
        this.editor.remove(key);
        this.editor.commit();
    }

    public void clear() {
        this.editor.clear();
        this.editor.commit();
    }
}
