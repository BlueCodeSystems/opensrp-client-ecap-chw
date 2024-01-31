package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.GraduationModel;
import com.bluecodeltd.ecap.chw.model.Household;
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

public class GraduationAssessmentAdapter extends RecyclerView.Adapter<GraduationAssessmentAdapter.ViewHolder> {
    Context context;
    List<GraduationModel> graduationAssessment;
    ObjectMapper oMapper;

    public GraduationAssessmentAdapter(Context context, List<GraduationModel> graduationAssessment) {
        this.context = context;
        this.graduationAssessment = graduationAssessment;
    }

    @NonNull
    @Override
    public GraduationAssessmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gradaution_list, parent, false);
        GraduationAssessmentAdapter.ViewHolder viewHolder = new GraduationAssessmentAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GraduationAssessmentAdapter.ViewHolder holder, int position) {

        final GraduationModel visit = graduationAssessment.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(visit.getDate_assessment());
        Household householdModel = HouseholdDao.getHousehold(visit.getHousehold_id());

//        if (householdModel.getCaregiver_hiv_status().equals("positive")){
//            holder.exPandableView.setVisibility(View.GONE);
//            holder.expMore.setVisibility(View.GONE);
//            holder.expLess.setVisibility(View.GONE);
//        }
        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                holder.exPandableView.setVisibility(View.VISIBLE);
                holder.expMore.setVisibility(View.GONE);
                holder.expLess.setVisibility(View.VISIBLE);
            }
        });

        holder.expMore.setOnClickListener(v -> {

            if (v.getId() == R.id.expand_more) {

                holder.exPandableView.setVisibility(View.VISIBLE);
                holder.expMore.setVisibility(View.GONE);
                holder.expLess.setVisibility(View.VISIBLE);
                holder.editme.setVisibility(View.GONE);
                holder.delete.setVisibility(View.GONE);
            }
        });

        holder.expLess.setOnClickListener(v -> {

            if (v.getId() == R.id.expand_less) {

                holder.exPandableView.setVisibility(View.GONE);
                holder.expMore.setVisibility(View.VISIBLE);
                holder.expLess.setVisibility(View.GONE);
                holder.editme.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.VISIBLE);
            }
        });


//        if(householdModel.getCaregiver_hiv_status() != null && householdModel.getCaregiver_hiv_status().equals("positive")){
//            holder.intialHivStatus.setText("Positive");
//        } else if(householdModel.getCaregiver_hiv_status().equals("unknown")) {
//            holder.intialHivStatus.setText("Unknown");
//        } else {
//            holder.intialHivStatus.setText("Negative");
//        }
        holder.initialHivStatusDate.setText(householdModel.getScreening_date());

//        if(visit.getHiv_status() != null && visit.getHiv_status().equals("positive")){
//            holder.updateHivStatus.setText("Positive");
//        } else if (visit.getHiv_status().equals("unknown")) {
//            holder.updateHivStatus.setText("Unknown");
//
//        } else {
//            holder.updateHivStatus.setText("Negative");
//        }
        holder.updatedHivStatusDate.setText(visit.getDate_assessment());


        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                try {

                    openFormUsingFormUtils(context, "graduation", visit);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        holder.editme.setOnClickListener(v -> {
            Household household = HouseholdDao.getHousehold(visit.getHousehold_id());
            if (household.getHousehold_case_status() != null && (household.getHousehold_case_status().equals("0") || household.getHousehold_case_status().equals("2"))) {
                showDialogBox(visit.getHousehold_id(), "`s has been inactive or de-registered");
            } else{
                if (v.getId() == R.id.edit_me) {

                    try {

                        openFormUsingFormUtils(context, "graduation", visit);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
        holder.delete.setOnClickListener(v -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("You are about to delete this household graduation ");
                builder.setNegativeButton("NO", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();

                }).setPositiveButton("YES",((dialogInterface, i) -> {
                    FormUtils formUtils = null;
                    try {
                        formUtils = new FormUtils(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    visit.setDelete_status("1");
                    JSONObject vcaScreeningForm = formUtils.getFormJson("graduation");
                    try {
                        CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue(visit, Map.class));
                        vcaScreeningForm.put("entity_id", visit .getBase_entity_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {

                        ChildIndexEventClient childIndexEventClient = processRegistration(vcaScreeningForm.toString());
                        if (childIndexEventClient == null) {
                            return;
                        }
                        saveRegistration(childIndexEventClient,true);


                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    Intent householdProfile = new Intent(context, HouseholdDetails.class);
                    householdProfile.putExtra("householdId",visit.getHousehold_id());
                    context.startActivity(householdProfile);
                    ((Activity) context).finish();

                }));

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert");
                alert.show();

            } catch (Exception e) {
                Timber.e(e);
            }
        });

    }
    public void showDialogBox(String householdId,String message){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();

        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Household house = HouseholdDao.getHousehold(householdId);
        dialogMessage.setText(house.getCaregiver_name() + message);

        android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

    }

    public void openFormUsingFormUtils(Context context, String formName, GraduationModel visit) throws JSONException {

        oMapper = new ObjectMapper();

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.put("entity_id", visit.getBase_entity_id());

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(visit, Map.class));

        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Follow Up Visitation");
        form.setHideSaveLabel(true);
        form.setNextLabel("Next");
        form.setPreviousLabel("Previous");
        form.setSaveLabel("Submit");
        form.setActionBarBackground(R.color.dark_grey);
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

            switch (encounterType) {

                case "Graduation":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_GRADUATION);
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
    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode) {

        Runnable runnable = () -> {

            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();

            if (event != null && client != null) {
                try {
                    ECSyncHelper ecSyncHelper = getECSyncHelper();

                    JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));

                    JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());

                    if (isEditMode) {
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

                    //Get saved event for processing
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
    private ECSyncHelper getECSyncHelper() {
        return ChwApplication.getInstance().getEcSyncHelper();
    }
    @Override
    public int getItemCount() {

        return graduationAssessment.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate,intialHivStatus,initialHivStatusDate,updateHivStatus,updatedHivStatusDate;

        LinearLayout linearLayout, exPandableView;
        ImageView expMore, expLess,editme,delete;

        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtDate  = itemView.findViewById(R.id.date);
            editme = itemView.findViewById(R.id.edit_me);
            delete = itemView.findViewById(R.id.delete_record);
            exPandableView = itemView.findViewById(R.id.expandable);
            expLess = itemView.findViewById(R.id.expand_less);
            expMore = itemView.findViewById(R.id.expand_more);
            intialHivStatus =  itemView.findViewById(R.id.initial_hiv_status);
            initialHivStatusDate  = itemView.findViewById(R.id.initial_hiv_status_date);
            updateHivStatus = itemView.findViewById(R.id.updated_hiv_status);
            updatedHivStatusDate = itemView.findViewById(R.id.updated_hiv_status_date);


        }


        @Override
        public void onClick(View v) {

        }
    }
}
