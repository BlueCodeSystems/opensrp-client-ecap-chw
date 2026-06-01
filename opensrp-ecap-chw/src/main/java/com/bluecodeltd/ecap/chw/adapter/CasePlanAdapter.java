package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CasePlan;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CasePlanAdapter extends RecyclerView.Adapter<CasePlanAdapter.ViewHolder>{

    Context context;
    List<CasePlanModel> caseplans;
    String hivStatus;
    private ObjectMapper oMapper;
    AlertDialog.Builder builder;
    private static final long REFRESH_DELAY = 100;
    private Handler handler = new Handler();


    public CasePlanAdapter(List<CasePlanModel> caseplans, Context context, String hivStatus){

        super();

        this.caseplans = caseplans;
        this.context = context;
        this.hivStatus = hivStatus;
    }

    @Override
    public CasePlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_plan, parent, false);

        CasePlanAdapter.ViewHolder viewHolder = new CasePlanAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CasePlanAdapter.ViewHolder holder, final int position) {

        final CasePlanModel casePlan = caseplans.get(position);

        holder.setIsRecyclable(false);

        holder.txtCaseDate.setText(casePlan.getCase_plan_date());
        holder.txtCasePlanStatus.setText(casePlan.getCase_plan_status());

        final String rowTag = casePlan.getBase_entity_id() != null ? casePlan.getBase_entity_id()
                : (casePlan.getUnique_id() != null ? casePlan.getUnique_id() : String.valueOf(position));
        holder.itemView.setTag(R.id.tag_row_id, rowTag);

        holder.txtVulnerabilities.setText("Loading…");
        holder.delete.setVisibility(View.INVISIBLE);
        Threading.ioBestEffort(() -> {
            String vulnerabilities = null;
            try { vulnerabilities = CasePlanDao.countVulnerabilities(casePlan.getUnique_id(), casePlan.getCase_plan_date()); } catch (Exception ignored) {}
            final String finalVulnerabilities = vulnerabilities;
            Threading.main(() -> {
                Object tag = holder.itemView.getTag(R.id.tag_row_id);
                if (!(tag instanceof String) || !rowTag.equals(tag)) return;
                String vCount = (finalVulnerabilities == null || finalVulnerabilities.trim().isEmpty()) ? "0" : finalVulnerabilities.trim();
                holder.txtVulnerabilities.setText(vCount + " Vulnerabilities");
                holder.delete.setVisibility("0".equals(vCount) ? View.VISIBLE : View.INVISIBLE);
            });
        });

        try {
            Date thedate = new SimpleDateFormat("dd-MM-yyyy").parse(casePlan.getCase_plan_date());
            String month = (String) DateFormat.format("M", thedate);
            int m = Integer.parseInt(month);


          if(m >= 1 && m <= 3){
              holder.txtQuarter.setText(R.string.quarter_two);

          } else if(m > 3 && m <= 6){

              holder.txtQuarter.setText(R.string.quarter_three);

          } else if (m > 6 && m <=9 ){

              holder.txtQuarter.setText(R.string.quarter_four);

          } else if (m > 9 && m <= 12) {

              holder.txtQuarter.setText(R.string.quarter_one);

          }

        } catch (ParseException e) {
            e.printStackTrace();
        }



        View.OnClickListener openCasePlanListener = v -> {
            Intent i = new Intent(context, CasePlan.class);
            i.putExtra("childId",  casePlan.getUnique_id());
            i.putExtra("dateId",  casePlan.getCase_plan_date());
            i.putExtra("case_plan_id",casePlan.getCase_plan_id());
            i.putExtra("hivStatus",  hivStatus);
            context.startActivity(i);
        };

        holder.linearLayout.setOnClickListener(openCasePlanListener);
        holder.editme.setOnClickListener(v -> {
            try {
                openFormUsingFormUtils(context, "case_plan", casePlan);
            } catch (JSONException e) {
                Timber.e(e);
            }
        });
        holder.delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("You are about to delete this VCA case plan");
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
                casePlan.setDelete_status("1");
                JSONObject vcaScreeningForm = formUtils.getFormJson("case_plan");
                try {
                    CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue( casePlan, Map.class));
                    vcaScreeningForm.put("entity_id", casePlan.getBase_entity_id());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {

                    ChildIndexEventClient childIndexEventClient = processRegistration(vcaScreeningForm.toString());
                    if (childIndexEventClient == null) {
                        return;
                    }
                    Runnable onComplete = () -> callActivity(casePlan);
                    boolean scheduled = saveRegistration(childIndexEventClient, true, onComplete);
                    if (!scheduled) {
                        onComplete.run();
                    }


                } catch (Exception e) {
                    Timber.e(e);
                }

            }));

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Alert");
            alert.show();
        });


    }
    public void refreshActivity() {
        handler.postDelayed(refreshRunnable, REFRESH_DELAY);
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            Activity activity = (CasePlan) context;
            activity.recreate();
        }
    };

    private void runOnUiThread(Runnable runnable) {
        if (context instanceof Activity && runnable != null) {
            ((Activity) context).runOnUiThread(runnable);
        }
    }

    public void callActivity(CasePlanModel casePlan) {
        Intent openActivity = new Intent(context, IndexDetailsActivity.class);
        openActivity.putExtra("Child",  casePlan.getUnique_id());
        openActivity.putExtra("dateId",  casePlan.getCase_plan_date());
        openActivity.putExtra("hivStatus",  hivStatus);
        if (context instanceof IndexDetailsActivity) {
            Activity activity = (IndexDetailsActivity) context;
            openActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.finish();
            activity.overridePendingTransition(0, 0);
            activity.startActivity(openActivity);
            activity.overridePendingTransition(0, 0);
//            activity.recreate();
        } else {
            context.startActivity(openActivity);
        }

    }

    public void openFormUsingFormUtils(Context context, String formName, CasePlanModel casePlan) throws JSONException {
        oMapper = new ObjectMapper();

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            Timber.e(e);
        }
        if (formUtils == null) {
            return;
        }

        JSONObject formToBeOpened = formUtils.getFormJson(formName);
        formToBeOpened.put("entity_id", casePlan.getBase_entity_id());
        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(casePlan, Map.class));
        startFormActivity(formToBeOpened);
    }

    public void startFormActivity(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setName("Case Plan");
        form.setHideSaveLabel(true);
        form.setNextLabel("Next");
        form.setPreviousLabel("Previous");
        form.setSaveLabel("Submit");
        form.setActionBarBackground(org.smartregister.R.color.dark_grey);

        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        ((Activity) context).startActivityForResult(intent, com.bluecodeltd.ecap.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
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

                case "VCA Case Plan":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_VCA_CASE_PLAN);
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
    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode, Runnable onComplete) {

        Runnable runnable = () -> {

            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();

            try {
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

                        //Get saved event for processing
                        List<EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                        getClientProcessorForJava().processClient(savedEvents);
                        getAllSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());


                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            } finally {
                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }
            }

        };

        try {
            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(runnable);
            return true;
        } catch (Exception exception) {
            Timber.e(exception);
            if (onComplete != null) {
                runOnUiThread(onComplete);
            }
            return false;
        }
    }
    private ECSyncHelper getECSyncHelper() {
        return ChwApplication.getInstance().getEcSyncHelper();
    }
    @Override
    public int getItemCount() {

        return caseplans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtCaseDate, txtQuarter, txtCasePlanStatus, txtVulnerabilities;
        ImageView delete, editme;

        LinearLayout linearLayout;


        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtCaseDate  = itemView.findViewById(R.id.case_date);
            txtQuarter = itemView.findViewById(R.id.quarter);
            txtCasePlanStatus = itemView.findViewById(R.id.case_plan_status);
            txtVulnerabilities = itemView.findViewById(R.id.vulnerabilities);
            editme = itemView.findViewById(R.id.edit_me);
            delete = itemView.findViewById(R.id.delete_record);

        }


        @Override
        public void onClick(View v) {

        }
    }

}


