package com.example.radha.techglaz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HmeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HmeFragment extends Fragment {

    ViewPager2 viewPager2;
    ArrayList<ViewPagerItem> viewPagerItemArrayList;

    public HmeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HmeFragment newInstance(String param1, String param2) {
        HmeFragment fragment = new HmeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hme, container, false);
        viewPager2 = view.findViewById(R.id.viewpager);

        int[] images ={R.drawable.aurdino,R.drawable.iot,R.drawable.robotics,R.drawable.embedded_system,R.drawable.java,R.drawable.web_development,R.drawable.app_development};
        String[] titles = {getString(R.string.aurdino_title), getString(R.string.iot_title), getString(R.string.robotics_title),getString(R.string.embeddedSystem_title),getString(R.string.java_title),getString(R.string.webDevelopment_title),getString(R.string.appDevelopment_title)};
        String[] descriptions = {getString(R.string.aurdino_des), getString(R.string.iot_des), getString(R.string.robotics_des),getString(R.string.embeddedSystem_des),getString(R.string.java_des),getString(R.string.webDevelopment_des),getString(R.string.androidDevelopment_des)};

        viewPagerItemArrayList = new ArrayList<>();

        for(int i=0;i<images.length;i++){
            ViewPagerItem viewPagerItem = new ViewPagerItem(images[i],titles[i],descriptions[i]);
            viewPagerItemArrayList.add(viewPagerItem);
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(viewPagerItemArrayList,getContext(),getChildFragmentManager());

        viewPager2.setAdapter(viewPagerAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        return view;
    }
}