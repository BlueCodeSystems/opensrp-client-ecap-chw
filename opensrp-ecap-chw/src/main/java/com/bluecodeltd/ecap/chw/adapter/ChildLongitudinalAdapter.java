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
import com.bluecodeltd.ecap.chw.model.ChildLongitudinalFollowUpModel;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.util.FormUtils;

import java.util.List;

import timber.log.Timber;

public class ChildLongitudinalAdapter extends RecyclerView.Adapter<ChildLongitudinalAdapter.ViewHolder> {

    private final Context context;
    private final List<ChildLongitudinalFollowUpModel> items;
    private final String householdId;
    private final String uniqueId;

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

        View.OnClickListener editListener = v -> openForm(visit);
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

            if (visit.getBase_entity_id() != null) {
                form.put("entity_id", visit.getBase_entity_id());
            }

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

            // Simple pre-populate selected visit basics
            try {
                JSONArray flds = form.getJSONObject("step1").getJSONArray("fields");
                for (int i = 0; i < flds.length(); i++) {
                    JSONObject f = flds.getJSONObject(i);
                    String key = f.optString("key");
                    if ("infant_date_of_visit".equals(key)) {
                        f.put("value", visit.getInfant_date_of_visit());
                    } else if ("infant_age".equals(key)) {
                        f.put("value", visit.getInfant_age());
                    }
                }
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
}

