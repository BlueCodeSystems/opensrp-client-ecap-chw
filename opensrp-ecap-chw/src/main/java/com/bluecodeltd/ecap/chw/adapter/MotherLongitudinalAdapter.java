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
import com.bluecodeltd.ecap.chw.model.MotherLongitudinalFollowUpModel;
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

public class MotherLongitudinalAdapter extends RecyclerView.Adapter<MotherLongitudinalAdapter.ViewHolder> {

    private final Context context;
    private final List<MotherLongitudinalFollowUpModel> items;
    private ObjectMapper oMapper;

    public MotherLongitudinalAdapter(Context context, List<MotherLongitudinalFollowUpModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mother_anc_visit, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MotherLongitudinalFollowUpModel visit = items.get(position);
        holder.setIsRecyclable(false);

        holder.txtDate.setText(visit.getLfu_date_of_visit());
        holder.txtGestation.setText(visit.getLfu_gestation_weeks());

        View.OnClickListener listener = v -> openForm(visit);
        holder.container.setOnClickListener(listener);
        holder.btnEdit.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtGestation;
        LinearLayout container;
        View btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.item_container);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtGestation = itemView.findViewById(R.id.txtGestation);
            btnEdit = itemView.findViewById(R.id.btnEdit);
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
