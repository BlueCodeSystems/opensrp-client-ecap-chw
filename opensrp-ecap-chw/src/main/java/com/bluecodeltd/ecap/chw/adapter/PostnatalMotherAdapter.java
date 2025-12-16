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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.PmtctMotherPostnatalModel;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PostnatalMotherAdapter extends RecyclerView.Adapter<PostnatalMotherAdapter.ViewHolder> {
    Context context;
    List<PmtctMotherPostnatalModel> postnatal;
    ObjectMapper oMapper;

    public PostnatalMotherAdapter(Context context, List<PmtctMotherPostnatalModel> postnatal) {
        this.context = context;
        this.postnatal = postnatal;
    }

    @NonNull
    @Override
    public PostnatalMotherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pmtct_mother_postnatal, parent, false);
        PostnatalMotherAdapter.ViewHolder viewHolder = new PostnatalMotherAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostnatalMotherAdapter.ViewHolder holder, int position) {

        final PmtctMotherPostnatalModel visit = postnatal.get(position);

        holder.setIsRecyclable(false);

        setText(holder.tvDate, valueOrNA(visit.getDate_of_st_post_natal_care()));
        setText(holder.tvVisitType, valueOrNA(visit.getPostnatal_care_visit()));
        setText(holder.tvVisit, "Postnatal Visit");
        setText(holder.tvMotherTestedHiv, valueOrNA(visit.getMother_tested_for_hiv()));
        setText(holder.tvArtInitiated, valueOrNA(visit.getArt_initiated()));
        setText(holder.tvArtAdherence, valueOrNA(visit.getArt_adherence_counselling_support()));
        setText(holder.tvVlResult, valueOrNA(visit.getVl_result()));
        setText(holder.tvFpCounselling, valueOrNA(visit.getFamily_planning_counselling()));
        setText(holder.tvCondoms, valueOrNA(visit.getNumber_of_condoms_distributed()));
        setText(holder.tvTbSymptoms, valueOrNA(visit.getTb_screening_symptoms_10plus()));
        setText(holder.tvTbOther, valueOrNA(visit.getOther_tb_symptom_10plus()));
        setText(holder.tvTbComments, valueOrNA(visit.getComments_tb_10plus()));
        setText(holder.tvComments, valueOrNA(visit.getComments_at_postnatal_care_visit()));

        String summaryText = "Visit: " + valueOrNA(visit.getPostnatal_care_visit()) + " | HIV test: " + valueOrNA(visit.getMother_tested_for_hiv());
        setText(holder.tvSummary, summaryText);

        View.OnClickListener openForm = v -> {
            try {
                openFormUsingFormUtils(context, "postnatal_care", visit);
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
                    visit.setDelete_status("1");
                    JSONObject vcaScreeningForm = formUtils.getFormJson("postnatal_care");
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
        String sVisit = visit.getPostnatal_care_visit();
        if (sVisit != null && holder.tvVisit != null) {
            SpannableString spannableString = new SpannableString(sVisit);
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sVisit.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvVisit.setText(spannableString);
        }


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

    public void openFormUsingFormUtils(Context context, String formName, PmtctMotherPostnatalModel visit) throws JSONException {

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

                case "Mother Pmtct":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_mother");
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

        TextView tvDate, tvVisit, tvVisitType, tvMotherTestedHiv, tvArtInitiated, tvArtAdherence, tvVlResult,
                tvFpCounselling, tvCondoms, tvTbSymptoms, tvTbOther, tvTbComments, tvComments, tvSummary;
        LinearLayout headerLayout;
        ImageView editme, delete;

        public ViewHolder(View itemView) {

            super(itemView);

            headerLayout = itemView.findViewById(R.id.header_layout);
            tvVisit = itemView.findViewById(R.id.tv_visit);
            tvDate  = itemView.findViewById(R.id.tv_date);
            tvSummary = itemView.findViewById(R.id.tv_summary);
            tvVisitType = itemView.findViewById(R.id.tv_visit_type);

            tvFpCounselling = itemView.findViewById(R.id.tv_fp_counselling);
            tvCondoms = itemView.findViewById(R.id.tv_condoms);

 
            tvComments = itemView.findViewById(R.id.tv_comments);
            editme = itemView.findViewById(R.id.iv_edit);
            delete = itemView.findViewById(R.id.delete_record);


        }


        @Override
        public void onClick(View v) {

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
