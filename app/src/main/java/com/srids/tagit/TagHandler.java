package com.srids.tagit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srids.tagit.helper.DatabaseHelper;
import com.srids.tagit.helper.RowData;
import com.srids.tagit.model.CategoryTag;
import com.srids.tagit.shareDataInterface;

import java.util.List;

/**
 * Created by surams on 11/5/2015.
 */
public class TagHandler {

    Context context = null;
    public static DatabaseHelper db = null;
    public static String[] navTitles = null;
    private static TagHandler tagHandler = null;
    public String selectedTitle;
    int categoryposition = 0;
    static  MyPrefs prefs = null;

    private TagHandler(Context context) {
        this.context = context;
        // establish data base connection and first read tags list for NAV drawer
        db = DatabaseHelper.getInstance(context);

    }

    public static synchronized TagHandler getInstalce(Context context) {
        if(tagHandler == null) {
            tagHandler = new TagHandler(context.getApplicationContext());
        }
        prefs = MyPrefs.getInstance(context);
        db = DatabaseHelper.getInstance(context.getApplicationContext());
        updateCategoryList(context);
        return tagHandler;
    }

    public static void updateCategoryList(Context context) {
        List<CategoryTag> ctagList = null;
        ctagList = db.getAllCategoryTags();
        if(ctagList.size() <= 0) {
            String[] titles = prefs.getNWList();
            CategoryTag tag = new CategoryTag(titles[0]);
            tag = db.getCategoryTagId(tag);
            if(tag.getId() == -1) {
                db.createCategoryTag(tag);
            }
        }
        ctagList = db.getAllCategoryTags();
        navTitles = new String[ctagList.size()];
        int i = 0;
        for(CategoryTag ct : ctagList) {
            navTitles[i++] = ct.getTagName();
        }
    }

    public static void setNavTitles(String[] navTitles) {
        navTitles = navTitles;
    }

    public static String[] getNavTitles() {
        return navTitles;
    }
    public void addToDB(RowData rowData, String category, final AlertDialogCallback<String> callback) {
        CategoryTag tag = new CategoryTag(category);
        tag = db.getCategoryTagId(tag);
        long path_id = db.createRowData(rowData, new long[]{tag.getId()});
        if(callback != null) {
            callback.onCompleteTagUpdate(category);
        }
    }

    public void getTagSelectionFromDialogBox(final Context context, final RowData rowData, final AlertDialogCallback<String> callback) {
        updateCategoryList(context);
        // If only one category, no need to ask. Update to existing.
        if(navTitles.length == 1) {
            selectedTitle = navTitles[0];
            addToDB(rowData, selectedTitle, callback);
            return;
        }

        // if more than one category, aks use to select one
        AlertDialog.Builder alertDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            alertDialog = new AlertDialog.Builder(context);
        }
        alertDialog.setTitle("Select Category");
        alertDialog.setSingleChoiceItems(navTitles, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int title_position = which;
                selectedTitle = navTitles[which];
                dialog.cancel();
                if (rowData.getInfoPath() != null) {
                    addToDB(rowData, selectedTitle, callback);
                    Toast.makeText(context, "New tag is added to: " + selectedTitle, Toast.LENGTH_LONG).show();
                } else {
                    updateInfo(context, rowData, title_position, selectedTitle, callback);
                }
            }
        });

        AlertDialog ad = alertDialog.create();
        ad.show();

        return;
    }

    public void addTagToCategory(final Context context, final RowData rowData, int categoryPosition, final AlertDialogCallback<String> callback) {

        selectedTitle = navTitles[categoryPosition];
        addToDB(rowData, selectedTitle, callback);
        if(callback != null) {
            callback.onCompleteTagUpdate(navTitles[categoryPosition]);
        }
        return;
    }

    public void updateInfo(final Context context, final RowData rowData, final int position, final String selectedTitle, final AlertDialogCallback<String> callback) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Update info");
        final EditText input = new EditText(context);

        builder.setView(input);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String value = input.getText().toString();
                rowData.setInfoNote(value);
                addToDB(rowData, selectedTitle, callback);
                Toast.makeText(context, "New tag is added to: " + selectedTitle, Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                rowData.setInfoNote(null);
                addToDB(rowData, selectedTitle, callback);
                Toast.makeText(context, "New tag is added to: " + selectedTitle, Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void handleTagUpdate(Context context, RowData rowData, final AlertDialogCallback<String> callback) {

        getTagSelectionFromDialogBox(context, rowData, callback);
    }

    //
    // handling category create new, delete etc..
    //
    public void addCategory(final Context context, final AlertDialogCallback<String> callback){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Enter new category name");
        final EditText input = new EditText(context);
        input.setSingleLine(true);
        builder.setView(input);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String value = input.getText().toString();
                if(value.length() <= 0) {
                    Toast.makeText(context, "Empty Category not allowed.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (istagNamePresentAlready(value)) {
                    Toast.makeText(context, "Category Name: '" + value + "' already present.", Toast.LENGTH_LONG).show();
                    return;
                }
                CategoryTag tag = new CategoryTag(value);
                db.createCategoryTag(tag);
                Toast.makeText(context, "Category: '" + value + "' is added.", Toast.LENGTH_LONG).show();
                updateCategoryList(context);
                callback.updateNavigationDrawer();

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean istagNamePresentAlready(String inputCategory){
        /*If any category is deleted in between, need to update with right navTitles */
        updateCategoryList(this.context);

        for(String cat: navTitles){
            if(cat.equalsIgnoreCase(inputCategory)){
                return true;
            }
        }
        return false;
    }

    public void deleteCategory(final Context context, final AlertDialogCallback<String> callback) {
        AlertDialog.Builder alertDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            alertDialog = new AlertDialog.Builder(context);
        }
        alertDialog.setTitle("Select category to delete");
        alertDialog.setSingleChoiceItems(navTitles, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedTitle = navTitles[which];
                dialog.cancel();
                deleteCategoryAndUpdate(selectedTitle, context, callback);
            }
        });
        AlertDialog ad = alertDialog.create();
        ad.show();
    }

    public void deleteCategoryAndUpdate(final String selectedtag, final  Context context, final AlertDialogCallback<String> callback) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(context);
        }

        if(selectedtag.equals(MyGlobals.DEFAULT_CATEGORY)) {
            builder.setTitle("Delete: " + selectedtag);
            builder.setMessage(MyGlobals.DEFAULT_CATEGORY + " is default category. Only tags of this category will be deleted.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    CategoryTag tag = new CategoryTag(selectedtag);
                    tag = db.getCategoryTagId(tag);
                    db.deleteAllRowdataOfTag(tag, true);
                    callback.updateNavigationDrawer();
                    callback.onCompleteTagUpdate(navTitles[0]);
                    Toast.makeText(context, "All tags of Category: " + selectedtag + " are deleted", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            builder.setTitle("Delete Category '" + selectedtag + "'?");
            builder.setMessage("All tags of this Category will be deleted");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    CategoryTag tag = new CategoryTag(selectedtag);
                    tag = db.getCategoryTagId(tag);
                    db.deleteCategoryTag(tag, true);
                    callback.updateNavigationDrawer();
                    callback.onCompleteTagUpdate(navTitles[0]);
                    Toast.makeText(context, "Category: " + selectedtag + " is deleted", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.show();
    }


    public void addUpdateDataTag(final Context context, String titleString, final AlertDialogCallback<String> callback)
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.addnotes_dialog, null);

        final AlertDialog.Builder alertDialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            alertDialogBuilder = new AlertDialog.Builder(context);
        }
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle(titleString);

        final int maxLen = MyGlobals.ADD_NOTES_MAX_CHARS;
        final int numberOfLines = MyGlobals.ADD_NOTES_MAX_LINES;

        final EditText userInput = (EditText) promptsView.findViewById(R.id.addnotes);
        final EditText notesTitle = (EditText) promptsView.findViewById(R.id.notes_info);
        final TextView subText = (TextView) promptsView.findViewById(R.id.notes_subtitle);

        final Spinner spinnerDropDown = (Spinner) promptsView.findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item ,navTitles);
        spinnerDropDown.setAdapter(adapter);
        spinnerDropDown.setSelection(categoryposition);

        spinnerDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryposition = spinnerDropDown.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                categoryposition = 0;
            }
        });

        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
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

        userInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String text = ((EditText) v).getText().toString();
                    int editTextRowCount = text.split("\\n").length;
                    int editTextLineCount = ((EditText) v).getLineCount();
                    if (editTextLineCount >= numberOfLines) {
                        return true;
                    }
                }

                return false;
            }
        });

        Button btn_ok = (Button) promptsView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                RowData rowData = new RowData(userInput.getText().toString(), notesTitle.getText().toString(), MyGlobals.DATA_TYPE_TEXT);
                //createTagAndUpdate(rowData);
                tagHandler.addTagToCategory(context, rowData, categoryposition, callback);
                categoryposition = 0;
                //updateShowListFragment(navTitles[categoryposition]);
                return;
            }
        });

        Button btn_cancel = (Button) promptsView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }
}
