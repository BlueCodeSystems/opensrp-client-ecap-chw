package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.MotherPostnatalCareModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.util.FormUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class MotherPostnatalAdapter extends RecyclerView.Adapter<MotherPostnatalAdapter.ViewHolder> {

    private final Context context;
    private final List<MotherPostnatalCareModel> items;
    private ObjectMapper oMapper;

    public MotherPostnatalAdapter(Context context, List<MotherPostnatalCareModel> items) {
        this.context = context;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public void setItems(List<MotherPostnatalCareModel> data) {
        this.items.clear();
        if (data != null) {
            this.items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mother_postnatal_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MotherPostnatalCareModel visit = items.get(position);
        holder.setIsRecyclable(false);

        String date = visit.getLast_interacted_with();
        String visitType = visit.getPnc_visit_type();
        String feeding = visit.getPnc_type_of_feeding();
        String hivTest = visit.getPnc_hiv_test_done();
        String onPrep = visit.getPnc_on_prep();
        String fpCounselling = visit.getPnc_fp_counselling();
        String cervicalScreening = visit.getPnc_cervical_cancer_screening();
        String stiScreening = visit.getPnc_sti_screening();
        String comments = visit.getPnc_comments();

        // Header fields
        if (date != null && !date.isEmpty()) {
            holder.txtDate.setText("Postnatal Date: " + fmt(date));
        } else {
            holder.txtDate.setText("");
        }

        if (visitType != null && !visitType.isEmpty()) {
            holder.txtVisit.setText("PNC Visit: " + visitType);
            holder.txtPncVisitType.setText(visitType);
        } else {
            holder.txtVisit.setText("PNC Visit");
            holder.txtPncVisitType.setText("");
        }

        // Detail fields
        holder.txtPncTypeOfFeeding.setText(feeding != null ? feeding : "");
        holder.txtPncHivTestDone.setText(hivTest != null ? hivTest : "");
        holder.txtPncOnPrep.setText(onPrep != null ? onPrep : "");
        holder.txtPncFpCounselling.setText(fpCounselling != null ? fpCounselling : "");
        holder.txtPncCervicalCancerScreening.setText(cervicalScreening != null ? cervicalScreening : "");
        holder.txtPncStiScreening.setText(stiScreening != null ? stiScreening : "");
        holder.txtPncComments.setText(comments != null ? comments : "");

        // Summary line: quick overview
        StringBuilder summaryBuilder = new StringBuilder();
        if (feeding != null && !feeding.isEmpty()) {
            summaryBuilder.append(feeding);
        }
        if (hivTest != null && !hivTest.isEmpty()) {
            if (summaryBuilder.length() > 0) summaryBuilder.append(" • ");
            summaryBuilder.append("HIV: ").append(hivTest);
        }
        if (fpCounselling != null && !fpCounselling.isEmpty()) {
            if (summaryBuilder.length() > 0) summaryBuilder.append(" • ");
            summaryBuilder.append("FP: ").append(fpCounselling);
        }
        holder.txtSummaryLine.setText(summaryBuilder.toString());

        View.OnClickListener listener = v -> openForm(visit);
        holder.container.setOnClickListener(listener);
        holder.btnEdit.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtVisit;
        TextView txtSummaryLine;
        TextView txtPncVisitType;
        TextView txtPncTypeOfFeeding;
        TextView txtPncHivTestDone;
        TextView txtPncOnPrep;
        TextView txtPncFpCounselling;
        TextView txtPncCervicalCancerScreening;
        TextView txtPncStiScreening;
        TextView txtPncComments;
        LinearLayout container;
        View btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.itemm);
            txtDate = itemView.findViewById(R.id.date);
            txtVisit = itemView.findViewById(R.id.visit);
             txtSummaryLine = itemView.findViewById(R.id.summary_line);
             txtPncVisitType = itemView.findViewById(R.id.pnc_visit_type);
             txtPncTypeOfFeeding = itemView.findViewById(R.id.pnc_type_of_feeding);
             txtPncHivTestDone = itemView.findViewById(R.id.pnc_hiv_test_done);
             txtPncOnPrep = itemView.findViewById(R.id.pnc_on_prep);
             txtPncFpCounselling = itemView.findViewById(R.id.pnc_fp_counselling);
             txtPncCervicalCancerScreening = itemView.findViewById(R.id.pnc_cervical_cancer_screening);
             txtPncStiScreening = itemView.findViewById(R.id.pnc_sti_screening);
             txtPncComments = itemView.findViewById(R.id.pnc_comments);
            btnEdit = itemView.findViewById(R.id.edit_me);
        }
    }

    private String fmt(String millisStr) {
        try {
            long v = Long.parseLong(millisStr);
            return new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date(v));
        } catch (Exception e) {
            return millisStr != null ? millisStr : "";
        }
    }

    private void openForm(MotherPostnatalCareModel visit) {
        try {
            if (oMapper == null) oMapper = new ObjectMapper();
            FormUtils formUtils = new FormUtils(context);
            JSONObject form = formUtils.getFormJson("mother_postnatal_care");

            if (visit.getBase_entity_id() != null) {
                form.put("entity_id", visit.getBase_entity_id());
            }

            // Prefill all mapped fields from the mother postnatal record (including household_id)
            CoreJsonFormUtils.populateJsonForm(form, oMapper.convertValue(visit, Map.class));

            startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startFormActivity(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setName("Mother Postnatal Care");
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
