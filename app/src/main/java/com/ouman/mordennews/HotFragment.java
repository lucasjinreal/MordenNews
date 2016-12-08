package com.ouman.mordennews;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ouman.mordennews.adapters.HotRecyclerViewAdapter;
import com.ouman.mordennews.models.HotNewsModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HotFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private HotRecyclerViewAdapter adapter;
    private ArrayList<HotNewsModel> newsArray;
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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("最热");
        View rootView = inflater.inflate(R.layout.fragment_hot, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.hot_recyclerview);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //fragment的布局代码在这里写吧应该是,下面的代码中好几处要用到getActivity，很显然要创建了activity之后才能获得
        newsArray = new ArrayList<>();
        for (int i=1; i<30; i++){
            HotNewsModel news = new HotNewsModel();
            news.setTitle("习近平来到中南大学参观");
            news.setDate("20161207");
            news.setImages("0000");
            newsArray.add(news);
        }
        System.out.println("====>>>");
        System.out.println(newsArray);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recyclerview_spacing);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        HotRecyclerViewAdapter adapter = new HotRecyclerViewAdapter(getActivity(), newsArray);
        recyclerView.setAdapter(adapter);

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
}


