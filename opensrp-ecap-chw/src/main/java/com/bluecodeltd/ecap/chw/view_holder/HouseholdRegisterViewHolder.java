package com.bluecodeltd.ecap.chw.view_holder;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;

import java.util.List;

public class HouseholdRegisterViewHolder extends RecyclerView.ViewHolder{

    private TextView familyNameTextView;

    private TextView villageTextView;

    LinearLayout hLayout;

    public HouseholdRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        hLayout = itemView.findViewById(R.id.child_wrapper);
    }

    public void setupViews(String family, String village, List<String> genderList, Context context){
        familyNameTextView.setText(family);
        villageTextView.setText(village);

        for(int i=0; i < genderList.size(); i++) {

            ImageView image = new ImageView(context);


            LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.gravity = Gravity.CENTER;
            params.width = 55;
            params.height = 55;
            image.setLayoutParams(params);

            if (genderList.get(i).equals("male")){

                image.setImageResource(R.mipmap.ic_boy_child);

            } else if(genderList.get(i).equals("female")) {

                image.setImageResource(R.mipmap.ic_girl_child);

            }

            hLayout.addView(image);

        }
    }
}
