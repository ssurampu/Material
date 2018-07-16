package com.srids.tagit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.srids.tagit.helper.RowData;

/**
 * Created by surams on 7/20/2015.
 */
public class AddNotes extends AppCompatActivity implements  AlertDialogCallback<String> {

    TagHandler tagHandler = null;
    int categorypostion = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnotes);

        /*
        Toolbar toolbar = (Toolbar)  findViewById(R.id.toolbar);
        toolbar.setTitle("Notes");
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */

        tagHandler = TagHandler.getInstalce(getApplicationContext());

        final EditText userInput = (EditText) findViewById(R.id.addnotes);
        final int maxLen = MyGlobals.ADD_NOTES_MAX_CHARS;
        final int numberOfLines = MyGlobals.ADD_NOTES_MAX_LINES;

        final TextView subText = (TextView) findViewById(R.id.notes_subtitle);
        final TextView notestInfo = (TextView) findViewById(R.id.notes_info);

        final Spinner spinnerDropDown = (Spinner) findViewById(R.id.spinnerCategory);
        final String[] categories = TagHandler.getNavTitles();
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(AddNotes.this,android.R.layout.simple_spinner_dropdown_item ,categories);
        spinnerDropDown.setAdapter(adapter);
        spinnerDropDown.setSelection(categorypostion);

        spinnerDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categorypostion = spinnerDropDown.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                categorypostion = 0;
            }
        });

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
        Button okbtn = (Button) findViewById(R.id.btn_ok);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RowData rowData = new RowData(userInput.getText().toString(), notestInfo.getText().toString(), MyGlobals.DATA_TYPE_TEXT);
                //tagHandler.getTagSelectionFromDialogBox(AddNotes.this, rowData, AddNotes.this);
                tagHandler.addTagToCategory(AddNotes.this, rowData,categorypostion, null);

                Intent intent=new Intent();
                intent.putExtra("ADD_NOTES_CATEGORY",categories[categorypostion]);
                categorypostion = 0;
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        Button cancel = (Button) findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_third, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleteTagUpdate(String category) {
        finish();
    }

    @Override
    public void updateNavigationDrawer() {

    }
}
