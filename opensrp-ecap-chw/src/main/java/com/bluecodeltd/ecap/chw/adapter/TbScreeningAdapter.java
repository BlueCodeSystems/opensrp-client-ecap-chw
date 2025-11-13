package com.bluecodeltd.ecap.chw.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.TbScreeningModel;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.util.FormUtils;

import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TbScreeningAdapter extends RecyclerView.Adapter<TbScreeningAdapter.VH> {

    public interface Listener {
        void onAddOutcome(TbScreeningModel item);
        void onEdit(TbScreeningModel item);
    }

    private final Context context;
    private final Listener listener;
    private final List<TbScreeningModel> items;

    public TbScreeningAdapter(Context context, List<TbScreeningModel> data, Listener listener) {
        this.context = context;
        this.listener = listener;
        this.items = data != null ? new ArrayList<>(data) : new ArrayList<>();
    }

    public void setItems(List<TbScreeningModel> data) {
        this.items.clear();
        if (data != null) this.items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tb_screening, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        TbScreeningModel m = items.get(position);
        h.tvDate.setText(fmt(m.getLast_interacted_with()));
        String summary = m.getReferred_for_tb_evaluation() != null ? ("Referred: " + m.getReferred_for_tb_evaluation()) : "";
        h.tvSummary.setText(summary);
        // Follow-up date binding
        String follow = m.getFollowup_date();
        if (follow == null || follow.trim().isEmpty()) follow = m.getTreatment_followup_date();
        if (follow != null && !follow.trim().isEmpty()) {
            h.tvFollowUpDate.setText("Follow-up: " + follow);
            h.tvFollowUpDate.setVisibility(View.VISIBLE);
            h.btnAddOutcome.setText("View Outcome");
        } else {
            h.tvFollowUpDate.setVisibility(View.GONE);
            h.btnAddOutcome.setText("Follow up");
        }

        h.btnAddOutcome.setOnClickListener(v -> {
            if (isInactive(m)) { showInactiveDialog(m); return; }
            // Open outcome form using the TB screening entity_id (unique_tb_id) with encounter type "TB Screening"
            openForm("tb_screening_outcome", m);
        });
        h.btnEdit.setOnClickListener(v -> {
            if (isInactive(m)) { showInactiveDialog(m); return; }
            if (listener != null) listener.onEdit(m);
        });
        h.itemView.setOnClickListener(v -> {
            if (isInactive(m)) { showInactiveDialog(m); return; }
            // Align with Nutrition adapter behavior: clicking row opens edit
            if (listener != null) listener.onEdit(m);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String fmt(String millisStr) {
        try {
            long v = Long.parseLong(millisStr);
            return new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date(v));
        } catch (Exception e) {
            return millisStr != null ? millisStr : "";
        }
    }

    private boolean isInactive(TbScreeningModel m) {
        try {
            CaseStatusModel caseStatusModel = IndexPersonDao.getCaseStatus(m.getUnique_id());
            String status = caseStatusModel != null ? caseStatusModel.getCase_status() : null;
            return status != null && (status.equals("0") || status.equals("2"));
        } catch (Exception e) {
            return false;
        }
    }

    private void showInactiveDialog(TbScreeningModel m) {
        try {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.show();
            TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
            CaseStatusModel caseStatusModel = IndexPersonDao.getCaseStatus(m.getUnique_id());
            String first = caseStatusModel != null && caseStatusModel.getFirst_name() != null ? caseStatusModel.getFirst_name() : "This beneficiary";
            String last = caseStatusModel != null && caseStatusModel.getLast_name() != null ? caseStatusModel.getLast_name() : "";
            dialogMessage.setText(first + (last.isEmpty() ? "" : (" " + last)) + " was either de-registered or inactive in the program");
            Button dialogButton = dialog.findViewById(R.id.dialog_button);
            dialogButton.setOnClickListener(va -> dialog.dismiss());
        } catch (Exception ignored) { }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvSummary, tvFollowUpDate;
        ImageButton btnEdit;
        Button btnAddOutcome;

        VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            tvFollowUpDate = itemView.findViewById(R.id.tvFollowUpDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnAddOutcome = itemView.findViewById(R.id.btnAddOutcome);
        }
    }
    private void openForm(String formName, TbScreeningModel visit) {
        try {
            FormUtils formUtils = new FormUtils(context);
            org.json.JSONObject form = formUtils.getFormJson(formName);
            // Default entity_id to base_entity_id; outcomes also use TB screening base_entity_id
            String entityId = visit != null ? visit.getBase_entity_id() : null;
            if (entityId == null || entityId.trim().isEmpty()) {
                entityId = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }
            form.put("entity_id", entityId);
            try {
                org.json.JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
                for (int i = 0; i < fields.length(); i++) {
                    org.json.JSONObject f = fields.getJSONObject(i);
                    String key = f.optString("key");
                    if ("unique_id".equals(key)) {
                        f.put("value", visit.getUnique_id());
                    } else if ("unique_tb_id".equals(key)) {
                        String val = visit != null ? visit.getUnique_tb_id() : null;
                        if (val == null || val.trim().isEmpty()) {
                            val = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
                        }
                        f.put("value", val);
                    }
                }
                // Ensure outcome uses encounter type "TB Screening" as requested
                if ("tb_screening_outcome".equals(formName)) {
                    form.remove(JsonFormConstants.ENCOUNTER_TYPE);
                    form.put(JsonFormConstants.ENCOUNTER_TYPE, "TB Screening");
                }
                if (visit != null) {
                    CoreJsonFormUtils.populateJsonForm(form, new com.fasterxml.jackson.databind.ObjectMapper().convertValue(visit, java.util.Map.class));
                }
            } catch (Exception ignored) {}

            Form f = new Form();
            f.setWizard(false);
            f.setName("TB Screening");
            f.setHideSaveLabel(true);
            f.setNextLabel("Next");
            f.setPreviousLabel("Previous");
            f.setSaveLabel("Submit");
            Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, f);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
            ((android.app.Activity) context).startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) { }
    }
}


