package com.srids.tagit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.srids.tagit.CategoryInfo;
import com.srids.tagit.R;
import java.util.Collections;
import java.util.List;

/**
 * Created by surams on 4/11/2016.
 */
public class GridListAdapter extends RecyclerView.Adapter<GridListAdapter.MyViewHolder1> {
    List<CategoryInfo> rowDataList= Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private int lastPosition = -1;
    private Animation rotate_forward,rotate_backward;
    TextView categoryName;
    TextView categoryTagCount;
    OnItemClickListener monClickListener;
    OnItemLongClickListener monLongClickListener;

    public GridListAdapter(Context context, List<CategoryInfo> data){
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.rowDataList=data;
    }

    public void delete(int position){
        rowDataList.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.gridview_allcategories, parent,false);
        MyViewHolder1 holder=new MyViewHolder1(view);
        return holder;
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
    public void onBindViewHolder(MyViewHolder1 holder, int position) {
        //setAnimation(holder.itemView, position);

        lastPosition = position;
        categoryName = holder.categoryName;
        categoryTagCount = holder.categoryTagCount;

        categoryName.setText(this.rowDataList.get(position).getCategoryName());
        categoryTagCount.setText("Tags count: " + this.rowDataList.get(position).getCategoryTagCount());

    }
    @Override
    public int getItemCount() {
        return rowDataList.size();
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView categoryName;
        TextView categoryTagCount;

        public MyViewHolder1(View itemView) {
            super(itemView);
            categoryName= (TextView) itemView.findViewById(R.id.categoryName);
            categoryTagCount = (TextView) itemView.findViewById(R.id.categoryTagCount);

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
    public void insert(int position, CategoryInfo data) {
        rowDataList.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(CategoryInfo data) {
        int position = rowDataList.indexOf(data);
        rowDataList.remove(position);
        notifyItemRemoved(position);
    }
}
