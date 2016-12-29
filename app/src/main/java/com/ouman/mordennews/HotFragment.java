package com.ouman.mordennews;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ouman.mordennews.adapters.HotRecyclerViewAdapter;
import com.ouman.mordennews.models.HotNewsModel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_SHORT;

public class HotFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private HotRecyclerViewAdapter adapter;
    private ArrayList<HotNewsModel> newsArray;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String LAST_UPDATE;

    public HotFragment() {
    }

    public static HotFragment newInstance(String param1, String param2) {
        HotFragment fragment = new HotFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("日报");
        View rootView = inflater.inflate(R.layout.fragment_hot, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.hot_recyclerview);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //fragment的布局代码在这里写吧应该是,下面的代码中好几处要用到getActivity，很显然要创建了activity之后才能获得

        //1. initdata
        //2. set refresh tool because http thread gonner user it
        //3. set data changed and apply it to recycler view
        //4. init data model, http thread must at the last because it must waite
        //UI load finish

        //首先定义一个标志位，并初始化
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("DateToday", "2017");
        editor.commit();

        newsArray = new ArrayList<>();
        //add swipe refresh on recycler view
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeLayout_hot_recylcerview);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_red_light,
                android.R.color.holo_blue_light, android.R.color.holo_purple);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("xia la shuaxin le!!=====<<<<<");
                new  Thread(getHttpThread).start();
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recyclerview_spacing);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        adapter = new HotRecyclerViewAdapter(getActivity(), newsArray);
        recyclerView.setAdapter(adapter);

        //call http get thread
        new  Thread(getHttpThread).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
        private int space;
        public SpaceItemDecoration(int space) {
            this.space = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }

    private  Thread getHttpThread = new Thread(){
        public void run(){
            //In here we solve http request to get data
            swipeRefreshLayout.setRefreshing(true);
            HttpURLConnection connection = null;
            try {
                String urlAPI = "http://news-at.zhihu.com/api/4/news/latest";
                URL url = new URL(urlAPI);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == 200){
                    InputStream is = connection.getInputStream();
                    String result = IOUtils.toString(is);
                    System.out.println(result);
                    is.close();
                    JSONObject jsonObject = new JSONObject(result);
                    //每次请求，都获取一下日期，对比本地的LAST_UPDATE标志位
                    LAST_UPDATE = jsonObject.getString("date");
                    System.out.println(LAST_UPDATE);
                    //read data local
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String localDateToday = preferences.getString("DateToday", null);
                    if (localDateToday.equals(LAST_UPDATE)){
                        //send handler message do not update
                        Message msg = Message.obtain();
                        msg.what = 1;
                        getHttpHandler.sendMessage(msg);
                    }else{
                        JSONArray jArray = jsonObject.getJSONArray("stories");
                        for (int i=0; i<jArray.length(); i++){
                            try {
                                JSONObject dataObject = jArray.getJSONObject(i);
                                String title = dataObject.getString("title");

                                ArrayList<String> imagesUrl = new ArrayList();
                                JSONArray images = dataObject.getJSONArray("images");
                                System.out.println(images.length());
                                //!!!!这里是整个显示的核心部分，images解析到JSONArray，然后JSONArray遍历到ArrayList
                                //但是注意，这里从JSONArray中提取元素一定要是字符串！！！！
                                for (int j=0; j<images.length();j++){
                                    imagesUrl.add(images.getString(j));
                                    System.out.println("===");
                                }
                                System.out.println(imagesUrl);
                                int id = dataObject.getInt("id");
                                HotNewsModel news = new HotNewsModel();
                                news.setTitle(title);
                                news.setDate(LAST_UPDATE);
                                news.setId(id);
                                news.setImages(imagesUrl);
                                newsArray.add(0, news);

                            } catch (JSONException e){
                            }
                        }
                        if (!newsArray.isEmpty()){
                            Message msg = Message.obtain();
                            msg.what = 0;
                            getHttpHandler.sendMessage(msg);
                        }
                    }

                    // when http get is done, after that ,save the date ,next time compare it
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("DateToday", LAST_UPDATE);
                    editor.commit();

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

    private Handler getHttpHandler = new Handler(){
        public void handleMessage(Message msg){
            //如果是0则表示需要刷新数据，如果是1表示今天的数据已经刷新过了
            if (msg.what == 0){
                //add data stop refresher and send to adapter data
                //changed
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }else if (msg.what == 1){
                Toast.makeText(getActivity(), "Already up-to-date!", LENGTH_SHORT).show();
            }
        }
    };


}

//这里用到了一个本地标志位 DateToday，存储上次刷新使得日期


