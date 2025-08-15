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
import com.bluecodeltd.ecap.chw.dao.ChildSafetyActionDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.ChildSafetyPlanModel;
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

public class ChildSafetyPlanAdapter  extends RecyclerView.Adapter<ChildSafetyPlanAdapter.ViewHolder>{

    Context context;
    ArrayList<ChildSafetyPlanModel> plans;
    ObjectMapper oMapper;


    public ChildSafetyPlanAdapter(ArrayList<ChildSafetyPlanModel> plans, Context context){

        super();

        this.plans = plans;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_safety_plan, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final ChildSafetyPlanModel plan = plans.get(position);

        holder.setIsRecyclable(false);
        if (plan.getInitial_date() != null){
            holder.txtDate.setText(plan.getInitial_date());
        }

        if (plan.getInitial_date() != null){
            holder.numberOfAction.setText(ChildSafetyActionDao.countChildSafetyPlan(plan.getUnique_id(),plan.getInitial_date())+" Actions");
        }


        Child child = IndexPersonDao.getChildByBaseId(plan.getUnique_id());
        holder.linearLayout.setOnClickListener(v -> {

            Intent openChildSafetyPlanActionActivity = new Intent(context, ChildSafetyPlanActions.class);
            openChildSafetyPlanActionActivity.putExtra("vca_id",plan.getUnique_id());
            openChildSafetyPlanActionActivity.putExtra("vca_name",child.getFirst_name()+" "+child.getLast_name());
            openChildSafetyPlanActionActivity.putExtra("action_date",plan.getInitial_date());
            context.startActivity(openChildSafetyPlanActionActivity);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }

        });
        if(ChildSafetyActionDao.countChildSafetyPlan(plan.getUnique_id(),plan.getInitial_date()).equals("0")){
          holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }

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
                JSONObject childSafetyPlanForm = formUtils.getFormJson("child_safety_plan");
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
                callChildSafetyActivity(plan,child);


//                if (context instanceof Activity) {
//                    ((Activity) context).finish();
//                }
//                Intent openChildSafetyPlanActivity = new Intent(context, ChildSafetyPlanActivity.class);
//                openChildSafetyPlanActivity.putExtra("vca_id",plan.getUnique_id());
//                openChildSafetyPlanActivity.putExtra("vca_name",child.getFirst_name()+" "+child.getLast_name());
//                openChildSafetyPlanActivity.putExtra("action_date",plan.getInitial_date());
//                context.startActivity(openChildSafetyPlanActivity);

            }));

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Alert");
            alert.show();
        });
    }
    public void callChildSafetyActivity(ChildSafetyPlanModel plan, Child child) {
        Intent openChildSafetyPlanActivity = new Intent(context, ChildSafetyPlanActivity.class);
        openChildSafetyPlanActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        openChildSafetyPlanActivity.putExtra("vca_id", plan.getUnique_id());
        openChildSafetyPlanActivity.putExtra("vca_name", child.getFirst_name() + " " + child.getLast_name());
        openChildSafetyPlanActivity.putExtra("action_date", plan.getInitial_date());

        if (context instanceof ChildSafetyPlanActivity) {
            Activity activity = (ChildSafetyPlanActivity) context;
            activity.finish();
            activity.startActivity(openChildSafetyPlanActivity);
        } else {
            context.startActivity(openChildSafetyPlanActivity);
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

                case "Child Safety Plan":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CHILD_SAFETY_PLAN);
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

    public void openFormUsingFormUtils(Context context, String formName, ChildSafetyPlanModel service) throws JSONException {

        oMapper = new ObjectMapper();


        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).remove("read_only");

        formToBeOpened.put("entity_id", service.getBase_entity_id());

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(service, Map.class));

        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Service Report");
        form.setHideSaveLabel(true);
        form.setNextLabel("Next");
        form.setPreviousLabel("Previous");
        form.setSaveLabel("Submit");
        form.setActionBarBackground(org.smartregister.R.color.dark_grey);
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        ((Activity) context).startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }

    @Override
    public int getItemCount() {

        return plans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate,numberOfAction;
        ImageView delete;


        LinearLayout linearLayout;


        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtDate  = itemView.findViewById(R.id.date);
            numberOfAction = itemView.findViewById(R.id.number_of_actions);
            delete = itemView.findViewById(R.id.delete_record);


        }


        @Override
        public void onClick(View v) {

        }
    }
}
