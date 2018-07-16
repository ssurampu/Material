package com.srids.tagit;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class showDataActivity extends AppCompatActivity {

    public String title = null;
    public boolean showLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata);

        Toolbar toolbar = (Toolbar)  findViewById(R.id.toolbar);
        toolbar.setTitle("Tag Details");
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        String info = getIntent().getStringExtra("INFO");
        String details = getIntent().getStringExtra("DETAILS");

        title = getIntent().getStringExtra("TITLE");
        showLocation = getIntent().getBooleanExtra("SHOWLOCATION", false);

        TextView infoView = (TextView) findViewById(R.id.textInfo);
        if(info != null) {
            infoView.setText("Tag Content: " + info);
        } else {
            infoView.setText("Tag Content:");
        }
        TextView detailsView = (TextView) findViewById(R.id.textDetails);
        detailsView.setText("Details: \n" + details);

        Intent intent = new Intent();
        intent.putExtra("SHOWLOCATION", showLocation);
        intent.putExtra("TITLE", title);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onStop() {
        super.onStop();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_third, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
