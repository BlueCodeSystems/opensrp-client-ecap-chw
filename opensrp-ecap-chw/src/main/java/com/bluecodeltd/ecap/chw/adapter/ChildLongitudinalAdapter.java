package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.app.Dialog;
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
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.ChildLongitudinalFollowUpModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.util.FormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ChildLongitudinalAdapter extends RecyclerView.Adapter<ChildLongitudinalAdapter.ViewHolder> {

    private final Context context;
    private final List<ChildLongitudinalFollowUpModel> items;
    private final String householdId;
    private final String uniqueId;
    private final ObjectMapper oMapper = new ObjectMapper();

    public ChildLongitudinalAdapter(Context context, List<ChildLongitudinalFollowUpModel> items,
                                    String householdId, String uniqueId) {
        this.context = context;
        this.items = items;
        this.householdId = householdId;
        this.uniqueId = uniqueId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_longitudinal_visit, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildLongitudinalFollowUpModel visit = items.get(position);
        holder.setIsRecyclable(false);

        holder.txtDate.setText(visit.getInfant_date_of_visit());
        holder.txtAge.setText(visit.getInfant_age());

        View.OnClickListener editListener = v -> {
            if (isInactive(visit)) {
                showInactiveDialog(visit);
                return;
            }
            openForm(visit);
        };
        holder.container.setOnClickListener(editListener);
        holder.btnEdit.setOnClickListener(editListener);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtAge;
        LinearLayout container;
        View btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.item_container);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtAge = itemView.findViewById(R.id.txtAge);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    private void openForm(ChildLongitudinalFollowUpModel visit) {
        try {
            FormUtils formUtils = new FormUtils(context);
            JSONObject form = formUtils.getFormJson("child_longitudinal_follow_up");

            try {
                JSONArray flds = form.getJSONObject("step1").getJSONArray("fields");
                for (int i = 0; i < flds.length(); i++) {
                    JSONObject f = flds.getJSONObject(i);
                    String key = f.optString("key");
                    if ("household_id".equals(key) && householdId != null) {
                        f.put("value", householdId);
                    } else if ("unique_id".equals(key) && uniqueId != null) {
                        f.put("value", uniqueId);
                    }
                }
            } catch (Exception ignored) {
            }

            if (visit.getBase_entity_id() != null) {
                form.put("entity_id", visit.getBase_entity_id());
            }

            try {
                CoreJsonFormUtils.populateJsonForm(form, oMapper.convertValue(visit, Map.class));
            } catch (Exception ignored) {
            }

            Form f = new Form();
            f.setWizard(false);
            f.setName(context.getString(org.smartregister.chw.core.R.string.child_details));
            f.setHideSaveLabel(true);
            f.setNextLabel(context.getString(R.string.next));
            f.setPreviousLabel(context.getString(R.string.previous));
            f.setSaveLabel(context.getString(R.string.submit));
            Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, f);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
            ((Activity) context).startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private boolean isInactive(ChildLongitudinalFollowUpModel visit) {
        try {
            CaseStatusModel caseStatusModel = IndexPersonDao.getCaseStatus(visit.getUnique_id());
            String status = caseStatusModel != null ? caseStatusModel.getCase_status() : null;
            return status != null && (status.equals("0") || status.equals("2"));
        } catch (Exception e) {
            return false;
        }
    }

    private void showInactiveDialog(ChildLongitudinalFollowUpModel visit) {
        try {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.show();
            TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
            CaseStatusModel caseStatusModel = IndexPersonDao.getCaseStatus(visit.getUnique_id());
            String first = caseStatusModel != null && caseStatusModel.getFirst_name() != null ? caseStatusModel.getFirst_name() : "This beneficiary";
            String last = caseStatusModel != null && caseStatusModel.getLast_name() != null ? caseStatusModel.getLast_name() : "";
            dialogMessage.setText(first + (last.isEmpty() ? "" : (" " + last)) + " was either de-registered or inactive in the program");
            android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
            dialogButton.setOnClickListener(va -> dialog.dismiss());
        } catch (Exception ignored) {
        }
    }
}
