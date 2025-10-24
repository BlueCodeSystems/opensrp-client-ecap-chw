package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.bluecodeltd.ecap.chw.util.FormCache;
import com.bluecodeltd.ecap.chw.util.FormLoadingDialog;
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
import org.smartregister.sync.helper.ECSyncHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class ChildSafetyPlanAdapter  extends RecyclerView.Adapter<ChildSafetyPlanAdapter.ViewHolder>{

    Context context;
    ArrayList<ChildSafetyPlanModel> plans;
    ObjectMapper oMapper;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnDataUpdateListener {
        void onDataUpdate();
    }

    private OnDataUpdateListener onDataUpdateListener;

    public void setOnDataUpdateListener(OnDataUpdateListener onDataUpdateListener) {
        this.onDataUpdateListener = onDataUpdateListener;
    }


    public ChildSafetyPlanAdapter(ArrayList<ChildSafetyPlanModel> plans, Context context){

        super();

        this.plans = plans;
        this.context = context;
        FormCache.warmFormAsync(context, "child_safety_plan");

    }

    @Override
    public ChildSafetyPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_safety_plan, parent, false);

        ChildSafetyPlanAdapter.ViewHolder viewHolder = new ChildSafetyPlanAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChildSafetyPlanAdapter.ViewHolder holder, final int position) {

        final ChildSafetyPlanModel plan = plans.get(position);

        holder.setIsRecyclable(false);
        if (plan.getInitial_date() != null){
            holder.txtDate.setText(plan.getInitial_date());
        }

        if (plan.getInitial_date() != null){
            holder.numberOfAction.setText(ChildSafetyActionDao.countChildSafetyPlan(plan.getUnique_id(),plan.getInitial_date())+" Actions");
        }

        Child child = null;
        try {
            child = IndexPersonDao.getChildByBaseId(plan.getUnique_id());
        } catch (Exception e) {
            Timber.e(e);
        }
        final Child safeChild = child;

        if (safeChild == null) {
            holder.linearLayout.setOnClickListener(v ->
                    Toast.makeText(context, "Member data incomplete", Toast.LENGTH_LONG).show());
            holder.delete.setVisibility(View.GONE);
            return;
        }

        holder.linearLayout.setOnClickListener(v -> {

            Intent openChildSafetyPlanActionActivity = new Intent(context, ChildSafetyPlanActions.class);
            openChildSafetyPlanActionActivity.putExtra("vca_id",plan.getUnique_id());
            openChildSafetyPlanActionActivity.putExtra("vca_name",safeChild.getFirst_name()+" "+safeChild.getLast_name());
            openChildSafetyPlanActionActivity.putExtra("action_date",plan.getInitial_date());
            context.startActivity(openChildSafetyPlanActionActivity);

        });
        if(ChildSafetyActionDao.countChildSafetyPlan(plan.getUnique_id(),plan.getInitial_date()).equals("0")){
          holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("You are about to delete "+safeChild.getFirst_name()+" "+safeChild.getLast_name()+" child safety plan");
            builder.setNegativeButton("NO", (dialog, id) -> {
                //  Action for 'NO' Button
                dialog.cancel();

            }).setPositiveButton("YES",((dialogInterface, i) -> {
                plan.setDelete_status("1");
                AlertDialog loading = FormLoadingDialog.show(context instanceof Activity ? (Activity) context : null);
                Threading.io(() -> {
                    try {
                        JSONObject childSafetyPlanForm = FormCache.obtainFormTemplate(context, "child_safety_plan");
                        if (childSafetyPlanForm == null) {
                            Threading.main(() -> FormLoadingDialog.dismiss(loading));
                            return;
                        }
                        CoreJsonFormUtils.populateJsonForm(childSafetyPlanForm, new ObjectMapper().convertValue(plan, Map.class));
                        childSafetyPlanForm.put("entity_id", plan.getBase_entity_id());

                        ChildIndexEventClient childIndexEventClient = processRegistration(childSafetyPlanForm.toString());
                        if (childIndexEventClient == null) {
                            Threading.main(() -> FormLoadingDialog.dismiss(loading));
                            return;
                        }
                        saveRegistration(childIndexEventClient,true);
                        Threading.main(() -> {
                            FormLoadingDialog.dismiss(loading);
                            if (onDataUpdateListener != null) {
                                onDataUpdateListener.onDataUpdate();
                            }
                        });
                    } catch (Exception e) {
                        Timber.e(e);
                        Threading.main(() -> {
                            FormLoadingDialog.dismiss(loading);
                            Toasty.error(context, "Unable to update plan", Toast.LENGTH_LONG, true).show();
                        });
                    }
                });

            }));

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Alert");
            alert.show();
        });
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

                    if (onDataUpdateListener != null) {
                        mainHandler.post(onDataUpdateListener::onDataUpdate);
                    }

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

    public void openFormUsingFormUtils(Context context, String formName, ChildSafetyPlanModel service) {
        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (!isActivityActive(activity)) {
            return;
        }
        AlertDialog loading = FormLoadingDialog.show(activity);
        if (!isActivityActive(activity)) {
            FormLoadingDialog.dismiss(loading);
            return;
        }
        Threading.io(() -> {
            try {
                oMapper = new ObjectMapper();
                JSONObject formToBeOpened = FormCache.obtainFormTemplate(context, formName);
                if (formToBeOpened == null) {
                    Threading.main(() -> FormLoadingDialog.dismiss(loading));
                    return;
                }

                formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).remove("read_only");
                formToBeOpened.put("entity_id", service.getBase_entity_id());

                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(service, Map.class));
                Threading.main(() -> {
                    FormLoadingDialog.dismiss(loading);
                    if (!isActivityActive(activity)) {
                        return;
                    }
                    startFormActivity(formToBeOpened);
                });
            } catch (Exception e) {
                Timber.e(e);
                Threading.main(() -> {
                    FormLoadingDialog.dismiss(loading);
                    if (!isActivityActive(activity)) {
                        return;
                    }
                    Toasty.error(context, "Unable to open form", Toast.LENGTH_LONG, true).show();
                });
            }
        });
    }

    private static boolean isActivityActive(Activity activity) {
        if (activity == null) {
            return false;
        }
        if (activity.isFinishing()) {
            return false;
        }
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !activity.isDestroyed();
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
