package com.srids.tagit.helper;

import com.srids.tagit.model.CategoryTag;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "tagIt";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tagItDBManager";

    // Table Names
    private static final String TABLE_ROWDATA = "rowdatas";
    private static final String TABLE_TAG = "tags";
    private static final String TABLE_ROWDATA_TAG = "rowdatas_tags";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // NOTES Table - column nmaes
    private static final String KEY_ROWDATA = "rowdata";
    private static final String KEY_TYPE = "type";
    private static final String KEY_INFOPATH = "infopath";
    private static final String KEY_DATA1 = "important";
    private static final String KEY_DATA2 = "data2";
    private static final String KEY_DATA3 = "data3";
    private static final String KEY_DATA4 = "data4";
    private static final String KEY_DATA5 = "data5";

    // TAGS Table - column names
    private static final String KEY_TAG_NAME = "tag_name";

    // NOTE_TAGS Table - column names
    private static final String KEY_ROWDATA_ID = "rowdata_id";
    private static final String KEY_TAG_ID = "tag_id";

    // Table Create Statements
    private static final String CREATE_TABLE_ROWDATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ROWDATA + "(" + KEY_ID + " INTEGER PRIMARY KEY autoincrement,"
            + KEY_ROWDATA + " TEXT,"
            + KEY_INFOPATH + " TEXT,"
            + KEY_DATA1 + " TEXT,"
            + KEY_DATA2 + " TEXT,"
            + KEY_DATA3 + " TEXT,"
            + KEY_DATA4 + " TEXT,"
            + KEY_DATA5 + " TEXT,"
            + KEY_TYPE + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    // CategoryTag table create statement
    private static final String CREATE_TABLE_TAG = "CREATE TABLE IF NOT EXISTS " + TABLE_TAG
            + "(" + KEY_ID + " INTEGER PRIMARY KEY autoincrement," + KEY_TAG_NAME + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    // rowdata_tag table create statement
    private static final String CREATE_TABLE_ROWDATA_TAG = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ROWDATA_TAG + "(" + KEY_ID + " INTEGER PRIMARY KEY autoincrement,"
            + KEY_ROWDATA_ID + " INTEGER," + KEY_TAG_ID + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    Context context;
    private static DatabaseHelper sInstance = null;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

     public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
         //Toast.makeText(context, "DB path: " + context.getDatabasePath(DATABASE_NAME), Toast.LENGTH_LONG ).show();
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_ROWDATA);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_ROWDATA_TAG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROWDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROWDATA_TAG);

        // create new tables
        onCreate(db);
    }

    // ------------------------ "rowdatas" table methods ----------------//

    /**
     * Creating a rowdata
     */
    public long createRowData(RowData rowdata, long[] tag_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ROWDATA, rowdata.getPath());
        values.put(KEY_INFOPATH, rowdata.getInfoPath());
        values.put(KEY_TYPE, rowdata.getType());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long rowdata_id = db.insert(TABLE_ROWDATA, null, values);
        rowdata.setId((int) rowdata_id);

        // insert tag_ids
        for (long tag_id : tag_ids) {
            createRowDataTag(rowdata_id, tag_id);
        }

        return rowdata_id;
    }

    /**
     * get single rowdata
     */
    public RowData getRowData(long rowdata_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ROWDATA + " WHERE "
                + KEY_ID + " = " + rowdata_id;


        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        RowData td = new RowData();
        td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        td.setType(c.getInt(c.getColumnIndex(KEY_TYPE)));
        td.setPath((c.getString(c.getColumnIndex(KEY_ROWDATA))));
        td.setInfoNote((c.getString(c.getColumnIndex(KEY_INFOPATH))));
        td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
        return td;
    }

    /**
     * getting all rowdatas
     * */
    public List<RowData> getAllRowDatas() {
        List<RowData> rowdatas = new ArrayList<RowData>();
        String selectQuery = "SELECT  * FROM " + TABLE_ROWDATA;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                RowData td = new RowData();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setType(c.getInt(c.getColumnIndex(KEY_TYPE)));
                td.setPath((c.getString(c.getColumnIndex(KEY_ROWDATA))));
                td.setInfoNote((c.getString(c.getColumnIndex(KEY_INFOPATH))));
                td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to rowdata list
                rowdatas.add(td);
            } while (c.moveToNext());
        }
        return rowdatas;
    }

    /**
     * getting all rowdatas under single tag
     * */
    public List<RowData> getAllRowDatasByCategoryTag(String tag_name) {
        List<RowData> rowdatas = new ArrayList<RowData>();

        String selectQuery = "SELECT  * FROM " + TABLE_ROWDATA + " td, "
                + TABLE_TAG + " tg, " + TABLE_ROWDATA_TAG + " tt WHERE tg."
                + KEY_TAG_NAME + " = '" + tag_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_ROWDATA_ID;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                RowData td = new RowData();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setType(c.getInt(c.getColumnIndex(KEY_TYPE)));
                td.setPath((c.getString(c.getColumnIndex(KEY_ROWDATA))));
                td.setInfoNote((c.getString(c.getColumnIndex(KEY_INFOPATH))));
                td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to rowdata list
                rowdatas.add(td);
            } while (c.moveToNext());
        }
        return rowdatas;
    }

    public RowData getRowDataId(String tag_name, RowData rowdata) {

        String selectQuery = "SELECT  * FROM " + TABLE_ROWDATA + " td, "
                + TABLE_TAG + " tg, " + TABLE_ROWDATA_TAG + " tt WHERE tg."
                + KEY_TAG_NAME + " = '" + tag_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_ROWDATA_ID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                if(rowdata.getPath().equals(c.getString(c.getColumnIndex(KEY_ROWDATA)))) {
                    rowdata.setId(c.getColumnIndex(KEY_ID));
                    rowdata.setType(c.getInt(c.getColumnIndex(KEY_TYPE)));
                    rowdata.setInfoNote(c.getString(c.getColumnIndex(KEY_INFOPATH)));
                    rowdata.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
                    break;
                }
            } while (c.moveToNext());
        }
        return rowdata;
    }
    /**
     * getting rowdata count
     */
    public int getRowDataCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ROWDATA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /**
     * Updating a rowdata
     */
    public int updateRowData(RowData rowdata) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ROWDATA, rowdata.getPath());
        values.put(KEY_INFOPATH, rowdata.getInfoPath());
        values.put(KEY_TYPE, rowdata.getType());

        // updating row
        return db.update(TABLE_ROWDATA, values, KEY_ID + " = ?",
                new String[] { String.valueOf(rowdata.getId()) });
    }

    public int getRowDataCountByCategoryTag(String tag_name) {
        String selectQuery = "SELECT  * FROM " + TABLE_ROWDATA + " td, "
                + TABLE_TAG + " tg, " + TABLE_ROWDATA_TAG + " tt WHERE tg."
                + KEY_TAG_NAME + " = '" + tag_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_ROWDATA_ID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int count = c.getCount();
        return count;
    }
    /**
     * Deleting a rowdata
     */
    public void deleteRowData(long todo_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int ret = db.delete(TABLE_ROWDATA, KEY_ID + " = ?",
                new String[]{String.valueOf(todo_id)});


    }

    // ------------------------ "tags" table methods ----------------//

    /**
     * Creating tag
     */
    public long createCategoryTag(CategoryTag tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TAG_NAME, tag.getTagName());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long tag_id = db.insert(TABLE_TAG, null, values);
        tag.setId((int) tag_id);
        return tag_id;
    }

    public String[] getAllCategoryTagNames() {
        String[] tagNames = null;
        int count = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_TAG;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        tagNames = new String[c.getCount()];
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                tagNames[count] = c.getString(c.getColumnIndex(KEY_TAG_NAME));
                count++;
            } while (c.moveToNext());
        }
        return tagNames;
    }
    /**
     * getting all tags
     * */
    public List<CategoryTag> getAllCategoryTags() {
        List<CategoryTag> tags = new ArrayList<CategoryTag>();
        String selectQuery = "SELECT  * FROM " + TABLE_TAG;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                CategoryTag t = new CategoryTag();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setTagName(c.getString(c.getColumnIndex(KEY_TAG_NAME)));

                // adding to tags list
                tags.add(t);
            } while (c.moveToNext());
        }
        return tags;
    }

    public CategoryTag getCategoryTagId(CategoryTag tag) {
        String selectQuery = "SELECT  * FROM " + TABLE_TAG;
        boolean found = false;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndex(KEY_TAG_NAME)).equals(tag.getTagName())) {
                    tag.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                    found = true;
                    break;
                }
            } while (c.moveToNext());
        }
        if(found) {
            return tag;
        } else {
            tag.setId(-1);
            return tag;
        }
    }

    /**
     * Updating a tag
     */
    public int updateCategoryTag(CategoryTag tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TAG_NAME, tag.getTagName());

        // updating row
        return db.update(TABLE_TAG, values, KEY_ID + " = ?",
                new String[] { String.valueOf(tag.getId()) });
    }

    /**
     * Deleting a tag
     */
    public void deleteCategoryTag(CategoryTag tag, boolean should_delete_all_tag_rowdatas) {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting tag
        // check if rowdatas under this tag should also be deleted
        if (should_delete_all_tag_rowdatas) {
            // get all rowdatas under this tag
            List<RowData> allTagToDos = getAllRowDatasByCategoryTag(tag.getTagName());

            // delete all rowdatas
            for (RowData rowdata : allTagToDos) {
                // delete rowdata
                deleteRowData(rowdata.getId());
            }
        }

        // now delete the tag
        db.delete(TABLE_TAG, KEY_ID + " = ?",
                new String[]{String.valueOf(tag.getId())});
    }

    public void deleteAllRowdataOfTag(CategoryTag tag, boolean should_delete_all_tag_rowdatas) {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting tag
        // check if rowdatas under this tag should also be deleted
        if (should_delete_all_tag_rowdatas) {
            // get all rowdatas under this tag
            List<RowData> allTagToDos = getAllRowDatasByCategoryTag(tag.getTagName());

            // delete all rowdatas
            for (RowData rowdata : allTagToDos) {
                // delete rowdata
                deleteRowData(rowdata.getId());
            }
        }
    }

    // ------------------------ "rowdata_tags" table methods ----------------//

    /**
     * Creating rowdata_tag
     */
    public long createRowDataTag(long rowdata_id, long tag_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ROWDATA_ID, rowdata_id);
        values.put(KEY_TAG_ID, tag_id);
        values.put(KEY_CREATED_AT, getDateTime());
        long id = db.insert(TABLE_ROWDATA_TAG, null, values);
        return id;
    }

    public void getRowDataTagId(long rowdata_id, long tag_id) {
        String selectQuery = "SELECT  * FROM " + TABLE_ROWDATA_TAG;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                if(tag_id == c.getInt(c.getColumnIndex(KEY_TAG_ID)) && (rowdata_id == c.getInt(c.getColumnIndex(KEY_ROWDATA_ID)))) {
                    break;
                }
            }while (c.moveToNext());
        }
    }

    /**
     * Updating a rowdata tag
     */
    public int updateRowData_Tag(long id, long tag_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TAG_ID, tag_id);

        // updating row
        return db.update(TABLE_ROWDATA_TAG, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /**
     * Deleting a rowdata tag
     */
    public void deleteRowData_Tag(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROWDATA_TAG, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}