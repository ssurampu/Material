package com.srids.tagit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.srids.tagit.helper.DatabaseHelper;
import com.srids.tagit.helper.RowData;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.FragmentDrawerListener, shareDataInterface, AlertDialogCallback<String> {

    private Toolbar toolbar;
    private NavigationDrawerFragment navigationDrawerFragment;
    public DatabaseHelper db = null;
    public  String[] navTitles = null;
    Utils utils = null;
    private Menu menu;
    public MyPrefs prefs = null;
    public static final int NOTES_RESULT = 2;
    public  static int loggedin = 0;
    public static int categoryIndex = 0;
    TagHandler tagHandler = null;
    private FloatingActionButton fab = null;
    private Fragment fragment;
    Intent activityIntent = null;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private static String mCurrentPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle("tagit");

        utils= new Utils();
        prefs = MyPrefs.getInstance(this);
        if(prefs.getBool(MyGlobals.PASSWORD_PROTECT)) {
            if(loggedin == 0) {
                loggedin = 1;
                utils.showDialog(MainActivity.this, MyGlobals.DIALOG_PIN_INPUT);
            }
        }

        // populate navigation drawer
        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        navigationDrawerFragment.setDrawerListener(this);


        // establish data base connection and first read tags list for NAV drawer
        db = DatabaseHelper.getInstance(getApplicationContext());

        navTitles =navigationDrawerFragment.titles;

        // create tag handler
        tagHandler = TagHandler.getInstalce(getApplicationContext());

        handleFAB();

       // show the default tag list details
        if(savedInstanceState != null) {
            updateShowListFragment(savedInstanceState.getString("CURRENT_CATEGORY"));
        } else {
            // handle incoming actions
            activityIntent = getIntent();
            if(activityIntent != null) {
                String action = activityIntent.getAction();
                String type = activityIntent.getType();

                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if (type.startsWith("text/")) {
                        handleSendText(activityIntent, type); // Handle text being sent
                    } else {
                        handleRest(activityIntent);
                    }
                    //utils.showDialog3(this, "Save Tag", navTitles);
                }
            }
            updateShowListFragment(navTitles[categoryIndex]);
            if(!prefs.getBool(MyGlobals.donotshow_snackbar_listentry_longpress)) {
                Snackbar.make(findViewById(R.id.container_body), "Long press a Tag for more options.",
                        Snackbar.LENGTH_LONG)
                .show();
            }
        }


    }

    public void handleRest(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String realPath;
        if (imageUri != null) {
            realPath = GetPathDetails.getRealPath(getApplicationContext(), imageUri);
            //Toast.makeText(getApplicationContext(),"img path: "+ realPath, Toast.LENGTH_LONG).show();
            RowData rowData = new RowData(realPath, null, MyGlobals.DATA_TYPE_PATH);
            createTagAndUpdate(rowData);
        }
    }
    void handleSendText(Intent intent, String type) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            RowData rowData;
            if(Utils.isALink(sharedText)) {
                rowData = new RowData(sharedText, null, MyGlobals.DATA_TYPE_LINK);
            } else {
                rowData = new RowData(sharedText, null, MyGlobals.DATA_TYPE_TEXT);
            }

            createTagAndUpdate(rowData);
        } else {
            handleRest(intent);
        }
    }

    public void createTagAndUpdate(RowData rowData) {
        tagHandler.handleTagUpdate(MainActivity.this, rowData, this);
        //Toast.makeText(getApplicationContext(), "New tag is added to: " + selectedCategory, Toast.LENGTH_LONG).show();
    }

    public void updateShowListFragment(String category) {
        fragment = new ShowListFragment();
        Bundle args = new Bundle();
        args.putString("TITLE_STRING", category);
        args.putBoolean("SHOW_LOCATION", prefs.getBool(MyGlobals.SHOW_HIDE_LOCATION));
        fragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();

        collapsingToolbarLayout.setTitle(category);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

        MenuItem menuitem = menu.findItem(R.id.action_password);
        if(!prefs.getBool(MyGlobals.PASSWORD_PROTECT)) {
            menuitem.setChecked(false);
        } else {
            menuitem.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_categories:
                showCategories();
                break;
            case R.id.action_add:
                tagHandler.addCategory(MainActivity.this, MainActivity.this);
                break;
            case R.id.action_delete:
                tagHandler.deleteCategory(MainActivity.this, MainActivity.this);
                break;
            case R.id.action_pastfromclipboard:
                handleData(MyGlobals.FROM_CLIPBOARD, null);
                break;
            case R.id.action_addnotes:
                addNotes();
                break;
            case R.id.action_help:
                showHelpActivity();
                break;
            case R.id.rate_app:
                rateThisApp();
                break;
            case R.id.action_password:
                MenuItem menuitem = menu.findItem(R.id.action_password);
                if(menuitem.isChecked()) {
                    disableProtection();
                    menuitem.setChecked(false);
                } else {
                    enableProtection();
                }
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void enableProtection() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }
        builder.setTitle("Enter pin (6 digit numeric only)");
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        builder.setView(input);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String value = input.getText().toString();
                MenuItem menuitem = menu.findItem(R.id.action_password);
                if (value.length() == 6) {
                    if (prefs != null) {
                        prefs.set("PASSWORD", value);
                        prefs.setBool(MyGlobals.PASSWORD_PROTECT, true);
                        menuitem.setChecked(true);
                    }
                } else {
                    if (prefs != null) {
                        prefs.set("PASSWORD", value);
                        prefs.setBool(MyGlobals.PASSWORD_PROTECT, false);
                        menuitem.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Pin should be 6 digit numeric value.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MenuItem menuitem = menu.findItem(R.id.action_password);
                if (prefs.getBool(MyGlobals.PASSWORD_PROTECT)) {
                    menuitem.setChecked(true);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void disableProtection(){
        prefs.setBool(MyGlobals.PASSWORD_PROTECT, false);
        MenuItem menuitem = menu.findItem(R.id.action_password);
        menuitem.setChecked(false);
        Toast.makeText(getApplicationContext(),"Protection is disabled.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if (collapsingToolbarLayout != null) {
            String currentCategory = collapsingToolbarLayout.getTitle().toString();
            if(currentCategory != null) {
                outState.putString("CURRENT_CATEGORY", currentCategory);
            }
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        String title = navigationDrawerFragment.getTitleString(position);
        updateShowListFragment(title);
     }

    @Override
    public void drawerStateChanged(int newState) {
        switch (newState) {
            case DrawerLayout.STATE_DRAGGING:
            case DrawerLayout.STATE_SETTLING:
                ShowListFragment fragment = (ShowListFragment) getSupportFragmentManager().findFragmentById(R.id.container_body);
                if(fragment != null) {
                    fragment.closeFAB();
                }
                break;
            default:
        }

    }

    public void handleData(int dataType, String data1) {
        final String pasteData;
        String dataToUse = null;
        if(dataType == MyGlobals.FROM_CLIPBOARD) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clipdata = clipboard.getPrimaryClip();
            if (clipdata == null) {
                Toast.makeText(getApplicationContext(), "No data in clipboard.", Toast.LENGTH_LONG).show();
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
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
            }else {
                builder = new AlertDialog.Builder(MainActivity.this);
            }
            if(Utils.isALink(pasteData)) {
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
                        createTagAndUpdate(rowData);
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
                        createTagAndUpdate(rowData);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                //Toast.makeText(getApplicationContext(), "Saving data from Clipboard", Toast.LENGTH_LONG).show();
                RowData rowData = new RowData(pasteData, null, MyGlobals.DATA_TYPE_TEXT);
                createTagAndUpdate(rowData);
            }

        }
    }

    public void showHelpActivity() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void addNotes() {
        // show dialog to add notes
        tagHandler.addUpdateDataTag(this, "Add Notes", this);
        //Intent intent = new Intent(this, AddNotes.class);
        //startActivityForResult(intent, NOTES_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTES_RESULT) {
            if (resultCode == RESULT_OK && data != null) {
                String selectedCategory = data.getStringExtra("ADD_NOTES_CATEGORY");
                updateShowListFragment(selectedCategory);
            }
        } else if(requestCode == MyGlobals.AI_DATA_SHOWN){
            if (resultCode == RESULT_OK) {
                String title = data.getStringExtra("TITLE");
                updateShowListFragment(title);
            }
        } else if(requestCode == MyGlobals.REQUEST_CODE_QR) {
            if(data != null) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                if (contents != null  && format != null) {
                    Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG).show();
                    handleData(MyGlobals.FROM_QRSCAN, contents);
                }
            }
        } else if(requestCode == 15 && resultCode == Activity.RESULT_OK) {
            galleryAddPic();
            RowData rowData = new RowData(mCurrentPhotoPath, null, MyGlobals.DATA_TYPE_PATH);
            createTagAndUpdate(rowData);
        }else if(requestCode == MyGlobals.ACTIVITY_REQUESTER_ALLCATEGORIES && resultCode == Activity.RESULT_OK) {
            String categoryName = data.getStringExtra("CATEGORY_NAME");
            updateShowListFragment(categoryName);
        }
    }

    public void galleryAddPic() {
        File f = new File("file:" + mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TAGIT_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(!storageDir.exists() && !storageDir.mkdir())
            return null;
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    public void handleCapturePicture() {
        final int REQUEST_TAKE_PHOTO = 15;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    public void workOnQRCode() {
        try {
            final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, MyGlobals.REQUEST_CODE_QR);
        } catch(ActivityNotFoundException anfe) {
            //Toast.makeText(this,"scanner not found", Toast.LENGTH_LONG).show();
            DialogHelper.showAppdownload(this, "No scanner app installed", "Want to download recommended app?", "Yes", "No");
        }
    }

    public void handleSMSMessages() {
        Intent intent = new Intent(this, SMSActivity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
    }
    public void rateThisApp()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppRater.showRateDialog(MainActivity.this, null);
            }
        });
    }

    public void handleFAB() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                }else {
                    alertDialog = new AlertDialog.Builder(MainActivity.this);
                }
                alertDialog.setSingleChoiceItems(MyGlobals.supportedActions, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        switch (which) {
                            case 0:
                                workOnQRCode();
                                break;
                            case 1:
                                handleSMSMessages();
                                break;
                            case 2:
                                addNotes();
                                break;
                            case 3:
                                handleData(MyGlobals.FROM_CLIPBOARD, null);
                                break;
                            case 4:
                                handleCapturePicture();
                            default:
                                break;
                        }
                    }
                });

                AlertDialog ad = alertDialog.create();
                ad.show();
            }
        });
    }


    @Override
    public String[] getnavTitles() {
        return navTitles;
    }
    @Override
    public boolean isNavDrawerOpen() {
        return navigationDrawerFragment.isVisible();
    }
    @Override
    public void onCompleteTagUpdate(String category) {
        updateShowListFragment(category);
    }

    @Override
    public void updateNavigationDrawer(){
        navigationDrawerFragment.updatenavDrawer();
        navTitles =navigationDrawerFragment.titles;
        if(tagHandler != null) {
            TagHandler.setNavTitles(navTitles);
        }
    }
    public void showCategories() {
        Intent intent = new Intent(this, AllCategories.class);
        startActivityForResult(intent, MyGlobals.ACTIVITY_REQUESTER_ALLCATEGORIES);
    }
}
