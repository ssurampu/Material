package com.srids.tagit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by surams on 7/4/2015.
 */
public class Utils {
    MyPrefs prefs = null;
    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return type;
    }

    public static boolean isALink(String data) {
        return (data.toLowerCase().startsWith(MyGlobals.LinksList[0])) ||
                (data.toLowerCase().startsWith(MyGlobals.LinksList[1])) ||
                (data.toLowerCase().startsWith(MyGlobals.LinksList[2]));
    }
    public void showDialog(final Context context, final int dlid)
    {
        prefs = MyPrefs.getInstance(context);
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Enter pin to unlock");
        final EditText input = new EditText(context);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setTextIsSelectable(false);
        builder.setView(input);
        builder.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String userInput = input.getText().toString();
                if(userInput.equals(prefs.get("PASSWORD"))) {
                    Toast.makeText(context.getApplicationContext(), "Unlock success", Toast.LENGTH_LONG).show();
                    return;
                }

                String message = "The Pin you have entered is incorrect." + " \n \n" + "Please try again!!";
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Error");
                builder.setMessage(message);
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        showDialog(context, dlid);
                    }
                });
                builder.create().show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDialog2(final Context context, String retString)
    {
        prefs = MyPrefs.getInstance(context);
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.addnotes, null);
        final AlertDialog.Builder alertDialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            alertDialogBuilder = new AlertDialog.Builder(context);
        }
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.addnotes);
        final int maxLen = 8096;
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final TextView subText = (TextView) promptsView.findViewById(R.id.notes_subtitle);

        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                subText.setText("remaining " + String.valueOf(maxLen - s.length()) + " of " + maxLen);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                subText.setText("remaining " + String.valueOf(maxLen - s.length()) + " of " + maxLen);
            }

            @Override
            public void afterTextChanged(Editable s) {
                subText.setText("remaining " + String.valueOf(maxLen - s.length()) + " of " + maxLen);
            }
        });
        Button login = (Button) promptsView.findViewById(R.id.btn_ok);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //userInput.getText().toString();

                return;
            }
        });

        Button cancel = (Button) promptsView.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        alertDialog.show();
    }


    public void showDialog3(final Context context, String retString, String[] categoryList)
    {
        prefs = MyPrefs.getInstance(context);
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.savenewtagwithcatselection, null);
        final AlertDialog.Builder alertDialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            alertDialogBuilder = new AlertDialog.Builder(context);
        }
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.taginfo);
        final ListView listView = (ListView) promptsView.findViewById(R.id.categoryList);
        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, categoryList));
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button login = (Button) promptsView.findViewById(R.id.btn_ok);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //userInput.getText().toString();

                return;
            }
        });

        Button cancel = (Button) promptsView.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                System.exit(1);
            }
        });

        alertDialog.show();
    }

    public static boolean isVideoFile(String filepath, String extension) {
        boolean isVideo = false;
        for(int i = 0; i < MyGlobals.videoFormats.length ; i++) {
         if(MyGlobals.videoFormats[i].toLowerCase().equals(extension.toLowerCase())) {
             isVideo = true;
             break;
         }
        }
        return isVideo;
    }

    public static boolean isAudioFile(String filepath, String extension) {
        boolean isAudio = false;
        for(int i = 0; i < MyGlobals.audioFormats.length ; i++) {
            if(MyGlobals.audioFormats[i].toLowerCase().equals(extension.toLowerCase())) {
                isAudio = true;
                break;
            }
        }
        return isAudio;
    }

    public static boolean isLink(String filepath, String extension) {
        boolean isLink = false;
        for(int i = 0; i < MyGlobals.linkFormats.length ; i++) {
            if(MyGlobals.linkFormats[i].toLowerCase().equals(extension.toLowerCase())) {
                isLink = true;
                break;
            }
        }
        return isLink;
    }
}
