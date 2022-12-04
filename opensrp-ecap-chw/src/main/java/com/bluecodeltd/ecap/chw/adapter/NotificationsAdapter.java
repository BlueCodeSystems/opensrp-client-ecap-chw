package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CasePlan;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.model.FamilyServiceModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.smartregister.util.FormUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {


    Context context;
    List<VcaVisitationModel> visits;


    public NotificationsAdapter(List<VcaVisitationModel> visits, Context context){

        super();

        this.visits = visits;
        this.context = context;
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification, parent, false);

        NotificationsAdapter.ViewHolder viewHolder = new NotificationsAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.ViewHolder holder, final int position) {

        final VcaVisitationModel visit = visits.get(position);

        holder.setIsRecyclable(false);

       /* holder.linearLayout.setOnClickListener(v -> {

            Intent i = new Intent(context, IndexDetailsActivity.class);
            i.putExtra("childId",  visit.getUnique_id());
            context.startActivity(i);
        });*/

        String inputDate = visit.getBirthdate().substring(0, 5);
        String yearPart = "-" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String newDate = inputDate.concat(yearPart);

        Date birthday = null;
        try {
            birthday = new SimpleDateFormat("dd-mm-yyyy").parse(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date lastvisit = null;
        try {
            lastvisit = new SimpleDateFormat("dd-mm-yyyy").parse(visit.getVisit_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try{

            Boolean checkDue = compareDates(birthday, lastvisit);

            if(checkDue){

                holder.txtName.setText(visit.getFirst_name() + " " + visit.getLast_name());
                holder.txtBirthdate.setText(visit.getBirthdate());
                holder.txtVisitDate.setText(visit.getVisit_date());

            }

        } catch (NullPointerException e) {

            Log.e("datenullexeption", e.getMessage());
        }
    }

    public Boolean compareDates(Date date1, Date date2)
    {

        if(date1.after(date2)){
         // show notification
            return true;
        }

        return false;

    }



    @Override
    public int getItemCount() {

        return visits.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtName, txtBirthdate, txtVisitDate;

        LinearLayout linearLayout;

        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtName  = itemView.findViewById(R.id.name);
            txtBirthdate = itemView.findViewById(R.id.birthdate);
            txtVisitDate = itemView.findViewById(R.id.visit_date);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
