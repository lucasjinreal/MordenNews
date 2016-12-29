package com.ouman.mordennews;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ouman.mordennews.models.HotNewsModel;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class NewsDetailActivity extends AppCompatActivity {

    private int newsId;
    private String title;
    private String body;
    private String imageCopyright;
    private String image;
    private String contentText = "";
    private String authorAvatar;
    private String authorName;
    private String authorBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.news_detail_toolbar);

        Bundle bundle = this.getIntent().getExtras();
        newsId = bundle.getInt("newsId");
        System.out.println(newsId);
        new Thread(getNewsDetailThread).start();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            NewsDetailActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //处理网络请求，获取新闻详情
    private  Thread getNewsDetailThread = new Thread(){
        public void run(){

            HttpURLConnection connection = null;
            try {
                String urlAPI = "http://news-at.zhihu.com/api/4/news/" + newsId;
                URL url = new URL(urlAPI);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == 200){
                    InputStream is = connection.getInputStream();
                    String result = IOUtils.toString(is);
                    System.out.println(result);
                    is.close();

                    JSONObject jsonObject = new JSONObject(result);
                    body = jsonObject.getString("body");
                    title = jsonObject.getString("title");
                    imageCopyright = jsonObject.getString("image_source");
                    image = jsonObject.getString("image");

                    System.out.println(title);
                    if (title != null){
                        Message msg = Message.obtain();
                        msg.what = 0;
                        getNewsDetailHandler.sendMessage(msg);
                    }
                }

            } catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            } finally{
                if (connection != null){
                    connection.disconnect();
                }
            }

        }
    };

    private Handler getNewsDetailHandler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 0){

                Toolbar toolbar = (Toolbar) findViewById(R.id.news_detail_toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle(title);

                ImageView barImageView = (ImageView) findViewById(R.id.tool_bar_imageview);
                Glide.with(NewsDetailActivity.this)
                        .load(image)
                        .into(barImageView);

                //解析网页正文
                try {
                    Document doc = Jsoup.parse(body);
                    //这里解析的话我们要读取所有的content div标签，每个div标签下要读取所有的p标签
                    Elements contents = doc.select("div.content");
                    for (int i=0;i<contents.size();i++){
                        Elements p = contents.get(i).select("p");
                        for (int j=0;j<p.size();j++){
                            contentText = contentText + p.get(j).text() + "\n";
                        }
                    }
                    System.out.println(contentText);

                    //再解析作者信息
                    Elements meta = doc.select("div.meta");
                    authorAvatar = meta.select("img.avatar").attr("src");
                    authorName = meta.select("span.author").text();
                    authorBio = meta.select("span.bio").text();

                } catch (Exception e){
                    e.printStackTrace();
                }

                TextView newsDetailTextView = (TextView) findViewById(R.id.news_detail_textview);
                TextView authorNameTextView = (TextView) findViewById(R.id.author_name);
                TextView authorBioTextView = (TextView) findViewById(R.id.author_bio);
                newsDetailTextView.setText(contentText);
                authorNameTextView.setText(authorName);
                authorBioTextView.setText(authorBio);

                ImageView authorImageView = (ImageView) findViewById(R.id.author_imageview);
                Glide.with(NewsDetailActivity.this)
                        .load(authorAvatar)
                        .into(authorImageView);


            }else {
                Toast.makeText(NewsDetailActivity.this, "加载失败，出现位置错误", LENGTH_SHORT).show();
            }
        }
    };



}
