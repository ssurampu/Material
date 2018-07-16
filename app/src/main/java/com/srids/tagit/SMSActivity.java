package com.srids.tagit;

import android.app.AlertDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import  com.srids.tagit.adapter.SMSListAdapter;
import com.srids.tagit.helper.RowData;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by surams on 11/1/2015.
 */
public class SMSActivity extends AppCompatActivity {

    private List<SMSData> smsList = null;
    String smstodo = null;
    long itemselected = -1;
    TagHandler tagHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smshandle);

        Toolbar toolbar = (Toolbar)  findViewById(R.id.toolbar);
        toolbar.setTitle("SMS selector");
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tagHandler = TagHandler.getInstalce(getApplicationContext());

        smsList = new ArrayList<SMSData>();

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c= getContentResolver().query(uri, null, null ,null,null);

        // Read the sms data and store it in the list
        if(c.moveToFirst()) {
            for(int i=0; i < c.getCount(); i++) {
                SMSData sms = new SMSData();
                sms.setId(c.getString(c.getColumnIndexOrThrow("_id")).toString());
                sms.setThread_id(c.getString(c.getColumnIndexOrThrow("thread_id")).toString());
                sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                smsList.add(sms);

                c.moveToNext();
            }
        }
        c.close();

        Toast.makeText(getApplicationContext(), "total size " + smsList.size(), Toast.LENGTH_LONG).show();
        // Set smsList in the ListAdapter
        ListView smsListview = (ListView) findViewById(R.id.smslistView);
        SMSListAdapter smsListAdapter = new SMSListAdapter(this, smsList);
        smsListview.setAdapter(smsListAdapter);

        smsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tagSMS(position);
            }
        });
       // registerForContextMenu(smsListview);


    }

    public void tagSMS(int position) {
        final SMSData smsData = smsList.get(position);
        LayoutInflater li = LayoutInflater.from(SMSActivity.this);
        View promptsView = li.inflate(R.layout.smssaver, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SMSActivity.this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        final TextView textInfo = (TextView) promptsView.findViewById(R.id.smssaver_info);

        final ListView listView = (ListView) promptsView.findViewById(R.id.smssaver_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        String[] choices = getResources().getStringArray(R.array.smssave_chooser);


        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, choices));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                smstodo = listView.getItemAtPosition(position).toString();
                itemselected = position;
                if (position == 0) {

                    textInfo.setText("Reference to sms is captured. Reference tag will be obsolete if sms gets deleted from messages. ");
                } else {
                    textInfo.setText("Content of sms is saved as data.");
                }
            }
        });
        Button btnOk = (Button) promptsView.findViewById(R.id.smssave_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if(itemselected == 0) {
                    //save as tag
                    RowData rowData = new RowData(smsData.getThread_id(), smsData.getNumber(), MyGlobals.DATA_TYPE_SMSREF);
                    tagHandler.getTagSelectionFromDialogBox(SMSActivity.this, rowData, null);

                } else if (itemselected == 1) {
                    //save as data
                    RowData rowData = new RowData(smsData.getBody(), smsData.getNumber(), MyGlobals.DATA_TYPE_SMSTEXT);
                    tagHandler.getTagSelectionFromDialogBox(SMSActivity.this, rowData, null);
                }
                return;
            }
        });

        Button btnCancel = (Button) promptsView.findViewById(R.id.smssave_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                return;
            }
        });
        alertDialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.smslistView) {
            MenuInflater inflater = this.getMenuInflater();// getActivity().getMenuInflater();
            inflater.inflate(R.menu.smslist_entry_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.tagThis:
                //openFile(info.position);
                tagSMS(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
