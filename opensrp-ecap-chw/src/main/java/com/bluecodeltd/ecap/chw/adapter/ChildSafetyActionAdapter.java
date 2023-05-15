package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.ChildSafetyPlanActions;
import com.bluecodeltd.ecap.chw.activity.ChildSafetyPlanActivity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.ChildSafetyActionModel;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ChildSafetyActionAdapter extends RecyclerView.Adapter<ChildSafetyActionAdapter.ViewHolder>{

    Context context;

    List<ChildSafetyActionModel> action_plan;

    ObjectMapper oMapper;

    public ChildSafetyActionAdapter(ArrayList<ChildSafetyActionModel> action_plan, Context context){

        super();

        this.action_plan = action_plan;
        this.context = context;
    }

    @Override
    public ChildSafetyActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_action_plan, parent, false);

        ChildSafetyActionAdapter.ViewHolder viewHolder = new ChildSafetyActionAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChildSafetyActionAdapter.ViewHolder holder, final int position) {

        final ChildSafetyActionModel plan = action_plan.get(position);

        holder.txtSafetyThreats.setText(plan.getSafety_threats());
        holder.txtSafetyAction.setText(plan.getSafety_action());
        holder.txtSafetyProtection.setText(plan.getSafety_protection());
        holder.txtWhen.setText(plan.getStateWhen());
        holder.txtFrequency.setText(plan.getFrequency());
        holder.txtWho.setText(plan.getWho());
        holder.txtActionDate.setText("Date Created : " + plan.getInitial_date());

        Child child = IndexPersonDao.getChildByBaseId(plan.getUnique_id());
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
                holder.delete.setVisibility(View.GONE);
            }
        });

        holder.editme.setOnClickListener(v -> {

            if (v.getId() == R.id.edit_me) {

                try {
                    if(context instanceof ChildSafetyPlanActions){
                        openFormUsingFormUtils(context, "child_safety_action", plan);
                    } else {
                        openFormUsingFormUtils(context, "caregiver_domain", plan);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.expLess.setOnClickListener(v -> {

            if (v.getId() == R.id.expand_less) {

                holder.exPandableView.setVisibility(View.GONE);
                holder.expMore.setVisibility(View.VISIBLE);
                holder.expLess.setVisibility(View.GONE);
                holder.delete.setVisibility(View.VISIBLE);
            }
        });

        holder.delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("You are about to delete "+child.getFirst_name()+" "+child.getLast_name()+" child safety plan");
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
                plan.setDelete_status("1");
                JSONObject childSafetyPlanForm = formUtils.getFormJson("child_safety_action");
                try {
                    CoreJsonFormUtils.populateJsonForm(childSafetyPlanForm, new ObjectMapper().convertValue( plan, Map.class));
                    childSafetyPlanForm.put("entity_id", plan.getBase_entity_id());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {

                    ChildIndexEventClient childIndexEventClient = processRegistration(childSafetyPlanForm.toString());
                    if (childIndexEventClient == null) {
                        return;
                    }
                    saveRegistration(childIndexEventClient,true);


                } catch (Exception e) {
                    Timber.e(e);
                }
                callChildActionActivity(plan,child);


            }));

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Alert");
            alert.show();
        });
    }
    public void callChildActionActivity(ChildSafetyActionModel plan, Child child) {
        Intent openChildSafetyPlanActionActivity = new Intent(context, ChildSafetyPlanActivity.class);
        openChildSafetyPlanActionActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        openChildSafetyPlanActionActivity.putExtra("vca_id", plan.getUnique_id());
        openChildSafetyPlanActionActivity.putExtra("vca_name", child.getFirst_name() + " " + child.getLast_name());
        openChildSafetyPlanActionActivity.putExtra("action_date", plan.getInitial_date());

        if (context instanceof ChildSafetyPlanActions) {
            Activity activity = (ChildSafetyPlanActions) context;
            activity.finish();
            activity.startActivity(openChildSafetyPlanActionActivity);
        } else {
            context.startActivity(openChildSafetyPlanActionActivity);
        }
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

                case "Child Safety Actions":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CHILD_SAFETY_ACTION);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
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


    public void openFormUsingFormUtils(Context context, String formName, ChildSafetyActionModel caseplan) throws JSONException {

        oMapper = new ObjectMapper();

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.put("entity_id", caseplan.getBase_entity_id());

//        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("read_only",true);

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(caseplan, Map.class));

        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Vulnerabilities");
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

    @Override
    public int getItemCount() {

        return action_plan.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView  txtSafetyThreats,txtSafetyAction,txtSafetyProtection,txtWhen,txtFrequency,txtWho,txtActionDate;

        LinearLayout linearLayout, exPandableView;

        ImageView expMore, expLess, editme,delete;

        public ViewHolder(View itemView) {

            super(itemView);

            editme = itemView.findViewById(R.id.edit_me);
            expLess = itemView.findViewById(R.id.expand_less);
            expMore = itemView.findViewById(R.id.expand_more);
            linearLayout = itemView.findViewById(R.id.itemm);
            exPandableView = itemView.findViewById(R.id.expandable);
            txtSafetyThreats = itemView.findViewById(R.id.safetyThreats);
            txtSafetyAction = itemView.findViewById(R.id.safetyAction);
            txtSafetyProtection = itemView.findViewById(R.id.safetyProtection);
            txtWhen = itemView.findViewById(R.id.when);
            txtFrequency = itemView.findViewById(R.id.frequency);
            txtWho = itemView.findViewById(R.id.who);
            txtActionDate = itemView.findViewById(R.id.initial_date);
            delete = itemView.findViewById(R.id.delete_record);
        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }
    }

}