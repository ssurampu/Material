package com.srids.tagit;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.srids.tagit.adapter.GridListAdapter;
import com.srids.tagit.helper.DatabaseHelper;
import com.srids.tagit.model.CategoryTag;

import java.util.ArrayList;
import java.util.List;

public class AllCategories extends AppCompatActivity {

    View rootViw;
    public static DatabaseHelper db = null;
    public static String[] navTitles = null;
    List<CategoryInfo> list;
    static  MyPrefs prefs = null;
    List<CategoryTag> ctagList = null;
    GridListAdapter lpadapter = null;
    RecyclerView lv = null;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allcategories);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle("All Categories");


        lv = (RecyclerView) findViewById(R.id.categoryList);
        //lv.setHasFixedSize(true);

        lv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        lpadapter = new GridListAdapter(getApplicationContext(), getData());
        lv.setAdapter(lpadapter);
        lpadapter.setOnItemClickListener(new GridListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openCategory(position);
            }
        });

    }

    public void openCategory(int position) {
        Intent intent = new Intent();
        intent.putExtra("CATEGORY_NAME", navTitles[position]);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public List<CategoryInfo> getData() {
        db = DatabaseHelper.getInstance(getApplicationContext());
        navTitles = db.getAllCategoryTagNames();
        list = new ArrayList<CategoryInfo>();
        for (String s : navTitles) {
            CategoryInfo c = new CategoryInfo();
            c.setCategoryName(s);
            c.setCategoryTagCount(db.getRowDataCountByCategoryTag(s));
            list.add(c);
        }
        return list;
    }
}
