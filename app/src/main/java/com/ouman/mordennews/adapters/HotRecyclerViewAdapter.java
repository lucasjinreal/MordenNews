package com.ouman.mordennews.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ouman.mordennews.MainActivity;
import com.ouman.mordennews.NewsDetailActivity;
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("===>>click!!!!!");
            }
        });

        return new HotViewHolder(view);
    }

    public void onBindViewHolder(final HotRecyclerViewAdapter.HotViewHolder holder, final int position){
        String title = data.get(position).getTitle();
        String imageUrl = data.get(position).getImages();
        String date = data.get(position).getDate();

        holder.tv_title.setText(title);
        holder.tv_date.setText(date);
        holder.hotCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "inside viewholder position = " +
//                        String.valueOf(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), NewsDetailActivity.class);
                view.getContext().startActivity(intent);

            }
        });

    }

    public int getItemCount(){
        return data.size();
    }

    public class HotViewHolder extends ViewHolder{
        TextView tv_title;
        TextView tv_date;
        ImageView news_image;
        CardView hotCardView;
        public HotViewHolder(View view){
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            news_image = (ImageView) view.findViewById(R.id.news_image);
            hotCardView = (CardView) view.findViewById(R.id.hot_recyclerview_cardview);
        }

    }
}
