package com.srids.tagit;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import com.srids.tagit.adapter.RecyclerListAdaper;
import com.srids.tagit.helper.DatabaseHelper;
import com.srids.tagit.helper.RowData;
import com.srids.tagit.model.CategoryTag;
import com.srids.tagit.adapter.RecyclerListAdaper.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowListFragment extends Fragment {

    public View rootView;
    String[] pathList = null;
    String[] pathInfoList = null;
    List<RowData> rowDatasList = null;
    public String title;
    DatabaseHelper db = null;
    public final int CHOOSE_FILE_REQUESTCODE = 0;
    Utils utils = null;
    RecyclerListAdaper lpadapter = null;
    RecyclerView lv = null;
    shareDataInterface sdI = null;
    int categoryposition = -1;

    public boolean showLocation = true;
    public static android.view.ActionMode mActionMode;
    ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // called when the action mode is created; startActionMode() was called
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            // assumes that you have "contexual.xml" menu resources
            inflater.inflate(R.menu.context_menu_list, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // called when the user selects a contextual menu item
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int position = Integer.parseInt(mode.getTag().toString());
            switch (item.getItemId()) {
                case R.id.delete:
                    deleteListEntry(position);
                    updateFragmentWithData();
                    break;
                case R.id.updateinfo:
                    updateInfo(position);
                    break;
                case R.id.moverowdata:
                    moveRowDataToAnotherTag(position);
                    break;
                case R.id.copytoclipboard:
                    copyToClipBoard(position);
                    break;
                case R.id.tagtype:
                    changeTagType(position);
                    break;
                default:
                    break;
            }
            mode.finish();
            return true;
        }

        // called when the user exits the action mode
        public void onDestroyActionMode(ActionMode mode) {
            mode.finish();
        }

    };

    public ShowListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof shareDataInterface){
            sdI = (shareDataInterface) activity;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        title = getArguments().getString("TITLE_STRING");
        showLocation = getArguments().getBoolean("SHOW_LOCATION");

        rootView = inflater.inflate(R.layout.fragment_show_list, container, false);

        CategoryTag tag = new CategoryTag(title);
        db = DatabaseHelper.getInstance(getActivity().getApplicationContext());


        return updateFragmentWithData();
    }


    public void updateRowDataList(){
        rowDatasList = db.getAllRowDatasByCategoryTag(title);
        int i = 0;
        if(rowDatasList.size() == 0) {
           return;
        }
        pathList = new String[rowDatasList.size()];
        pathInfoList = new String[rowDatasList.size()];
        for (RowData rowdata : rowDatasList) {
            pathList[i] = rowdata.getPath();
            pathInfoList[i] = rowdata.getInfoPath();
            i++;
        }
    }
    public  View updateFragmentWithData(){
        updateRowDataList();
       // lpadapter = new ListPathsAdapter(getActivity(), rowDatasList, showLocation);
        //lv = (ListView) rootView.findViewById(R.id.pathsList);
        //lv.setAdapter(lpadapter);
        lpadapter = new RecyclerListAdaper(getActivity(), rowDatasList, showLocation);
        lv = (RecyclerView) rootView.findViewById(R.id.pathsList);
        lv.setAdapter(lpadapter);
        lv.setLayoutManager(new LinearLayoutManager(getActivity()));

        lpadapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int type = rowDatasList.get(position).getType();
                if (type == MyGlobals.DATA_TYPE_TEXT) {
                    showDataContent(pathInfoList[position], pathList[position]);
                } else if (type == MyGlobals.DATA_TYPE_SMSTEXT) {
                    showDataContent(pathInfoList[position], pathList[position]);

                } else if (rowDatasList.get(position).getType() == MyGlobals.DATA_TYPE_SMSREF) {
                    Intent defineIntent = new Intent(Intent.ACTION_VIEW);
                    defineIntent.setData(Uri.parse("content://mms-sms/conversations/" + rowDatasList.get(position).getPath()));
                    startActivity(defineIntent);
                } else if (rowDatasList.get(position).getType() == MyGlobals.DATA_TYPE_CONTACT) {
                    final Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + pathList[position]));
                    if (dialIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(dialIntent);
                    } else {
                        showDataContent(pathInfoList[position], pathList[position]);
                    }
                } else {
                    openFile(position);
                }
                return;
            }

        });

        lpadapter.setOnItemLongClickListener(new RecyclerListAdaper.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(View view, int position) {
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                mActionMode.setTag(position);
                mActionMode.setTitle("Tag#" + (position + 1));
                view.setSelected(true);
                return true;
            }
        });

        /*
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                deleteListEntry(viewHolder.getAdapterPosition());
            }
        });

        itemTouchHelper.attachToRecyclerView(lv);
        */

        /*
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                int type = rowDatasList.get(position).getType();
                if (type == MyGlobals.DATA_TYPE_TEXT) {
                    showDataContent(pathInfoList[position], pathList[position]);
                } else if (type == MyGlobals.DATA_TYPE_SMSTEXT) {
                    showDataContent(pathInfoList[position], pathList[position]);

                } else if (rowDatasList.get(position).getType() == MyGlobals.DATA_TYPE_SMSREF) {
                    Intent defineIntent = new Intent(Intent.ACTION_VIEW);
                    defineIntent.setData(Uri.parse("content://mms-sms/conversations/" + rowDatasList.get(position).getPath()));
                    startActivity(defineIntent);
                } else if (rowDatasList.get(position).getType() == MyGlobals.DATA_TYPE_CONTACT) {
                    final Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + pathList[position]));
                    if (dialIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(dialIntent);
                    } else {
                        showDataContent(pathInfoList[position], pathList[position]);
                    }
                }else {
                    openFile(position);
                }
                return;
            }

        });

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                mActionMode.setTag(position);
                mActionMode.setTitle("Tag#" + (position + 1));
                view.setSelected(true);
                return true;
            }
        });
*/
        /*Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Click on -> to show/hide tag content", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 100, 200);
        toast.show();*/
        //registerForContextMenu(lv);
        return rootView;
    }

    public void showDataContent(String info, String details) {
        Intent intent = new Intent(getActivity(), showDataActivity.class);
        intent.putExtra("INFO", info);
        intent.putExtra("DETAILS", details);
        intent.putExtra("TITLE", title);
        intent.putExtra("SHOWLOCATION", showLocation);
        getActivity().startActivityForResult(intent, MyGlobals.AI_DATA_SHOWN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyGlobals.AI_DATA_SHOWN) {
            if (resultCode == getActivity().RESULT_OK) {
                updateFragmentWithData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mActionMode != null) {
            mActionMode.finish();
        }
    }


    public void openFile(int position) {
        String path = pathList[position];
        Uri uriFromPath = Uri.fromFile(new File(path));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_VIEW);
        utils = new Utils();
        if(Utils.isALink(path) == true) {
            shareIntent.setData(Uri.parse(path));
        } else {
            shareIntent.setDataAndType(uriFromPath, utils.getMimeType(uriFromPath.toString()));
        }
        startActivity(Intent.createChooser(shareIntent, "Select app to open"));
    }

    public void copyToClipBoard(int position) {
        int i = 0;
        String info = null;
        String data = null;
        int type = -1;
        for(RowData rd : rowDatasList){
            if (i == position){
                info = rd.getInfoPath();
                data = rd.getPath();
                type = rd.getType();
                break;
            }
            i++;
        }
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().getApplicationContext().CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText(info, data);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

    public void deleteListEntry(final int position) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setMessage("Delete tag?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CategoryTag tag = new CategoryTag(title);
                tag = db.getCategoryTagId(tag);
                RowData rd  = rowDatasList.get(position);
                db.deleteRowData(rd.getId());
                updateFragmentWithData();
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

    public void changeTagType(final int position) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.updatetagtype_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Change tag type");

        final TextView currentTagType = (TextView) promptsView.findViewById(R.id.currenttagtype);
        final Spinner spinnerDropDown = (Spinner) promptsView.findViewById(R.id.spinnertagtype);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item ,MyGlobals.tagTypes);
        spinnerDropDown.setAdapter(adapter);


        spinnerDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryposition = spinnerDropDown.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        int i = 0;
        int type = 0;
        int index = 0;
        for(RowData rd : rowDatasList){
            if (i == position){
                type = rd.getType();
                if(type == MyGlobals.DATA_TYPE_TEXT || type == MyGlobals.DATA_TYPE_SMSTEXT) {
                    index = 0;
                }else if(type == MyGlobals.DATA_TYPE_PATH) {
                    index = 3;
                }else if (type == MyGlobals.DATA_TYPE_LINK) {
                    index = 2;
                }else if (type == MyGlobals.DATA_TYPE_SMSREF) {
                    index = 1;
                }else if (type == MyGlobals.DATA_TYPE_CONTACT) {
                    index = 4;
                }
                currentTagType.setText(MyGlobals.tagTypes[index]);
                break;
            }
            i++;
        }

        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        Button btn_ok = (Button) promptsView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if(categoryposition < 0) {
                    return;
                }
                int newtype = -1;
                if(categoryposition == 0) {
                    newtype = MyGlobals.DATA_TYPE_TEXT;
                }else if(categoryposition == 1){
                    newtype = MyGlobals.DATA_TYPE_SMSREF;
                }else if(categoryposition == 2) {
                    newtype = MyGlobals.DATA_TYPE_LINK;
                }else if(categoryposition == 3) {
                    newtype = MyGlobals.DATA_TYPE_PATH;
                }else if(categoryposition == 4) {
                    newtype = MyGlobals.DATA_TYPE_CONTACT;
                }
                if(newtype >= 0) {
                    int i = 0;
                    for (RowData rd : rowDatasList) {
                        if (i == position) {
                            rd.setType(newtype);
                            db.updateRowData(rd);
                            break;
                        }
                        i++;
                    }
                    updateFragmentWithData();
                }
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
    public  void updateInfo(final int position) {

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.updatedata_dialog, null);
        final int maxLen = MyGlobals.ADD_NOTES_MAX_CHARS;
        final int numberOfLines = MyGlobals.ADD_NOTES_MAX_LINES;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Update info");

        final EditText userInput = (EditText) promptsView.findViewById(R.id.addnotes);
        final EditText notesTitle = (EditText) promptsView.findViewById(R.id.notes_info);
        final TextView subText = (TextView) promptsView.findViewById(R.id.notes_subtitle);

        subText.setVisibility(View.INVISIBLE);
        int i = 0;
        int type = 0;
        for(RowData rd : rowDatasList){
            if (i == position){
                notesTitle.setText(rd.getInfoPath());
                type = rd.getType();
                userInput.setText(rd.getPath());
                if(type == MyGlobals.DATA_TYPE_SMSTEXT || type == MyGlobals.DATA_TYPE_TEXT ) {
                    subText.setVisibility(View.VISIBLE);
                } else {
                    userInput.setEnabled(false);
                }
                break;
            }
            i++;
        }

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
                String data = userInput.getText().toString();
                String title = notesTitle.getText().toString();
                int i = 0;
                for (RowData rd : rowDatasList) {
                    if (i == position) {
                        rd.setInfoNote(title);
                        rd.setPath(data);
                        db.updateRowData(rd);
                        break;
                    }
                    i++;
                }
                updateFragmentWithData();
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

    public void moveRowDataToAnotherTag(final int position) {
        if(sdI.getnavTitles().length < 2) {
            Toast.makeText(getActivity().getApplicationContext(),"Cannot move. Only one category present. ", Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Select destination Category...");

        // off course we should not show the existing category and show other categories to move
        String[] tTitles = new String[sdI.getnavTitles().length -1];
        int i = 0;
        for (String st: sdI.getnavTitles()) {
            if(title.equalsIgnoreCase(st)){
                continue;
            }else {
                tTitles[i++] = st;
            }
        }
        final String[] Titles = tTitles;

        alertDialog.setSingleChoiceItems(Titles, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int title_position = which;
                String selectedTitle = Titles[which];
                dialog.cancel();
                updateRowDataList();
                RowData rd  = rowDatasList.get(position);
                db.deleteRowData(rd.getId());

                CategoryTag ctag = new CategoryTag(selectedTitle);
                ctag = db.getCategoryTagId(ctag);
                db.createRowData(rd, new long[]{ctag.getId()});
                updateFragmentWithData();
                Toast.makeText(getActivity().getApplicationContext(), "Moved tag #" + position + "from '" + title + "' to '" + selectedTitle + "'",Toast.LENGTH_LONG).show();
                return;
            }
        });
        AlertDialog ad = alertDialog.create();
        ad.show();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.pathsList) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                deleteListEntry(info.position);
                updateFragmentWithData();
                return true;
            case R.id.updateinfo:
                updateInfo(info.position);
                return true;
            case R.id.moverowdata:
                moveRowDataToAnotherTag(info.position);
                return true;
            case R.id.copytoclipboard:
                copyToClipBoard(info.position);
                return true;
            case R.id.tagtype:
                changeTagType(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void closeFAB() {
        if(mActionMode != null) {
            mActionMode.finish();
        }
    }
}