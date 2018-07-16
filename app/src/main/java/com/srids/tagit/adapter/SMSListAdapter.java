package com.srids.tagit.adapter;

/**
 * Created by surams on 11/1/2015.
 */
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.srids.tagit.R;
import com.srids.tagit.SMSData;


/**
 * List adapter for storing SMS data
 *
 * @author itcuties
 *
 */
public class SMSListAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    // List context
    private final Activity activity;
    // List values
    private final List<SMSData> smsList;

    public SMSListAdapter(Activity activity, List<SMSData> smsList) {
        this.activity = activity;
        this.smsList = smsList;
    }

    @Override
    public int getCount() {
        return smsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.smslist_entry, null);
        }

        TextView senderNumber = (TextView) convertView.findViewById(R.id.smsNumber);
        senderNumber.setText(smsList.get(position).getNumber());

        TextView smsText = (TextView) convertView.findViewById(R.id.smsText);
        smsText.setText(smsList.get(position).getBody());

        return convertView;
    }

}