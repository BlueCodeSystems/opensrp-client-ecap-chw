package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.NutritionAssessmentInterventionModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class NutritionAssessmentInterventionAdapter extends RecyclerView.Adapter<NutritionAssessmentInterventionAdapter.ViewHolder> {
    private final Context context;
    private final List<NutritionAssessmentInterventionModel> items;
    private final ObjectMapper oMapper = new ObjectMapper();

    public NutritionAssessmentInterventionAdapter(Context context, List<NutritionAssessmentInterventionModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vca_nutrition_single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        NutritionAssessmentInterventionModel m = items.get(position);
        h.date.setText(m.getDate_of_assessment() != null ? m.getDate_of_assessment() : "");

        String dash = "—";
        h.muac.setText(m.getMuac_category() != null && !m.getMuac_category().isEmpty() ? m.getMuac_category() : dash);
        h.oedema.setText(m.getOedema_stage() != null && !m.getOedema_stage().isEmpty() ? m.getOedema_stage() : dash);
        h.wfa.setText(m.getWfa_category() != null && !m.getWfa_category().isEmpty() ? m.getWfa_category() : dash);
        h.status.setText(m.getIntervention_status() != null && !m.getIntervention_status().isEmpty() ? m.getIntervention_status() : dash);

        try {
            int color = getColorForWfa(m.getWfa_category());
            if (h.statusBar != null) h.statusBar.setBackgroundColor(color);
        } catch (Exception ignored) {}

        h.item.setOnClickListener(v -> openForm(m));

        final String rowTag = m.getBase_entity_id() != null ? m.getBase_entity_id()
                : (m.getUnique_id() != null ? m.getUnique_id() : String.valueOf(position));
        h.itemView.setTag(R.id.tag_row_id, rowTag);

        h.edit.setOnClickListener(v ->
                android.widget.Toast.makeText(context, "Loading case status…", android.widget.Toast.LENGTH_SHORT).show());

        final String uniqueId = m.getUnique_id();
        Threading.ioBestEffort(() -> {
            CaseStatusModel caseStatusModel = null;
            try { caseStatusModel = IndexPersonDao.getCaseStatus(uniqueId); } catch (Exception ignored) {}
            final CaseStatusModel finalCaseStatusModel = caseStatusModel;
            Threading.main(() -> {
                Object tag = h.itemView.getTag(R.id.tag_row_id);
                if (!(tag instanceof String) || !rowTag.equals(tag)) return;
                h.edit.setOnClickListener(v -> {
                    String status = null;
                    try { status = finalCaseStatusModel != null ? finalCaseStatusModel.getCase_status() : null; } catch (Exception ignored) {}
                    if (status != null && ("0".equals(status) || "2".equals(status))) {
                        Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_layout);
                        dialog.show();

                        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
                        String first = finalCaseStatusModel != null && finalCaseStatusModel.getFirst_name() != null ? finalCaseStatusModel.getFirst_name() : "This beneficiary";
                        String last = finalCaseStatusModel != null && finalCaseStatusModel.getLast_name() != null ? finalCaseStatusModel.getLast_name() : "";
                        dialogMessage.setText(first + (last.isEmpty()? "":(" "+last)) + " was either de-registered or inactive in the program");

                        Button dialogButton = dialog.findViewById(R.id.dialog_button);
                        dialogButton.setOnClickListener(va -> dialog.dismiss());
                    } else {
                        openForm(m);
                    }
                });
            });
        });
    }

    

    private void openForm(NutritionAssessmentInterventionModel visit) {
        try {
            FormUtils formUtils = new FormUtils(context);
            JSONObject form = formUtils.getFormJson("nutrition_assessment_intervention");
            form.put("entity_id", visit.getBase_entity_id());
            CoreJsonFormUtils.populateJsonForm(form, oMapper.convertValue(visit, Map.class));

            startFormActivity(form);
        } catch (JSONException e) {
            Timber.e(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startFormActivity(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setName("Nutrition Assessment and Intervention");
        form.setHideSaveLabel(true);
        form.setNextLabel("Next");
        form.setPreviousLabel("Previous");
        form.setSaveLabel("Submit");
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        ((Activity) context).startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    public ChildIndexEventClient processRegistration(String jsonString){
        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            String entityId = formJsonObject.optString("entity_id");
            if(entityId.isEmpty()){
                entityId  = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }
            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);
            JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);

            if ("Nutrition Assessment and Intervention".equals(encounterType)) {
                FormTag formTag = getFormTag();
                Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                        encounterType, Constants.EcapClientTable.EC_NUTRITION_ASSESSMENT_INTERVENTION);
                tagSyncMetadata(event);
                Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                return new ChildIndexEventClient(event, client);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode) {
        Runnable runnable = () -> {
            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();
            if (event != null && client != null) {
                try {
                    ECSyncHelper ecSyncHelper = getECSyncHelper();
                    JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));
                    JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());
                    if (isEditMode && existingClientJsonObject != null) {
                        JSONObject mergedClientJsonObject = org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
                        ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);
                    } else {
                        ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);
                    }
                    JSONObject eventJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));
                    ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);
                    Long lastUpdatedAtDate = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                    Date currentSyncDate = new Date(lastUpdatedAtDate);
                    List<EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                    getClientProcessorForJava().processClient(savedEvents);
                    getAllSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());
                } catch (Exception e) { Timber.e(e); }
            }
        };
        try {
            new AppExecutors().diskIO().execute(runnable);
            return true;
        } catch (Exception e) { Timber.e(e); return false; }
    }

    private ECSyncHelper getECSyncHelper() { return ChwApplication.getInstance().getEcSyncHelper(); }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView date, muac, oedema, wfa, status;
        ImageView edit;
        View statusBar;

        ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_row);
            date = itemView.findViewById(R.id.date);
            muac = itemView.findViewById(R.id.value_muac);
            oedema = itemView.findViewById(R.id.value_oedema);
            wfa = itemView.findViewById(R.id.value_wfa);
            status = itemView.findViewById(R.id.value_status);
            edit = itemView.findViewById(R.id.edit_me);
            statusBar = itemView.findViewById(R.id.status_bar);
        }
    }

    private int getColorForWfa(String categoryRaw) {
        if (categoryRaw == null) return Color.parseColor("#BDBDBD"); // neutral grey
        String c = categoryRaw.trim().toLowerCase();
        if (c.contains("under")) return Color.parseColor("#E53935"); // red 600
        if (c.contains("over")) return Color.parseColor("#FDD835"); // yellow 600
        if (c.contains("normal")) return Color.parseColor("#43A047"); // green 600
        // Fallbacks for possible synonyms
        if (c.contains("severe") || c.contains("moderate")) return Color.parseColor("#E53935");
        if (c.contains("obese") || c.contains("overweight")) return Color.parseColor("#FDD835");
        return Color.parseColor("#BDBDBD");
    }
}
