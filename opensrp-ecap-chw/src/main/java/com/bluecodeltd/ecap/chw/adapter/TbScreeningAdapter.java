package com.bluecodeltd.ecap.chw.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
        final String followForClick = follow;
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
            // If there is an existing follow-up, show read-only outcome dialog; otherwise start outcome form
            if (followForClick != null && !followForClick.trim().isEmpty()) {
                showOutcomeDialog(m);
            } else {
                // Open outcome form using the TB screening entity_id (unique_tb_id) with encounter type "TB Screening"
                openForm("tb_screening_outcome", m);
            }
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

    private void showOutcomeDialog(TbScreeningModel m) {
        try {
            Dialog dialog = new Dialog(context);
            View view = LayoutInflater.from(context).inflate(R.layout.tbscreening_outcome_dialog, null, false);
            dialog.setContentView(view);

            TextView caIdView = view.findViewById(R.id.tb_followup_ca_id);
            TextView dateView = view.findViewById(R.id.tb_followup_last_date);
            TextView referralView = view.findViewById(R.id.tb_followup_referral_status);
            TextView diagnosisView = view.findViewById(R.id.tb_followup_diagnosis_summary);
            TextView commentsView = view.findViewById(R.id.tb_followup_comments_preview);
            ImageButton closeButton = view.findViewById(R.id.tb_followup_close);
            ImageView editIcon = view.findViewById(R.id.tb_followup_edit_icon);

            if (caIdView != null) {
                String caId = m.getUnique_id();
                caIdView.setText(caId != null ? caId : "");
            }

            if (dateView != null) {
                String date = m.getFollowup_date();
                if (date == null || date.trim().isEmpty()) {
                    date = m.getTreatment_followup_date();
                }
                dateView.setText(date != null ? date : "");
            }

            if (referralView != null) {
                String raw = m.getFacility_referral_completed();
                String text;
                if ("yes".equalsIgnoreCase(raw)) {
                    text = "Yes";
                } else if ("no".equalsIgnoreCase(raw)) {
                    text = "No";
                } else {
                    text = "";
                }
                referralView.setText(text);
            }

            if (diagnosisView != null) {
                String summaryText = buildDiagnosisSummary(m);
                if (summaryText == null || summaryText.trim().isEmpty()) {
                    summaryText = context.getString(R.string.tb_followup_diagnosis_default);
                }
                diagnosisView.setText(summaryText);
            }

            if (commentsView != null) {
                String comments = buildCommentsSummary(m);
                commentsView.setText(comments != null ? comments : "");
            }

            if (closeButton != null) {
                closeButton.setOnClickListener(v -> dialog.dismiss());
            }

            if (editIcon != null) {
                editIcon.setOnClickListener(v -> {
                    dialog.dismiss();
                    openForm("tb_screening_outcome", m);
                });
            }

            dialog.show();
        } catch (Exception ignored) { }
    }

    private String buildDiagnosisSummary(TbScreeningModel m) {
        String diagnosisKey = m.getTb_diagnosis_at_facility();
        String diagnosisLabel;
        if ("clinically_diagnosed".equalsIgnoreCase(diagnosisKey)) {
            diagnosisLabel = "Clinically diagnosed";
        } else if ("bacteriologically_confirmed".equalsIgnoreCase(diagnosisKey)) {
            diagnosisLabel = "Bacteriologically confirmed";
        } else if ("no_tb_diagnosis".equalsIgnoreCase(diagnosisKey)) {
            diagnosisLabel = "No TB diagnosis made";
        } else {
            diagnosisLabel = null;
        }

        String initiatedTreatment = m.getInitiated_tb_treatment();
        String initiatedTpt = m.getInitiated_tpt();

        StringBuilder sb = new StringBuilder();
        if (diagnosisLabel != null && !diagnosisLabel.isEmpty()) {
            sb.append("Diagnosis: ").append(diagnosisLabel);
        }
        if (initiatedTreatment != null && !initiatedTreatment.isEmpty()) {
            if (sb.length() > 0) sb.append(" • ");
            sb.append("TB treatment: ").append(mapYesNoNa(initiatedTreatment));
        }
        if (initiatedTpt != null && !initiatedTpt.isEmpty()) {
            if (sb.length() > 0) sb.append(" • ");
            sb.append("TPT: ").append(mapYesNoNa(initiatedTpt));
        }
        return sb.toString();
    }

    private String buildCommentsSummary(TbScreeningModel m) {
        StringBuilder sb = new StringBuilder();
        String outcome = m.getTb_treatment_outcome();
        if (outcome != null && !outcome.isEmpty()) {
            sb.append("Outcome: ").append(mapOutcome(outcome));
        }
        String outcomeComment = m.getTb_treatment_outcome_comment();
        if (outcomeComment != null && !outcomeComment.trim().isEmpty()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(outcomeComment.trim());
        }
        String sectionComments = m.getSection_c_comments();
        if (sectionComments != null && !sectionComments.trim().isEmpty()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(sectionComments.trim());
        }
        return sb.toString();
    }

    private String mapYesNoNa(String value) {
        if (value == null) return "";
        if ("yes".equalsIgnoreCase(value)) return "Yes";
        if ("no".equalsIgnoreCase(value)) return "No";
        if ("na".equalsIgnoreCase(value) || "n_a".equalsIgnoreCase(value)) return "N/A";
        return value;
    }

    private String mapOutcome(String key) {
        if (key == null) return "";
        switch (key) {
            case "cured":
                return "Cured";
            case "treatment_completed":
                return "Treatment completed";
            case "treatment_failed":
                return "Treatment failed";
            case "not_evaluated":
                return "Not evaluated";
            case "died":
                return "Died";
            case "exited_ovc_comprehensive_program":
                return "Exited OVC comprehensive program";
            case "other":
                return "Other";
            default:
                return key;
        }
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


