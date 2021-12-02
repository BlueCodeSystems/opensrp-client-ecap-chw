package com.bluecodeltd.ecap.chw.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.model.Child;

import java.util.Calendar;
import java.util.List;

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


        if(dob != null){

            String[] items1 = dob.split("-");
            String date1 = items1[0];
            String month = items1[1];
            String year = items1[2];

            myAge = getAge(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date1));

            holder.age.setText("Age : " + myAge);

        } else {

            holder.age.setText("Not Set");

        }

        holder.lview.setOnClickListener(v -> {

            switch (v.getId()) {

                case (R.id.register_columns):

                  /*Intent intent = new Intent(context, IndexDetailsActivity.class);
                    intent.putExtra("clients",  children.get(position));
                    context.startActivity(intent);*/
                    break;
            }
        });

    }

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    @Override
    public int getItemCount() {

        return children.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView fullName, age;
        RelativeLayout lview;

        public ViewHolder(View itemView) {

            super(itemView);


            fullName  = (TextView) itemView.findViewById(R.id.familyNameTextView);
            age  = (TextView) itemView.findViewById(R.id.child_age);
            lview = (RelativeLayout) itemView.findViewById(R.id.register_columns);

        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }
    }
}
