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

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MotherPostnatalAdapter extends RecyclerView.Adapter<MotherPostnatalAdapter.ViewHolder> {

    private final Context context;
    private final List<MotherPostnatalCareModel> items;
    private ObjectMapper oMapper;

    public MotherPostnatalAdapter(Context context, List<MotherPostnatalCareModel> items) {
        this.context = context;
        this.items = items;
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

        holder.txtDate.setText(visit.getLast_interacted_with());
        holder.txtVisit.setText(visit.getPnc_visit_type());

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
        TextView txtVisit;
        LinearLayout container;
        View btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.itemm);
            txtDate = itemView.findViewById(R.id.date);
            txtVisit = itemView.findViewById(R.id.visit);
            btnEdit = itemView.findViewById(R.id.edit_me);
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
