package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.MotherLongitudinalFollowUpModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MotherLongitudinalAdapter extends RecyclerView.Adapter<MotherLongitudinalAdapter.ViewHolder> {

    private final Context context;
    private final List<MotherLongitudinalFollowUpModel> items;
    private ObjectMapper oMapper;

    public MotherLongitudinalAdapter(Context context, List<MotherLongitudinalFollowUpModel> items) {
        this.context = context;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public void setItems(List<MotherLongitudinalFollowUpModel> data) {
        this.items.clear();
        if (data != null) {
            this.items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mother_longitudinal_follow_up_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MotherLongitudinalFollowUpModel visit = items.get(position);
        holder.setIsRecyclable(false);

        String contactNumber = visit.getContact_count_number();
        String dateOfVisit = visit.getLfu_date_of_visit();
        String gaWeeks = visit.getLfu_gestation_weeks();
        String weightKg = visit.getLfu_weight_kg();
        String hivResult = visit.getLfu_hiv_subsequent_test_result();
        String prepStarted = visit.getLfu_started_prep_if_negative();
        String syphilisResult = visit.getLfu_syphilis_test_result();
        String hepbResult = visit.getLfu_hepb_test_result();
        String tbStatus = visit.getLfu_tb_status();
        String specialConditions = visit.getLfu_special_conditions();

        // Header: contact number and date
        if (contactNumber != null && !contactNumber.isEmpty()) {
            holder.txtContactNumber.setText("Contact #" + contactNumber);
        } else {
            holder.txtContactNumber.setText("");
        }

        if (dateOfVisit != null && !dateOfVisit.isEmpty()) {
            holder.txtDateOfVisit.setText("Visit Date: " + dateOfVisit);
        } else {
            holder.txtDateOfVisit.setText("");
        }

        // GA + weight summary
        StringBuilder gaWeightSummary = new StringBuilder();
        if (gaWeeks != null && !gaWeeks.isEmpty()) {
            gaWeightSummary.append("GA: ").append(gaWeeks).append(" weeks");
        }
        if (weightKg != null && !weightKg.isEmpty()) {
            if (gaWeightSummary.length() > 0) gaWeightSummary.append(" • ");
            gaWeightSummary.append("Wt: ").append(weightKg).append(" kg");
        }
        holder.txtGaWeightSummary.setText(gaWeightSummary.toString());

        // HIV / PrEP / Syphilis / Hep B section
        holder.txtHivSubsequentTestResult.setText(hivResult != null ? hivResult : "");
        holder.txtStartedPrepIfNegative.setText(prepStarted != null ? prepStarted : "");
        holder.txtSyphilisTestResult.setText(syphilisResult != null ? syphilisResult : "");
        holder.txtHepbTestResult.setText(hepbResult != null ? hepbResult : "");

        // TB and special conditions
        holder.txtTbStatus.setText(tbStatus != null ? tbStatus : "");
        holder.txtSpecialConditions.setText(specialConditions != null ? specialConditions : "");

        View.OnClickListener listener = v -> openForm(visit);
        holder.container.setOnClickListener(listener);
        holder.btnEdit.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtContactNumber;
        TextView txtDateOfVisit;
        TextView txtGaWeightSummary;
        TextView txtHivSubsequentTestResult;
        TextView txtStartedPrepIfNegative;
        TextView txtSyphilisTestResult;
        TextView txtHepbTestResult;
        TextView txtTbStatus;
        TextView txtSpecialConditions;
        View container;
        View btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.lfu_item_container);
            txtContactNumber = itemView.findViewById(R.id.contact_count_number);
            txtDateOfVisit = itemView.findViewById(R.id.lfu_date_of_visit);
            txtGaWeightSummary = itemView.findViewById(R.id.lfu_ga_weight_summary);
            txtHivSubsequentTestResult = itemView.findViewById(R.id.lfu_hiv_subsequent_test_result);
            txtStartedPrepIfNegative = itemView.findViewById(R.id.lfu_started_prep_if_negative);
            txtSyphilisTestResult = itemView.findViewById(R.id.lfu_syphilis_test_result);
            txtHepbTestResult = itemView.findViewById(R.id.lfu_hepb_test_result);
            txtTbStatus = itemView.findViewById(R.id.lfu_tb_status);
            txtSpecialConditions = itemView.findViewById(R.id.lfu_special_conditions);
            btnEdit = itemView.findViewById(R.id.lfu_edit);
        }
    }

    private void openForm(MotherLongitudinalFollowUpModel visit) {
        try {
            if (oMapper == null) oMapper = new ObjectMapper();
            FormUtils formUtils = new FormUtils(context);
            JSONObject form = formUtils.getFormJson("mother_longitudinal_follow_up");

            if (visit.getBase_entity_id() != null) {
                form.put("entity_id", visit.getBase_entity_id());
            }

            // Prefill all mapped fields from the longitudinal model (including household_id)
            CoreJsonFormUtils.populateJsonForm(form, oMapper.convertValue(visit, Map.class));

            startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startFormActivity(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setName("Mother Longitudinal Follow-up");
        form.setHideSaveLabel(true);
        form.setNextLabel(context.getString(R.string.next));
        form.setPreviousLabel(context.getString(R.string.previous));
        form.setSaveLabel(context.getString(R.string.submit));
        form.setActionBarBackground(org.smartregister.R.color.dark_grey);
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        ((Activity) context).startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
}
