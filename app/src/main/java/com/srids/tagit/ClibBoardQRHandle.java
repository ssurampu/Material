package com.srids.tagit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.srids.tagit.helper.RowData;

/**
 * Created by surams on 11/12/2015.
 */
public class ClibBoardQRHandle extends Activity implements AlertDialogCallback<String> {
    public TagHandler tagHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tagHandler = TagHandler.getInstalce(getApplicationContext());
        String handle = getIntent().getStringExtra("HANDLE");
        if(handle.equals("CLIPBOARD") == true) {
            handleData(MyGlobals.FROM_CLIPBOARD, null);
        }
    }

    public void copyToClipBoard(int dataType, String title, String data1) {

    }
    public void handleData(int dataType, String data1) {
        final String pasteData;
        String dataToUse = null;
        if(dataType == MyGlobals.FROM_CLIPBOARD) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clipdata = clipboard.getPrimaryClip();
            if (clipdata == null) {
                Toast.makeText(getApplicationContext(), "No clipboard data selected.", Toast.LENGTH_LONG).show();
                return;
            }
            ClipData.Item item = clipdata.getItemAt(0);

            if (item == null) {
                return;
            }
            dataToUse = item.getText().toString();
        } else if(dataType == MyGlobals.FROM_QRSCAN) {
            dataToUse = data1;
        }

        pasteData = dataToUse;

        if(pasteData != null) {
            //Toast.makeText(getApplicationContext(), "Clip board data: " + pasteData, Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(ClibBoardQRHandle.this, android.R.style.Theme_Material_Light_Dialog_Alert);
            }else {
                builder = new AlertDialog.Builder(ClibBoardQRHandle.this);
            }
            if((pasteData.toLowerCase().startsWith("http://")) || (pasteData.toLowerCase().startsWith("https://"))) {
                if(dataType == MyGlobals.FROM_CLIPBOARD) {
                    builder.setTitle("Paste from clipboard");
                } else if(dataType == MyGlobals.FROM_QRSCAN) {
                    builder.setTitle("Paste from QR Scan");
                }
                builder.setMessage("Data to tag seems like a link. Do you want to save it as URL link or as plain text?");
                builder.setPositiveButton("LINK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "saving as link", Toast.LENGTH_LONG).show();
                        RowData rowData = new RowData(pasteData, null, MyGlobals.DATA_TYPE_LINK);
                        //createTagAndUpdate(rowData);
                        tagHandler.handleTagUpdate(ClibBoardQRHandle.this, rowData, ClibBoardQRHandle.this);
                    }
                });
                builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("DATA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "saving as data", Toast.LENGTH_LONG).show();
                        RowData rowData = new RowData(pasteData, null, MyGlobals.DATA_TYPE_TEXT);
                        //createTagAndUpdate(rowData);
                        tagHandler.handleTagUpdate(ClibBoardQRHandle.this, rowData, ClibBoardQRHandle.this);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                //Toast.makeText(getApplicationContext(), "Saving data from Clipboard", Toast.LENGTH_LONG).show();
                RowData rowData = new RowData(pasteData, null, MyGlobals.DATA_TYPE_TEXT);
                //createTagAndUpdate(rowData);
                tagHandler.handleTagUpdate(ClibBoardQRHandle.this, rowData, ClibBoardQRHandle.this);
            }

        }
        return;
    }

    @Override
    public void onCompleteTagUpdate(String category) {
        finish();
    }

    @Override
    public void updateNavigationDrawer() {

    }
}
