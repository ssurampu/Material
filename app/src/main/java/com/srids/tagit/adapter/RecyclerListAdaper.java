package com.srids.tagit.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.srids.tagit.MyGlobals;
import com.srids.tagit.R;
import com.srids.tagit.RoundedImageView;
import com.srids.tagit.Utils;
import com.srids.tagit.helper.RowData;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sridhar on 22-12-2014.
 */
public class RecyclerListAdaper extends RecyclerView.Adapter<RecyclerListAdaper.MyViewHolder> {
    List<RowData> rowDataList= Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private boolean showLocation;
    private int lastPosition = -1;
    private Animation rotate_forward,rotate_backward;
    TextView episode = null;
    TextView title;
    TextView fpView;
    TextView subtitle;
    RoundedImageView roundedImageView;
    OnItemClickListener monClickListener;
    OnItemLongClickListener monLongClickListener;
    int type;

    public RecyclerListAdaper(Context context, List<RowData> data, boolean showLocation){
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.rowDataList=data;
        this.showLocation = showLocation;
    }

    public void delete(int position){
        rowDataList.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.cardview_list_row, parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    public void setRoundedImageViewId(int id) {
        roundedImageView.setImageDrawable(ContextCompat.getDrawable(this.context.getApplicationContext(), id));
    }

    public void setEpisodeColorId(int id) {
        episode.setTextColor(ContextCompat.getColor(this.context.getApplicationContext(), id));
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(this.context.getApplicationContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String toshow = null;

        setAnimation(holder.itemView, position);

        lastPosition = position;
        title = holder.title;
        fpView = holder.filename;
        subtitle = holder.subtitle;
        episode = holder.episode;
        roundedImageView = holder.icon;

        subtitle.setTextColor(ContextCompat.getColor(this.context.getApplicationContext(), R.color.infoColor));

        type = rowDataList.get(position).getType();
        String info = rowDataList.get(position).getInfoPath();
        String content = rowDataList.get(position).getPath();


        rotate_forward = AnimationUtils.loadAnimation(this.context.getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this.context.getApplicationContext(),R.anim.rotate_backward);


        if(type == MyGlobals.DATA_TYPE_PATH) {
            File f = new File(content);
            if(f.exists() == true) {
                int extensionloc = content.lastIndexOf(".");
                if(extensionloc >=0) {
                    String extension = content.substring(extensionloc);
                    if (extension.toLowerCase().equals(".jpg") || extension.toLowerCase().equals(".jpeg") || extension.toLowerCase().equals(".png")) {
                        roundedImageView.setImageBitmap(decodeSampledBitmapFromResource(content, 70, 70));
                    } else if (Utils.isVideoFile(content, extension)) {
                        //roundedImageView.setImageDrawable(this.context.getApplicationContext().getResources().getDrawable(R.mipmap.ic_movie_black_24dp));
                        setRoundedImageViewId(R.mipmap.ic_movie_black_24dp);
                    } else if (Utils.isAudioFile(content, extension)) {
                        setRoundedImageViewId(R.mipmap.ic_audiotrack_black_24dp);
                    } else if (Utils.isLink(content, extension)) {
                        setRoundedImageViewId(R.mipmap.ic_link_black_24dp);
                    } else if (extension.toLowerCase().equals(".pdf")) {
                        setRoundedImageViewId(R.mipmap.ic_file_pdf_black_24dp);
                    } else if (extension.toLowerCase().equals(".xml")) {
                        setRoundedImageViewId(R.mipmap.ic_file_xml_black_24dp);
                    } else if (extension.toLowerCase().equals(".doc") || extension.toLowerCase().equals(".docx") || extension.toLowerCase().equals(".txt")) {
                        setRoundedImageViewId(R.mipmap.ic_file_document_black_24dp);
                    }
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

        if(info != null) {
            toshow = MyGlobals.LISTPATH_INFO + info;
            title.setText(toshow);
        }else {
            title.setText(MyGlobals.LISTPATH_INFO);
        }

        String filePath = content.substring(content.lastIndexOf("/") + 1);

        subtitle.setTextColor(ContextCompat.getColor(this.context.getApplicationContext(), R.color.fileName));
        if(type == MyGlobals.DATA_TYPE_PATH) {
            toshow = Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_NAME + "</b>") + filePath;
            fpView.setText(toshow);
            subtitle.setTextColor(ContextCompat.getColor(this.context.getApplicationContext(), R.color.textColor));
            if (showLocation) {
                toshow = Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_LOCATION +"</b>") + content;
                subtitle.setText(toshow);
            } else {
                subtitle.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_LOCATION_HIDDEN +"</b>"));
            }

        } else if(type == MyGlobals.DATA_TYPE_LINK) {
            fpView.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_TYPE_URL + "</b>"));
            subtitle.setTextColor(ContextCompat.getColor(this.context.getApplicationContext(), R.color.linkColor));
            if (showLocation) {
                toshow = Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_LINK + "</b>") + content;
                subtitle.setText(toshow);
            } else {
                subtitle.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_LINK_HIDDEN + "</b>"));
            }
        } else if(type == MyGlobals.DATA_TYPE_TEXT || type == MyGlobals.DATA_TYPE_SMSTEXT){
            fpView.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_TYPE_DATA + "</b>"));
            if (showLocation) {
                subtitle.setTextColor(ContextCompat.getColor(this.context.getApplicationContext(), R.color.textColor));
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
                subtitle.setTextColor(ContextCompat.getColor(this.context.getApplicationContext(), R.color.textColor));
                subtitle.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_DATA_HIDDEN + "</b>"));
            }
        } else if(type == MyGlobals.DATA_TYPE_SMSREF) {
            fpView.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_TYPE_SMSREF + "</b>"));
        } else if(type == MyGlobals.DATA_TYPE_CONTACT) {
            fpView.setText(Html.fromHtml("<b>" + MyGlobals.LISTPATH_ADAPTER_TYPE_CONTACT + "</b>"));
            subtitle.setTextColor(ContextCompat.getColor(this.context.getApplicationContext(), R.color.textColor));
            subtitle.setText(Html.fromHtml("<b>" + content + "</b>"));
        }

        episode.setTextColor(ContextCompat.getColor(this.context.getApplicationContext(), R.color.linkColor));
        episode.setText("#" + (position + 1));

    }
    @Override
    public int getItemCount() {
        return rowDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView subtitle;
        TextView episode;
        TextView filename;

        RoundedImageView icon;
        public MyViewHolder(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            episode = (TextView) itemView.findViewById(R.id.episode);
            filename = (TextView) itemView.findViewById(R.id.filename);
            icon= (RoundedImageView) itemView.findViewById(R.id.thumbnail);
            icon.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (monClickListener != null) {
                        monClickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (monLongClickListener != null) {
                        monLongClickListener.onItemLongClicked(view, getAdapterPosition());
                    }
                    return true;
                }
            });


        }

        @Override
        public void onClick(View view) {
            RowData rowdata = rowDataList.get(getAdapterPosition());
            if (rowdata.getType() != MyGlobals.DATA_TYPE_SMSREF) {
                if (subtitle.getVisibility() == View.VISIBLE) {
                    icon.startAnimation(rotate_backward);
                    subtitle.setVisibility(View.GONE);
                } else {
                    icon.startAnimation(rotate_forward);
                    subtitle.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.monClickListener = mItemClickListener;
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mOnItemLongClickListener) {
        this.monLongClickListener = mOnItemLongClickListener;
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, RowData data) {
        rowDataList.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(RowData data) {
        int position = rowDataList.indexOf(data);
        rowDataList.remove(position);
        notifyItemRemoved(position);
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