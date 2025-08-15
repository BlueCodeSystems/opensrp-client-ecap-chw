package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static org.smartregister.client.utils.constants.JsonFormConstants.STEP1;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CasePlan;
import com.bluecodeltd.ecap.chw.activity.HouseholdCasePlanActivity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
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

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HouseholdDomainPlanAdapter extends RecyclerView.Adapter<HouseholdDomainPlanAdapter.ViewHolder> {
    Context context;

    String deleteFormName;

    List<CasePlanModel> caseplans;

    ObjectMapper oMapper;
    private static final long REFRESH_DELAY = 100;
    private Handler handler = new Handler();
    public interface OnDataUpdateListener {
        void onDataUpdate();
    }

    private DomainPlanAdapter.OnDataUpdateListener onDataUpdateListener;

    public void setOnDataUpdateListener(DomainPlanAdapter.OnDataUpdateListener onDataUpdateListener) {
        this.onDataUpdateListener = onDataUpdateListener;
    }

    public HouseholdDomainPlanAdapter(ArrayList<CasePlanModel> caseplans, Context context, String formName) {
        this.context = context;
        this.deleteFormName = formName;
        this.caseplans = caseplans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_domain, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final CasePlanModel casePlan = caseplans.get(position);

        holder.txtType.setText(casePlan.getType());
        holder.txtVulnerability.setText(casePlan.getVulnerability());
        holder.txtGoal.setText(casePlan.getGoal());
        holder.txtServices.setText(casePlan.getServices());
        holder.txtServicesReferred.setText(casePlan.getService_referred());
        holder.txtInstitution.setText(casePlan.getInstitution());
        holder.txtDueDate.setText("Due Date : " + casePlan.getDue_date());


        if(casePlan.getStatus().equals(("C"))){

            holder.txtStatus.setText("Complete");

        } else if(casePlan.getStatus().equals(("P"))) {

            holder.txtStatus.setText("In Progress");

        } else if(casePlan.getStatus().equals(("D"))) {

            holder.txtStatus.setText("Delayed");

        }

        holder.txtComment.setText(casePlan.getComment());

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
            }
        });

//        CaseStatusModel caseStatusModel = IndexPersonDao.getCaseStatus(casePlan.getUnique_id());

        holder.editme.setOnClickListener(v -> {
            Household household = HouseholdDao.getHousehold(casePlan.getHousehold_id());

          if (household.getHousehold_case_status() != null && (household.getHousehold_case_status().equals("0") || household.getHousehold_case_status().equals("2"))) {
                showDialogBox(casePlan.getHousehold_id(), "`s has been inactive or de-registered");
            } else {
                if (v.getId() == R.id.edit_me) {

                    try {
                        if (context instanceof CasePlan) {
                            openFormUsingFormUtils(context, "domain", casePlan);
                        } else {
                            openFormUsingFormUtils(context, "caregiver_domain", casePlan);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        holder.delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("You are about to delete this vulnerability");
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
                JSONObject vcaScreeningForm = formUtils.getFormJson(deleteFormName);
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
                    saveRegistration(childIndexEventClient,true);

                    if (onDataUpdateListener != null) {
                        onDataUpdateListener.onDataUpdate();
                    }

                } catch (Exception e) {
                    Timber.e(e);
                }
               refreshActivity();

            }));

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Alert");
            alert.show();
        });

        holder.expLess.setOnClickListener(v -> {

            if (v.getId() == R.id.expand_less) {

                holder.exPandableView.setVisibility(View.GONE);
                holder.expMore.setVisibility(View.VISIBLE);
                holder.expLess.setVisibility(View.GONE);
            }
        });
    }

    public void refreshActivity() {
        handler.postDelayed(refreshRunnable, REFRESH_DELAY);
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            Activity activity = (HouseholdCasePlanActivity) context;
            activity.recreate();
        }
    };
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
    public void openFormUsingFormUtils(Context context, String formName, CasePlanModel caseplan) throws JSONException {

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

        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("read_only",true);
        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("read_only",true);

        try {
            JSONArray subPopulation = getFieldJSONObject(fields(formToBeOpened, STEP1), "type").getJSONArray("options");
            JSONArray filteredSubPopulation = new JSONArray();
            Household household = HouseholdDao.getHousehold(caseplan.getHousehold_id());
            int age = calculateAge(household.getCaregiver_birth_date());

            for (int i = 0; i < subPopulation.length(); i++) {
                JSONObject option = subPopulation.getJSONObject(i);

                if (!(age > 18 && "schooled".equals(option.getString("key")))) {
                    filteredSubPopulation.put(option);
                }
            }

            getFieldJSONObject(fields(formToBeOpened, STEP1), "type").put("options", filteredSubPopulation);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FilterOptions", "Error processing subPopulation options: " + e.getMessage());
        }


        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(caseplan, Map.class));

        startFormActivity(formToBeOpened);

    }
    public static int calculateAge(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate birthDate = LocalDate.parse(dateString, formatter);

            LocalDate currentDate = LocalDate.now();

            if (birthDate.isAfter(currentDate)) {
                throw new IllegalArgumentException("Birth date cannot be in the future.");
            }

            return Period.between(birthDate, currentDate).getYears();

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use 'dd-MM-yyyy'.", e);
        }
    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Vulnerabilities");
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

                case "Domain":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_vca_case_plan_domain");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Caregiver Domain":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_caregiver_case_plan_domain");
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
                        onDataUpdateListener.onDataUpdate();
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

    @Override
    public int getItemCount() {
        return caseplans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtType, txtVulnerability,txtCasePlanStatus,
                txtGoal, txtServices, txtServicesReferred, txtInstitution, txtDueDate, txtStatus, txtComment;

        LinearLayout linearLayout, exPandableView;

        ImageView expMore, expLess, editme, delete;

        public ViewHolder(View itemView) {

            super(itemView);

            editme = itemView.findViewById(R.id.edit_me);
            expLess = itemView.findViewById(R.id.expand_less);
            expMore = itemView.findViewById(R.id.expand_more);
            linearLayout = itemView.findViewById(R.id.itemm);
            exPandableView = itemView.findViewById(R.id.expandable);
            txtType = itemView.findViewById(R.id.typex);
            txtVulnerability = itemView.findViewById(R.id.vulnerability);
            txtGoal = itemView.findViewById(R.id.goal);
            txtServices = itemView.findViewById(R.id.services);
            txtServicesReferred = itemView.findViewById(R.id.services_referred);
            txtInstitution = itemView.findViewById(R.id.institution);
            txtDueDate = itemView.findViewById(R.id.due_date);
            txtStatus = itemView.findViewById(R.id.statusx);
            txtComment = itemView.findViewById(R.id.comment);
            delete = itemView.findViewById(R.id.delete_record);


        }

        // Click event for all items
        public void onClick(View v) {

        }
    }
}
