package com.srids.tagit.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.srids.tagit.MyGlobals;
import com.srids.tagit.R;
import com.srids.tagit.RoundedImageView;
import com.srids.tagit.Utils;
import com.srids.tagit.helper.RowData;

import java.io.File;
import java.util.List;

public class ListPathsAdapter extends BaseAdapter {
	private Activity activity;
    private LayoutInflater inflater;
    List<RowData> rowDataList;
    boolean showLocation = true;
    private int lastPosition = -1;
    private Animation rotate_forward,rotate_backward;
    RoundedImageView roundedImageView = null;
    TextView episode = null;
    public ListPathsAdapter(Activity activity, List<RowData> rowDataList, boolean showLocation) {
        this.activity = activity;
        this.showLocation = showLocation;
        this.rowDataList = rowDataList;
    }
 
    @Override
    public int getCount() {
    	//return pathList.length;
        return rowDataList.size();
    }
 
    @Override
    public Object getItem(int location) {
    	return location;
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setRoundedImageViewId(int id) {
        roundedImageView.setImageDrawable(ContextCompat.getDrawable(activity.getApplicationContext(), id));
    }

    public void setEpisodeColorId(int id) {
        episode.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), id));
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String toshow = null;

        if (inflater == null) {
        	inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row, null);
        }

        Animation animation = AnimationUtils.loadAnimation(this.activity.getApplicationContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView fpView = (TextView) convertView.findViewById(R.id.filename);
        final TextView subtitle = (TextView) convertView.findViewById(R.id.subtitle);
        episode = (TextView) convertView.findViewById(R.id.episode);
        subtitle.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.infoColor));

        final int type = rowDataList.get(position).getType();
        String info = rowDataList.get(position).getInfoPath();
        String content = rowDataList.get(position).getPath();


        rotate_forward = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.rotate_backward);
        roundedImageView = (RoundedImageView) convertView.findViewById(R.id.thumbnail);

        if(type == MyGlobals.DATA_TYPE_PATH) {
            File f = new File(content);
            if(f.exists() == true) {
                int extensionloc = content.lastIndexOf(".");
                String extension = content.substring(extensionloc);
                if (extension.toLowerCase().equals(".jpg") || extension.toLowerCase().equals(".jpeg") || extension.toLowerCase().equals(".png")) {
                  roundedImageView.setImageBitmap(decodeSampledBitmapFromResource(content, 70, 70));
                }else if(Utils.isVideoFile(content, extension)) {
                    //roundedImageView.setImageDrawable(activity.getApplicationContext().getResources().getDrawable(R.mipmap.ic_movie_black_24dp));
                    setRoundedImageViewId(R.mipmap.ic_movie_black_24dp);
                }else if(Utils.isAudioFile(content, extension)) {
                    setRoundedImageViewId(R.mipmap.ic_audiotrack_black_24dp);
                }else if (Utils.isLink(content, extension)) {
                    setRoundedImageViewId(R.mipmap.ic_link_black_24dp);
                } else if (extension.toLowerCase().equals(".pdf")){
                    setRoundedImageViewId(R.mipmap.ic_file_pdf_black_24dp);
                }else if (extension.toLowerCase().equals(".xml")){
                    setRoundedImageViewId(R.mipmap.ic_file_xml_black_24dp);
                }else if (extension.toLowerCase().equals(".doc") || extension.toLowerCase().equals(".docx")|| extension.toLowerCase().equals(".txt")){
                    setRoundedImageViewId(R.mipmap.ic_file_document_black_24dp);
                }
            }
        }else if (type == MyGlobals.DATA_TYPE_SMSREF) {
            setRoundedImageViewId(R.mipmap.ic_message_black_24dp);
        }else if (type == MyGlobals.DATA_TYPE_CONTACT) {
            setRoundedImageViewId(R.mipmap.ic_phone_black_24dp);
        }else if(type == MyGlobals.DATA_TYPE_LINK){
            setRoundedImageViewId(R.mipmap.ic_link_black_24dp);
        }else if (type == MyGlobals.DATA_TYPE_TEXT || type == MyGlobals.DATA_TYPE_SMSTEXT) {
            setRoundedImageViewId(R.mipmap.ic_file_document_black_24dp);
        }


        roundedImageView.startAnimation(rotate_backward);
        subtitle.setVisibility(View.GONE);
        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != MyGlobals.DATA_TYPE_SMSREF) {
                    if (subtitle.getVisibility() == View.VISIBLE) {
                        roundedImageView.startAnimation(rotate_backward);
                        subtitle.setVisibility(View.GONE);
                    } else {
                        roundedImageView.startAnimation(rotate_forward);
                        subtitle.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if(info != null) {
            toshow = MyGlobals.LISTPATH_INFO + info;
            title.setText(toshow);
        }else {
            title.setText(MyGlobals.LISTPATH_INFO);
        }

        String filePath = content.substring(content.lastIndexOf("/") + 1);

        subtitle.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.fileName));
        if(type == MyGlobals.DATA_TYPE_PATH) {
            toshow = Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_NAME + "</b>") + filePath;
            fpView.setText(toshow);
            subtitle.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.textColor));
            if (showLocation) {
                toshow = Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_LOCATION +"</b>") + content;
                subtitle.setText(toshow);
            } else {
                subtitle.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_LOCATION_HIDDEN +"</b>"));
            }

        } else if(type == MyGlobals.DATA_TYPE_LINK) {
            fpView.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_TYPE_URL + "</b>"));
            subtitle.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.linkColor));
            if (showLocation) {
                toshow = Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_LINK + "</b>") + content;
                subtitle.setText(toshow);
            } else {
                subtitle.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_LINK_HIDDEN + "</b>"));
            }
        } else if(type == MyGlobals.DATA_TYPE_TEXT || type == MyGlobals.DATA_TYPE_SMSTEXT){
            fpView.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_TYPE_DATA + "</b>"));
            if (showLocation) {
                subtitle.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.textColor));
                String tstring = content;
                if(tstring.length() > 20) {
                    String t = tstring.substring(0, 20);
                    toshow = Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_DATA + "</b>") + t + " ...";
                    subtitle.setText(toshow);
                }else {
                    toshow = Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_DATA  + "</b>") + content;
                    subtitle.setText(toshow);
                }
            }else {
                subtitle.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.textColor));
                subtitle.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_DATA_HIDDEN + "</b>"));
            }
        } else if(type == MyGlobals.DATA_TYPE_SMSREF) {
            fpView.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_TYPE_SMSREF + "</b>"));
        } else if(type == MyGlobals.DATA_TYPE_CONTACT) {
            fpView.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_TYPE_CONTACT + "</b>"));
            subtitle.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.textColor));
            subtitle.setText(Html.fromHtml("<b>" + content + "</b>"));
        }

        episode.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.linkColor));
        episode.setText("#" + (position + 1));

        return convertView;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(res, options);
    }
}
