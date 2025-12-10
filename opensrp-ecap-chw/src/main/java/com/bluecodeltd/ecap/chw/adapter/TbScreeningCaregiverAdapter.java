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
import com.bluecodeltd.ecap.chw.model.TbScreeningCaregiverModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TbScreeningCaregiverAdapter extends RecyclerView.Adapter<TbScreeningCaregiverAdapter.VH> {

    public interface Listener {
        void onAddOutcome(TbScreeningCaregiverModel item);
        void onEdit(TbScreeningCaregiverModel item);
    }

    private final Context context;
    private final Listener listener;
    private final List<TbScreeningCaregiverModel> items;

    public TbScreeningCaregiverAdapter(Context context, List<TbScreeningCaregiverModel> data, Listener listener) {
        this.context = context;
        this.listener = listener;
        this.items = data != null ? new ArrayList<>(data) : new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tb_screening_caregiver, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        TbScreeningCaregiverModel m = items.get(position);
        h.tvDate.setText(fmt(m.getLast_interacted_with()));
        String summary = m.getReferred_for_tb_evaluation() != null ? ("Referred: " + m.getReferred_for_tb_evaluation()) : "";
        h.tvSummary.setText(summary);
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
            if (followForClick != null && !followForClick.trim().isEmpty()) {
                showOutcomeDialog(m);
            } else if (listener != null) {
                listener.onAddOutcome(m);
            }
        });
        h.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(m);
        });
        h.itemView.setOnClickListener(v -> {
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

    private void showOutcomeDialog(TbScreeningCaregiverModel m) {
        try {
            Dialog dialog = new Dialog(context);
            View view = LayoutInflater.from(context).inflate(R.layout.tbscreening_outcome_dialog_caregiver, null, false);
            dialog.setContentView(view);

            TextView caIdView = view.findViewById(R.id.tb_followup_ca_id);
            TextView dateView = view.findViewById(R.id.tb_followup_last_date);
            TextView referralView = view.findViewById(R.id.tb_followup_referral_status);
            TextView diagnosisView = view.findViewById(R.id.tb_followup_diagnosis_summary);
            TextView commentsView = view.findViewById(R.id.tb_followup_comments_preview);
            ImageButton closeButton = view.findViewById(R.id.tb_followup_close);
            ImageView editIcon = view.findViewById(R.id.tb_followup_edit_icon);

            if (caIdView != null) {
                String caId = m.getHousehold_id() != null ? m.getHousehold_id() : m.getUnique_id();
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
                    if (listener != null) listener.onAddOutcome(m);
                });
            }

            dialog.show();
        } catch (Exception ignored) { }
    }

    private String buildDiagnosisSummary(TbScreeningCaregiverModel m) {
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
            if (sb.length() > 0) sb.append(" · ");
            sb.append("TB treatment: ").append(mapYesNoNa(initiatedTreatment));
        }
        if (initiatedTpt != null && !initiatedTpt.isEmpty()) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append("TPT: ").append(mapYesNoNa(initiatedTpt));
        }
        return sb.toString();
    }

    private String buildCommentsSummary(TbScreeningCaregiverModel m) {
        String comments = m.getTb_treatment_outcome_comment();
        if (comments == null || comments.trim().isEmpty()) comments = m.getSection_c_comments();
        return comments;
    }

    private String mapYesNoNa(String raw) {
        if ("yes".equalsIgnoreCase(raw)) return "Yes";
        if ("no".equalsIgnoreCase(raw)) return "No";
        if ("na".equalsIgnoreCase(raw) || "n/a".equalsIgnoreCase(raw)) return "N/A";
        return raw;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvSummary, tvFollowUpDate;
        Button btnAddOutcome;
        ImageButton btnEdit;
        VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            tvFollowUpDate = itemView.findViewById(R.id.tvFollowUpDate);
            btnAddOutcome = itemView.findViewById(R.id.btnAddOutcome);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
