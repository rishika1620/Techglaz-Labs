package com.example.radha.techglaz;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.viewHolder> {

    ArrayList<ViewPagerItem> viewPagerItemArrayList;
    Context context;
    FragmentManager fragmentManager;

    public ViewPagerAdapter(ArrayList<ViewPagerItem> viewPagerItemArrayList,Context context,FragmentManager fragmentManager) {
        this.viewPagerItemArrayList = viewPagerItemArrayList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_with_text,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ViewPagerItem viewPagerItem = viewPagerItemArrayList.get(position);

        holder.imageView.setImageResource(viewPagerItem.imgId);
        holder.title.setText(viewPagerItem.title);
        holder.description.setText(viewPagerItem.description);
        holder.buttonRegisterCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;

                    // Create the new fragment instance
                    RegisterCourseFragment registerCourseFragment = new RegisterCourseFragment();

                    Log.d("Fragment","Opening Register Course");
                    // Perform fragment transaction
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, registerCourseFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return viewPagerItemArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title;
        TextView description;
        Button buttonRegisterCourse;

        public viewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.our_services_img);
            title = view.findViewById(R.id.our_services_title);
            description = view.findViewById(R.id.ourservices_description);
            buttonRegisterCourse  = view.findViewById(R.id.rc_register);

        }
    }
}
