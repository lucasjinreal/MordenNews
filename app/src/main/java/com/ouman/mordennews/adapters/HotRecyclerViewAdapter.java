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

import com.bumptech.glide.Glide;
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
        return new HotViewHolder(view);
    }

    public void onBindViewHolder(final HotRecyclerViewAdapter.HotViewHolder holder, final int position){
        String title = data.get(position).getTitle();
        List imageUrlString = data.get(position).getImages();
        String date = data.get(position).getDate();

        Glide.clear(holder.news_image);
        Glide.with(holder.news_image.getContext())
                .load(imageUrlString.get(0))
                .asBitmap()
                .error(R.drawable.bk1)
                .into(holder.news_image);

        holder.tv_title.setText(title);
        holder.tv_date.setText(date);
        holder.hotCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewsDetailActivity.class);
                intent.putExtra("newsId", data.get(position).getId());
                view.getContext().startActivity(intent);
                System.out.println(data.get(position).getId());

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

//    private class GetImageAsyncTask extends AsyncTask<Integer, Integer, Integer> {
//
//        private ImageView imageView;
//        private String imageUrlString;
//        private Bitmap bitmap;
//
//        public GetImageAsyncTask(ImageView imageView, String imageUrlString) {
//            super();
//            this.imageView = imageView;
//            this.imageUrlString = imageUrlString;
//        }
//
//        @Override
//        protected Integer doInBackground(Integer... params) {
//            URL imgUrl = null;
//            try {
//                imgUrl = new URL(imageUrlString);
//                HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
//                conn.setDoInput(true);
//                conn.connect();
//                InputStream is = conn.getInputStream();
//                bitmap = BitmapFactory.decodeStream(is);
//                is.close();
//            } catch (MalformedURLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            imageView.setImageBitmap(bitmap);
//        }
//    }
}
