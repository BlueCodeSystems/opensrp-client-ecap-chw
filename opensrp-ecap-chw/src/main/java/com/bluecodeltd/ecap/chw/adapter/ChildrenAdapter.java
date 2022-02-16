package com.bluecodeltd.ecap.chw.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.Child;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.ViewHolder>{

    Context context;

    List<Child> children;
    String myAge;

    public ChildrenAdapter(List<Child> children, Context context){

        super();

        this.children = children;
        this.context = context;
    }

    @Override
    public ChildrenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_child, parent, false);

        ChildrenAdapter.ViewHolder viewHolder = new ChildrenAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChildrenAdapter.ViewHolder holder, final int position) {

        final Child child = children.get(position);

        holder.fullName.setText(child.getFirst_name() + " " + child.getLast_name());



        String dob = child.getAdolescent_birthdate();

//        String caseStatus = child.getCase_status();
        String caseStatus = IndexPersonDao.getIndexStatus(child.getBaseEntity_id());


        if(caseStatus != null && caseStatus.equals("1")){

            holder.colorView.setBackgroundColor(Color.parseColor("#05b714"));

        } else if (caseStatus != null && caseStatus.equals("0")) {

            holder.colorView.setBackgroundColor(Color.parseColor("#ff0000"));

        } else if(caseStatus != null && caseStatus.equals("2")){

            holder.colorView.setBackgroundColor(Color.parseColor("#ffa500"));
        } else{
            holder.colorView.setBackgroundColor(Color.parseColor("#696969"));
        }



        if(dob != null){

            holder.age.setText("Age : " + getAge(dob));

        } else {

            holder.age.setText("Not Set");

        }

        String memberAge = getAgeWithoutText(dob);

        holder.lview.setOnClickListener(v -> {

            switch (v.getId()) {

                case (R.id.register_columns):

                    String subpop3 = child.getSubpop3();
                    assert subpop3 != null;

                    if(Integer.parseInt(memberAge) < 20){

                        Intent intent = new Intent(context, IndexDetailsActivity.class);
                        intent.putExtra("Child",  child.getUnique_id());
                        context.startActivity(intent);

                    } else {

                        Toasty.warning(context, "Member is not enrolled on the Program", Toast.LENGTH_LONG, true).show();

                    }


                    break;
            }
        });

    }

    private String getAge(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            return periodBetweenDateOfBirthAndNow.getYears() +" Years";
        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){
            return periodBetweenDateOfBirthAndNow.getMonths() +" Months ";
        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return periodBetweenDateOfBirthAndNow.getDays() +" Days ";
        }
        else return "Not Set";
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


    @Override
    public int getItemCount() {

        return children.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView fullName, age;
        View colorView;
        RelativeLayout lview;

        public ViewHolder(View itemView) {

            super(itemView);


            fullName  = (TextView) itemView.findViewById(R.id.familyNameTextView);
            age  = (TextView) itemView.findViewById(R.id.child_age);
            lview = (RelativeLayout) itemView.findViewById(R.id.register_columns);
            colorView = itemView.findViewById(R.id.mycolor);

        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }
    }
}
