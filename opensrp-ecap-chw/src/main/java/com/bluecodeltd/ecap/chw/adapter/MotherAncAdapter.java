package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.MotherAncModel;
import com.bluecodeltd.ecap.chw.util.Constants;
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
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MotherAncAdapter extends RecyclerView.Adapter<MotherAncAdapter.ViewHolder> {

    private final Context context;
    private final List<MotherAncModel> items;
    private ObjectMapper mapper;

    public MotherAncAdapter(Context context, List<MotherAncModel> items) {
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
        MotherAncModel visit = items.get(position);
        holder.setIsRecyclable(false);

        holder.txtDate.setText(visit.getDate_1st_visit());

        String gestationWeeks = visit.getGestation_age_in_weeks();
        if (gestationWeeks != null && !gestationWeeks.isEmpty()) {
            holder.txtGestation.setText("Gestation: " + gestationWeeks + " weeks");
        } else {
            holder.txtGestation.setText("");
        }

        String hivTested = visit.getHiv_tested();
        String hivResult = visit.getResult_of_hiv_test();
        if (hivTested != null && !hivTested.isEmpty()) {
            if (hivResult != null && !hivResult.isEmpty()) {
                holder.txtHivTestSummary.setText("Tested: " + hivTested + " (" + hivResult + ")");
            } else {
                holder.txtHivTestSummary.setText("Tested: " + hivTested);
            }
        } else if (hivResult != null && !hivResult.isEmpty()) {
            holder.txtHivTestSummary.setText(hivResult);
        } else {
            holder.txtHivTestSummary.setText("");
        }

        String maleHivTested = visit.getMale_hiv_tested();
        String maleHivResult = visit.getMale_result_of_hiv_test();
        if (maleHivTested != null && !maleHivTested.isEmpty()) {
            if (maleHivResult != null && !maleHivResult.isEmpty()) {
                holder.txtMaleHivTestSummary.setText("Tested: " + maleHivTested + " (" + maleHivResult + ")");
            } else {
                holder.txtMaleHivTestSummary.setText("Tested: " + maleHivTested);
            }
        } else if (maleHivResult != null && !maleHivResult.isEmpty()) {
            holder.txtMaleHivTestSummary.setText(maleHivResult);
        } else {
            holder.txtMaleHivTestSummary.setText("");
        }

        String gravida = visit.getGravida();
        String parity = visit.getParity();
        if ((gravida != null && !gravida.isEmpty()) || (parity != null && !parity.isEmpty())) {
            StringBuilder gpBuilder = new StringBuilder();
            if (gravida != null && !gravida.isEmpty()) {
                gpBuilder.append("G").append(gravida);
            }
            if (parity != null && !parity.isEmpty()) {
                if (gpBuilder.length() > 0) {
                    gpBuilder.append(" ");
                }
                gpBuilder.append("P").append(parity);
            }
            holder.txtGravidaParity.setText(gpBuilder.toString());
        } else {
            holder.txtGravidaParity.setText("");
        }

        String ttPreviousDoses = visit.getTt_previous_doses();
        holder.txtTtPreviousDoses.setText(ttPreviousDoses != null ? ttPreviousDoses : "");

        String lmpDate = visit.getLmp_date();
        holder.txtLmpDate.setText(lmpDate != null ? lmpDate : "");

        String eddDate = visit.getEdd_date();
        holder.txtEddDate.setText(eddDate != null ? eddDate : "");

        holder.container.setOnClickListener(v -> openAncForm(visit));
        holder.btnEdit.setOnClickListener(v -> openAncForm(visit));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtGestation;
        TextView txtHivTestSummary;
        TextView txtMaleHivTestSummary;
        TextView txtGravidaParity;
        TextView txtTtPreviousDoses;
        TextView txtLmpDate;
        TextView txtEddDate;
        View container;
        View btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.item_container);
            txtDate = itemView.findViewById(R.id.txtDateFirstVisit);
            txtGestation = itemView.findViewById(R.id.txtGestationWeeks);
            txtHivTestSummary = itemView.findViewById(R.id.txtHivTestSummary);
            txtMaleHivTestSummary = itemView.findViewById(R.id.txtMaleHivTestSummary);
            txtGravidaParity = itemView.findViewById(R.id.txtGravidaParity);
            txtTtPreviousDoses = itemView.findViewById(R.id.txtTtPreviousDoses);
            txtLmpDate = itemView.findViewById(R.id.txtLmpDate);
            txtEddDate = itemView.findViewById(R.id.txtEddDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    private void openAncForm(MotherAncModel visit) {
        try {
            if (mapper == null) mapper = new ObjectMapper();
            FormUtils formUtils = new FormUtils(context);
            JSONObject form = formUtils.getFormJson("mother_anc");

            // Use the existing mother record as entity_id to keep it linked
            form.put("entity_id", visit.getBase_entity_id());

            // Pre-populate with visit data (including household_id)
            CoreJsonFormUtils.populateJsonForm(form, mapper.convertValue(visit, Map.class));

            startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startFormActivity(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setName("ANC");
        form.setHideSaveLabel(true);
        form.setNextLabel(context.getString(R.string.next));
        form.setPreviousLabel(context.getString(R.string.previous));
        form.setSaveLabel(context.getString(R.string.submit));
        form.setActionBarBackground(org.smartregister.R.color.dark_grey);
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        ((Activity) context).startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    public static ChildIndexEventClient processRegistration(String jsonString) {
        try {
            JSONObject formJsonObject = new JSONObject(jsonString);

            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            String entityId = formJsonObject.optString("entity_id");
            if (entityId.isEmpty()) {
                entityId = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }

            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);
            JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);

            switch (encounterType) {
                case "ANC":
                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(
                                fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_MOTHER_ANC
                        );
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    public static boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode) {
        Runnable runnable = () -> {
            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();

            if (event != null && client != null) {
                try {
                    ECSyncHelper ecSyncHelper = getECSyncHelper();

                    JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));
                    JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());

                    if (isEditMode && existingClientJsonObject != null) {
                        JSONObject mergedClientJsonObject =
                                org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
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

                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        };

        try {
            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(runnable);
            return true;
        } catch (Exception exception) {
            Timber.e(exception);
            return false;
        }
    }

    private static ECSyncHelper getECSyncHelper() {
        return ChwApplication.getInstance().getEcSyncHelper();
    }
}
