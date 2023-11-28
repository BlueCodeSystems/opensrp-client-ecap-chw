package com.bluecodeltd.ecap.chw.view_holder;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class IndexRegisterViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private TextView familyNameTextView;

    private TextView villageTextView, gender_age;

    //public Button caseplan_layout;

    private View myStatus;

    private final ImageView  visitLayout, caseplan_layout, warningIcon;
    private final TextView index_icon_layout;
    private final Button dueButton;
    JSONObject indexRegisterForm;

    public IndexRegisterViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        caseplan_layout = itemView.findViewById(R.id.index_case_plan);
        index_icon_layout = itemView.findViewById(R.id.index_icon);
        myStatus = itemView.findViewById(R.id.mystatusx);
        visitLayout = itemView.findViewById(R.id.index_visit);
        gender_age = itemView.findViewById(R.id.gender_age);
        warningIcon = itemView.findViewById(R.id.index_warning);
        dueButton = itemView.findViewById(R.id.due_button);


    }


    public void setupViews(String family, String village, int plans, int visits, String is_index, String status, String gender, String age, String is_screened,String vcaAge){

        familyNameTextView.setText(family);
        villageTextView.setText("ID : "+village);
        gender_age.setText(gender + " : " + age+" ");



        if(status != null && status.equals("1")){
            myStatus.setBackgroundColor(0xff05b714);
        } else if (status != null && status.equals("0")) {
            myStatus.setBackgroundColor(0xffff0000);
        } else if(status != null && status.equals("2")){
            myStatus.setBackgroundColor(0xffffa500);
        }

        if (is_index != null && is_index.equals("1")){

            index_icon_layout.setVisibility(View.VISIBLE);
        } else {

            index_icon_layout.setVisibility(View.GONE);
        }


        if(plans > 0){
            caseplan_layout.setVisibility(View.VISIBLE);
        } else {
            caseplan_layout.setVisibility(View.GONE);
        }

        if(visits > 0){
            visitLayout.setVisibility(View.VISIBLE);
        } else {
            visitLayout.setVisibility(View.GONE);
        }


        if(is_screened != null && is_screened.equals("true")){
            warningIcon.setVisibility(View.GONE);
        } else {
            warningIcon.setVisibility(View.VISIBLE);
        }
        VcaVisitationModel visitStatus = VcaVisitationDao.getVcaVisitationNotification(village);

        if (visitStatus == null) {
            dueButton.setBackgroundResource(R.drawable.due_contact);
            dueButton.setText("Conduct Visit");
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.btn_blue));
            return;
        }

        String statusColor = visitStatus.getStatus_color();

        if (statusColor != null) {
            statusColor = statusColor.trim();
        }

        if (statusColor != null && statusColor.equalsIgnoreCase("green")) {
            dueButton.setBackgroundResource(R.drawable.home_visit_due);
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.colorGreen));
            dueButton.setText("Visit Due: "+visitStatus.getVisit_date());
        } else if (statusColor != null && statusColor.equalsIgnoreCase("yellow")) {
            dueButton.setBackgroundResource(R.drawable.home_visit_10days_less);
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.pie_chart_yellow));
            dueButton.setText("Visit Due: "+visitStatus.getVisit_date());
        } else if (statusColor != null && statusColor.equalsIgnoreCase("red")) {
            dueButton.setBackgroundResource(R.drawable.home_visit_overdue);
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.red_overlay));
            dueButton.setText("Visit Overdue: "+visitStatus.getVisit_date());
        } else  {
            dueButton.setBackgroundResource(R.drawable.due_contact);
            dueButton.setText("Conduct Visit");
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.btn_blue));
        }

//            dueButton.setOnClickListener(view -> {
//
//
//                try {
//                    FormUtils formUtils = null;
//                    try {
//                        formUtils = new FormUtils(context);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    indexRegisterForm = formUtils.getFormJson("household_visitation_for_vca_0_20_years");
//
//
//
//                    JSONObject cId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
////                    cId.remove(JsonFormUtils.VALUE);
////                    cId.put(JsonFormUtils.VALUE, village);
//
//
//                    if (cId  != null) {
//                        cId.remove(JsonFormUtils.VALUE);
////                        try {
////                            cId.put(JsonFormUtils.VALUE, village);
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
//                    }
//
//                    JSONObject cDate = getFieldJSONObject(fields(indexRegisterForm, STEP1), "age");
//                    cDate.put("value", vcaAge);
//
//                    SharedPreferences cp = PreferenceManager.getDefaultSharedPreferences(context);
//                    String  caseworkerphone = cp.getString("phone", "Anonymous");
//                    String caseworkername = cp.getString("caseworker_name", "Anonymous");
//
//                    JSONObject cphone = getFieldJSONObject(fields(indexRegisterForm, "step1"), "phone");
//
//                    if (cphone  != null) {
//                        cphone .remove(JsonFormUtils.VALUE);
//                        try {
//                            cphone .put(JsonFormUtils.VALUE, caseworkerphone);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    JSONObject caseworker_name_object = getFieldJSONObject(fields(indexRegisterForm, "step1"), "caseworker_name");
//                    if (caseworker_name_object != null) {
//                        caseworker_name_object.remove(JsonFormUtils.VALUE);
//                        try {
//                            caseworker_name_object.put(JsonFormUtils.VALUE, caseworkername);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    IndexRegisterActivity idRegisterActivity = (IndexRegisterActivity) context;
//                    idRegisterActivity.startFormActivity(indexRegisterForm);
//
//                    startFormActivity(indexRegisterForm);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
//
//
//                    ChildIndexEventClient childIndexEventClient = processRegistration(indexRegisterForm.toString());
//                    if (childIndexEventClient == null) {
//                        return;
//                    }
//                    saveRegistration(childIndexEventClient,true);
//
//
//                } catch (Exception e) {
//                    Timber.e(e);
////                }
//
//            });



    }
    public void startFormActivity(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setName("Visitation");
        form.setHideSaveLabel(true);
        form.setNextLabel(context.getString(R.string.next));
        form.setPreviousLabel(context.getString(R.string.previous));
        form.setSaveLabel(context.getString(R.string.submit));
        form.setNavigationBackground(R.color.primary);
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        context.startActivity(intent);
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
                case "Household Visitation Form 0-20 years":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD_VCA);
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
}
