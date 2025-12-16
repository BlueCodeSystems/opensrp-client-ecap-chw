package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;

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
import com.bluecodeltd.ecap.chw.activity.HeiDetailsActivity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.ChildMonitoringModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.PmtctChildModel;
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
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class PmctChildMonitoringAdapter extends RecyclerView.Adapter<PmctChildMonitoringAdapter.ViewHolder> {
    Context context;
    List<ChildMonitoringModel> postnatal;
    ObjectMapper oMapper;

    public PmctChildMonitoringAdapter(Context context, List<ChildMonitoringModel> postnatal) {
        this.context = context;
        this.postnatal = postnatal;
    }

    @NonNull
    @Override
    public PmctChildMonitoringAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_monitoring_list, parent, false);
        PmctChildMonitoringAdapter.ViewHolder viewHolder = new  PmctChildMonitoringAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PmctChildMonitoringAdapter.ViewHolder holder, int position) {
        final ChildMonitoringModel visit = postnatal.get(position);

        holder.setIsRecyclable(false);

        setText(holder.tvVisit, "Child monitoring visit");
        setText(holder.tvDate, valueOrNA(visit.getDate()));
        setText(holder.tvVisitTypeHeader, "Visit: " + valueOrNA(visit.getPediatic_care_follow_up()));
        setText(holder.tvVisitType, valueOrNA(visit.getPediatic_care_follow_up()));
        setText(holder.tvHivTest, valueOrNA(visit.getHiv_test()));
        setText(holder.tvNvp, valueOrNA(visit.getAzt_3tc_npv()));
        setText(holder.tvCtx, valueOrNA(visit.getCtx()));
        setText(holder.tvDateTested, valueOrNA(visit.getDate_tested()));
        setText(holder.tvIycfCounselling, valueOrNA(visit.getIycf_counselling()));
        setText(holder.tvFeedingOption, valueOrNA(visit.getInfant_feeding_options()));
        setText(holder.tvHighRiskHei, valueOrNA(visit.getHigh_risk_hei()));
        setText(holder.tvNutritionStatus, valueOrNA(visit.getNutrition_status()));
        setText(holder.tvMedicalComplications, valueOrNA(visit.getMedical_complications()));
        setText(holder.tvChildOedema, valueOrNA(visit.getChild_oedema()));
        setText(holder.tvOedemaStage, valueOrNA(visit.getOedema_stage()));
        setText(holder.tvTbSymptoms, valueOrNA(visit.getTb_screening_symptoms()));
        setText(holder.tvTbOther, valueOrNA(visit.getOther_tb_symptom()));
        setText(holder.tvTbReferral, valueOrNA(visit.getTb_referral()));
        setText(holder.tvTbComments, valueOrNA(visit.getComments_tb()));

        View.OnClickListener openForm = v -> {
            try {
                openFormUsingFormUtils(context, "pmtct_child_monitoring", visit);
            } catch (JSONException e) {
                Timber.e(e);
            }
        };

        holder.headerLayout.setOnClickListener(openForm);
        holder.editme.setOnClickListener(openForm);
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
                    visit.setDeleted_status("1");
                    JSONObject vcaScreeningForm = formUtils.getFormJson("pmtct_child_monitoring");
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
//                    Intent householdProfile = new Intent(context, M.class);
//                    householdProfile.putExtra("householdId",visit.getHousehold_id());
//                    context.startActivity(householdProfile);
//                    ((Activity) context).finish();

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
        dialogMessage.setText("Loading...");
        final String hid = householdId;
        Threading.io(() -> {
            Household house = null;
            try { house = HouseholdDao.getHousehold(hid); } catch (Exception ignored) {}
            final Household finalHouse = house;
            Threading.main(() -> {
                String name = (finalHouse != null && finalHouse.getCaregiver_name() != null) ? finalHouse.getCaregiver_name() : "Household";
                dialogMessage.setText(name + message);
            });
        });

        android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

    }

    public void openFormUsingFormUtils(Context context, String formName, ChildMonitoringModel visit) throws JSONException {

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

                case "Ptmct Child Monitoring":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_child_monitoring");
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

        return postnatal.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvVisit, tvVisitTypeHeader, tvDate, tvVisitType, tvHivTest, tvNvp, tvCtx, tvDateTested,
                tvIycfCounselling, tvFeedingOption, tvHighRiskHei, tvNutritionStatus, tvMedicalComplications,
                tvChildOedema, tvOedemaStage, tvTbSymptoms, tvTbOther, tvTbReferral, tvTbComments;
        LinearLayout headerLayout;
        ImageView editme, delete;

        public ViewHolder(View itemView) {

            super(itemView);

            headerLayout = itemView.findViewById(R.id.header_layout);
            tvVisit = itemView.findViewById(R.id.tv_visit);
            tvVisitTypeHeader = itemView.findViewById(R.id.tv_visit_type_header);
            tvDate  = itemView.findViewById(R.id.tv_date);
            tvVisitType = itemView.findViewById(R.id.tv_visit_type);
            tvHivTest = itemView.findViewById(R.id.tv_hiv_test);
            tvNvp = itemView.findViewById(R.id.tv_nvp);
            tvCtx = itemView.findViewById(R.id.tv_ctx);
            tvDateTested = itemView.findViewById(R.id.tv_date_tested);
            tvIycfCounselling = itemView.findViewById(R.id.tv_iycf_counselling);
            tvFeedingOption = itemView.findViewById(R.id.tv_feeding_option);
            tvHighRiskHei = itemView.findViewById(R.id.tv_high_risk_hei);
            tvNutritionStatus = itemView.findViewById(R.id.tv_nutrition_status);
            tvMedicalComplications = itemView.findViewById(R.id.tv_medical_complications);
            tvChildOedema = itemView.findViewById(R.id.tv_child_oedema);
            tvOedemaStage = itemView.findViewById(R.id.tv_oedema_stage);
            tvTbSymptoms = itemView.findViewById(R.id.tv_tb_symptoms);
            tvTbOther = itemView.findViewById(R.id.tv_tb_other);
            tvTbReferral = itemView.findViewById(R.id.tb_referral);
            tvTbComments = itemView.findViewById(R.id.tv_tb_comments);
            editme = itemView.findViewById(R.id.edit_me);
            delete = itemView.findViewById(R.id.delete_record);


        }


        @Override
        public void onClick(View v) {

        }
    }
    public void checkAgeAndOpenForm(Context context, ChildMonitoringModel visit,String dobString) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            Date dob = sdf.parse(dobString);

            Calendar today = Calendar.getInstance();
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dob);
            int ageInYears = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                ageInYears--;
            }

            if (ageInYears > 1) {
                openFormUsingFormUtils(context, "pmtct_child_monitoring", visit);
            } else {
                openFormUsingFormUtils(context, "pmtct_child_monitoring", visit);
            }
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String valueOrNA(String value) {
        return value == null || value.trim().isEmpty() ? "N/A" : value;
    }

    private void setText(TextView view, String value) {
        if (view != null) {
            view.setText(value);
        }
    }

}
