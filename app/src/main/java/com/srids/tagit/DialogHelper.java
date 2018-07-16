package com.srids.tagit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/**
 * Created by surams on 10/14/2015.
 */
public class DialogHelper {

    //alert dialog for downloadDialog
    public static AlertDialog showAppdownload(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            downloadDialog = new AlertDialog.Builder(act, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            downloadDialog = new AlertDialog.Builder(act);
        }
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                try {
                    act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.google.zxing.client.android")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+"com.google.zxing.client.android")));
                }

            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }
}
