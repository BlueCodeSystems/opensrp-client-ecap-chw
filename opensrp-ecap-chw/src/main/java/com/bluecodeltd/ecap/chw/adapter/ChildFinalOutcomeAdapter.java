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
import com.bluecodeltd.ecap.chw.model.ChildFinalOutcomeModel;
import com.bluecodeltd.ecap.chw.util.Threading;
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

public class ChildFinalOutcomeAdapter extends RecyclerView.Adapter<ChildFinalOutcomeAdapter.ViewHolder> {

    private final Context context;
    private final List<ChildFinalOutcomeModel> items;
    private final String householdId;
    private final String uniqueId;
    private final ObjectMapper oMapper = new ObjectMapper();

    public ChildFinalOutcomeAdapter(Context context, List<ChildFinalOutcomeModel> items,
                                    String householdId, String uniqueId) {
        this.context = context;
        this.items = items;
        this.householdId = householdId;
        this.uniqueId = uniqueId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_final_outcome, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildFinalOutcomeModel visit = items.get(position);
        holder.setIsRecyclable(false);

        holder.txtDate.setText(visit.getInfant_final_outcome_date());
        holder.txtStatus.setText(visit.getInfant_final_outcome() != null ? visit.getInfant_final_outcome() : "");

        View.OnClickListener editListener = v -> {
            runIfActive(visit, () -> openForm(visit));
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
        TextView txtStatus;
        LinearLayout container;
        View btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.item_container);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    private void openForm(ChildFinalOutcomeModel visit) {
        try {
            FormUtils formUtils = new FormUtils(context);
            JSONObject form = formUtils.getFormJson("child_final_outcome");

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

    private void runIfActive(ChildFinalOutcomeModel visit, Runnable onActive) {
        final String uniqueId = visit != null ? visit.getUnique_id() : null;
        Threading.io(() -> {
            CaseStatusModel caseStatusModel = null;
            try { caseStatusModel = IndexPersonDao.getCaseStatus(uniqueId); } catch (Exception ignored) {}
            String status = caseStatusModel != null ? caseStatusModel.getCase_status() : null;
            boolean inactive = status != null && (status.equals("0") || status.equals("2"));
            CaseStatusModel finalCaseStatusModel = caseStatusModel;
            Threading.main(() -> {
                if (inactive) {
                    showInactiveDialog(finalCaseStatusModel);
                    return;
                }
                if (onActive != null) onActive.run();
            });
        });
    }

    private void showInactiveDialog(CaseStatusModel caseStatusModel) {
        try {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.show();
            TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
            String first = caseStatusModel != null && caseStatusModel.getFirst_name() != null ? caseStatusModel.getFirst_name() : "This beneficiary";
            String last = caseStatusModel != null && caseStatusModel.getLast_name() != null ? caseStatusModel.getLast_name() : "";
            dialogMessage.setText(first + (last.isEmpty() ? "" : (" " + last)) + " was either de-registered or inactive in the program");
            android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
            dialogButton.setOnClickListener(va -> dialog.dismiss());
        } catch (Exception ignored) {
        }
    }
}
