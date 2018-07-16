package com.srids.tagit;

/**
 * Created by surams on 7/16/2015.
 */
public class MyGlobals {

    public static String[] supportedActions = {"Tag from QR code", "Tag sms", "Tag new note", "Tag from clipboard", "Take picture and Tag"};
    public static String[] LinksList = {"http://", "https://", "market://"};

    public static String[] tagTypes = {"DATA/TEXT", "SMS", "WEB LINK", "FILE PATH", "CONTACT" };

    public static String[] videoFormats = {".mov", ".mp4", ".m4a", ".aac", ".ts", ".3gp", ".mkv", ".webm"};
    public static String[] audioFormats = {".mp3", ".wav", ".mid", ".ogg", ".mid" };
    public static String[] linkFormats = {".html", ".htm"};

    public static String donotshow_snackbar_listentry_longpress = "DONOTSHOW_SNACKBAR_LISTENTRY_LONGPRESS";

    public static int ACTIVITY_REQUESTER_SMS = 10;
    public static int ACTIVITY_REQUESTER_ALLCATEGORIES = 20;


     public static int ADD_NOTES_MAX_LINES = 12;
     public static int ADD_NOTES_MAX_CHARS = 300;

     public static int   DATA_TYPE_LINK = 0;
     public static int   DATA_TYPE_TEXT = 1;
     public static int   DATA_TYPE_PATH = 2;
     public static int   DATA_TYPE_SMSTEXT = 3;
     public static int   DATA_TYPE_SMSREF = 4;
     public static int   DATA_TYPE_CONTACT = 5;

     public static int   DIALOG_PIN_INPUT = 1;
     public static int   DIALOG_PIN_INPUT_RETRY = 2;
     public static int   DIALOG_PIN_ENABLE = 3;

     public static int DIALOG_ADD_TAG = 4;
     public static int DIALOG_DELETE_TAG = 5;

     public static final String SHOW_HIDE_LOCATION = "ShowHideLocation";
     public static final String HIDE_LOCATION = "Show details";
     public static final String SHOW_LOCATION = "Hide details";
     public static final String DISABLE_PIN_PROTECTION = "Disable pin protection";
     public static final String ENABLE_PIN_PROTECTION = "Enable pin protection";

     public static final String PASSWORD_PROTECT = "PasswordProtect";

     public static final String DEFAULT_CATEGORY = "Default";
    public static final String ALL_CATEGORIES = "All Categories";

     public static final String LISTPATH_INFO = "INFO: ";
     public static final String LISTPATH_ADAPTER_NAME = "NAME:";
     public static final String LISTPATH_ADAPTER_LOCATION = "PATH: ";
     public static final String LISTPATH_ADAPTER_LOCATION_HIDDEN = "PATH:(Preview Disabled)";

     public static final String LISTPATH_ADAPTER_TYPE_URL = "TYPE: URL";
     public static final String LISTPATH_ADAPTER_LINK = "LINK: ";
     public static final String LISTPATH_ADAPTER_LINK_HIDDEN = "LINK:(Preview Disabled)";

     public static final String LISTPATH_ADAPTER_TYPE_DATA = "TYPE: data";
     public static final String LISTPATH_ADAPTER_DATA = "DATA: ";
     public static final String LISTPATH_ADAPTER_DATA_HIDDEN = "DATA:(Preview Disabled)";

     public static final String LISTPATH_ADAPTER_TYPE_SMSREF = "TYPE: sms";
     public static final String LISTPATH_ADAPTER_TYPE_CONTACT = "TYPE: contact";

     public static final int AI_DATA_SHOWN = 5;
     public static final int REQUEST_CODE_QR = 6;
    public static final int REQUEST_CODE_ADDNOTE = 7;

    public static final int FROM_CLIPBOARD = 1;
    public static final int FROM_QRSCAN = 2;
}
