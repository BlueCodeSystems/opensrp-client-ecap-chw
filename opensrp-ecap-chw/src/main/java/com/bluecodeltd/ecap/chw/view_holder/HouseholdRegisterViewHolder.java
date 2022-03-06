package com.bluecodeltd.ecap.chw.view_holder;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HouseholdRegisterViewHolder extends RecyclerView.ViewHolder{

    private TextView familyNameTextView;

    private TextView villageTextView;

    private ImageView homeIcon;

    LinearLayout hLayout;

    public HouseholdRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        hLayout = itemView.findViewById(R.id.child_wrapper);
        homeIcon = itemView.findViewById(R.id.home_icon);
    }

    public void setupViews(String family, String village, List<String> genderList, String screened, List<String> birthdateList, Context context){
        familyNameTextView.setText(family);
        villageTextView.setText(village);


        if (screened != null){

            homeIcon.setImageResource(R.mipmap.ic_home_active);
        } else {

            homeIcon.setImageResource(R.mipmap.ic_home);
        }

        //This prevents Duplication of Icons
        hLayout.removeAllViews();


        for(int i=0; i < genderList.size(); i++) {

            String myage = getAgeWithoutText(birthdateList.get(i));
            int age = Integer.parseInt(myage);

            ImageView image = new ImageView(context);

            LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.gravity = Gravity.CENTER;
            params.width = 40;
            params.height = 40;
            image.setLayoutParams(params);

            if (genderList.get(i).equals("male") && age < 20){

                image.setImageResource(R.mipmap.ic_boy_child);

            } else if(genderList.get(i).equals("female") && age < 20) {

                image.setImageResource(R.mipmap.ic_girl_child);

            } else {
                image.setImageResource(R.drawable.ic_person_black_24dp);
                image.setColorFilter(ContextCompat.getColor(context, R.color.dark_grey));
            }

            hLayout.addView(image);

        }
    }

    private String getAgeWithoutText(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            return String.valueOf(periodBetweenDateOfBirthAndNow.getYears());
        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){
            return String.valueOf(periodBetweenDateOfBirthAndNow.getMonths());
        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return String.valueOf(periodBetweenDateOfBirthAndNow.getDays());
        }
        else return "Not Set";
    }
}
