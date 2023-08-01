package com.bluecodeltd.ecap.chw.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.HTSlinksModel;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HTSlinksAdapter extends RecyclerView.Adapter<HTSlinksAdapter.View> {
    Context context;

    ArrayList<HTSlinksModel> links;

    public HTSlinksAdapter(Context context, ArrayList<HTSlinksModel> links) {
        this.context = context;
        this.links = links;
    }

    @NonNull
    @Override
    public HTSlinksAdapter.View onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View binder = LayoutInflater.from(parent.getContext()).inflate(R.layout.links_row, parent, false);

        HTSlinksAdapter.View viewHolder = new HTSlinksAdapter.View(binder);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HTSlinksAdapter.View holder, int position) {
        final  HTSlinksModel client = links.get(position);
        holder.clientNameTextView.setText(client.getFirst_name()+" "+ client.getLast_name());
        holder.clientAgeTextView.setText("Age: "+getAgeWithoutText(client.getBirthdate()));
        holder.clientDetails.setOnClickListener(view -> {
            showCustomDialog(client);
        });
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
    @SuppressLint("MissingInflatedId")
    private void showCustomDialog(HTSlinksModel client) {

        android.view.View dialogView = LayoutInflater.from(context).inflate(R.layout.hts_links_details, null);
        final TextView entry_point,hiv_status,date_tested,test_results,date_enrolled_on_ART,initial_art_date;
        LinearLayout  initial_artLayout,enrolled_on_ARTLayout;
        entry_point = dialogView.findViewById(R.id.entry_point);
        hiv_status = dialogView.findViewById(R.id.hiv_status);
        date_tested = dialogView.findViewById(R.id.date_tested);
        test_results = dialogView.findViewById(R.id.test_results);
        date_enrolled_on_ART = dialogView.findViewById(R.id.date_enrolled_on_ART);
        initial_art_date = dialogView.findViewById(R.id.initial_art_date);
        initial_artLayout = dialogView.findViewById(R.id.initial_artLayout);
        enrolled_on_ARTLayout = dialogView.findViewById(R.id.enrolled_on_ARTLayout);

        if (client != null) {
            if(client.getHiv_status().equals("unknown positive") || client.getHiv_result().equals("Newly Tested HIV+")){
                initial_artLayout.setVisibility(android.view.View.VISIBLE);
                enrolled_on_ARTLayout.setVisibility(android.view.View.VISIBLE);
            }
            if (entry_point != null) {
                String entryPointValue = client.getEntry_point();
                entry_point.setText(entryPointValue != null ? entryPointValue : "");
            }

            if (hiv_status != null) {
                String hivStatusValue = client.getHiv_status();
                hiv_status.setText(hivStatusValue != null ? hivStatusValue : "");
            }

            if (date_tested != null) {
                String dateTestedValue = client.getDate_tested();
                date_tested.setText(dateTestedValue != null ? dateTestedValue : "");
            }

            if (test_results != null) {
                String testResultsValue = client.getHiv_result();
                test_results.setText(testResultsValue != null ? testResultsValue : "");
            }

            if (date_enrolled_on_ART != null) {
                String dateEnrolledOnARTValue = client.getArt_date();
                date_enrolled_on_ART.setText(dateEnrolledOnARTValue != null ? dateEnrolledOnARTValue : "");
            }

            if (initial_art_date != null) {
                String initialArtDateValue = client.getArt_date_initiated();
                initial_art_date.setText(initialArtDateValue != null ? initialArtDateValue : "");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Custom title
        TextView title = new TextView(context);
        title.setText(client.getFirst_name()+" "+client.getLast_name()+" details");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        builder.setCustomTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public int getItemCount() {
        return links.size();
    }

    public class View extends RecyclerView.ViewHolder {
        TextView clientNameTextView, clientAgeTextView,clientDetails;
        ImageView editClient;
        public View(@NonNull android.view.View itemView) {
            super(itemView);
            clientNameTextView = itemView.findViewById(R.id.clientNameTextView);
            clientAgeTextView = itemView.findViewById(R.id.clientAgeTextView);
            clientDetails = itemView.findViewById(R.id.details);
            editClient = itemView.findViewById(R.id.edit_client);
        }
    }
}
