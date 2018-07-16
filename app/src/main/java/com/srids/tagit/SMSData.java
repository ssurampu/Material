package com.srids.tagit;

/**
 * Created by surams on 11/1/2015.
 */
public class SMSData {

    // Number from witch the sms was send
    private String number;
    // SMS text body
    private String body;

    private String id;

    private String thread_id;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getThread_id() {
        return this.thread_id;
    }
}
