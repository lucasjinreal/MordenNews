package com.ouman.mordennews.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouman.mordennews.R;
import com.ouman.mordennews.models.HotNewsModel;

import java.util.List;

/**
 * Created by GeekSpace on 2016/12/7.
 */

public class HotRecyclerViewAdapter extends RecyclerView.Adapter<HotRecyclerViewAdapter.HotViewHolder> {


    private Context context;
    private List<HotNewsModel> data;
    public HotRecyclerViewAdapter(Context context, List<HotNewsModel> data){
        this.context = context;
        this.data = data;
    }

    public HotRecyclerViewAdapter.HotViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.hot_recyclerview_item, null);
        return new HotViewHolder(view);
    }

    public void onBindViewHolder(HotRecyclerViewAdapter.HotViewHolder holder, int position){
        String title = data.get(position).getTitle();
        String imageUrl = data.get(position).getImages();
        String date = data.get(position).getDate();

        holder.tv_title.setText(title);
        holder.tv_date.setText(date);
    }

    public int getItemCount(){
        return data.size();
    }

    class HotViewHolder extends ViewHolder{
        TextView tv_title;
        TextView tv_date;
        ImageView news_image;
        public HotViewHolder(View view){
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            news_image = (ImageView) view.findViewById(R.id.news_image);

        }

    }
}
