package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.ViewHolder> {

    Context context;

    List<VcaVisitationModel> visits;
    ObjectMapper oMapper;

    public VisitAdapter(List<VcaVisitationModel> visits, Context context){

        super();

        this.visits = visits;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_vca_visit, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final VcaVisitationModel visit = visits.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(visit.getVisit_date());

        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                try {

                    openFormUsingFormUtils(context, "household_visitation_for_vca_0_20_years_edit", visit);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        CaseStatusModel caseStatusModel = IndexPersonDao.getCaseStatus(visit.getUnique_id());

        holder.editme.setOnClickListener(v -> {
            try {
                if (caseStatusModel != null && caseStatusModel.getCase_status() != null) {
                    // Check case status
                    if (caseStatusModel.getCase_status().equals("0") || caseStatusModel.getCase_status().equals("2")) {
                        Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_layout);
                        dialog.show();

                        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
                        String firstName = caseStatusModel.getFirst_name() != null ? caseStatusModel.getFirst_name() : "Unknown";
                        String lastName = caseStatusModel.getLast_name() != null ? caseStatusModel.getLast_name() : "User";

                        dialogMessage.setText(firstName + " " + lastName +
                                " was either de-registered or inactive in the program");

                        Button dialogButton = dialog.findViewById(R.id.dialog_button);
                        dialogButton.setOnClickListener(va -> dialog.dismiss());
                    } else {
                        if (v.getId() == R.id.edit_me) {
                            try {
                                openFormUsingFormUtils(context, "household_visitation_for_vca_0_20_years_edit", visit);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Error opening the form. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Unable to retrieve case status. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "An unexpected error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("You are about to delete this VCA visit");
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
                JSONObject vcaScreeningForm = formUtils.getFormJson("household_visitation_for_vca_0_20_years_edit");
                try {

                    CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue( visit, Map.class));
                    vcaScreeningForm.put("entity_id", visit.getBase_entity_id());
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
                Intent returnToProfile = new Intent(context, IndexDetailsActivity.class);
                returnToProfile.putExtra("Child",  visit.getUnique_id());
                context.startActivity(returnToProfile);
                ((Activity) context).finish();

            }));

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Alert");
            alert.show();
        });

        Child childModel = IndexPersonDao.getChildByBaseId(visit.getUnique_id());

        if (childModel != null && childModel.getIs_hiv_positive() != null && "yes".equalsIgnoreCase(childModel.getIs_hiv_positive())) {
            holder.exPandableView.setVisibility(View.GONE);
            holder.expMore.setVisibility(View.GONE);
            holder.expLess.setVisibility(View.GONE);
        }
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


        if (childModel != null) {
            String hivStatus = childModel.getIs_hiv_positive();
            if ("yes".equalsIgnoreCase(hivStatus)) {
                holder.intialHivStatus.setText("Positive");
            } else if ("unknown".equalsIgnoreCase(hivStatus)) {
                holder.intialHivStatus.setText("Unknown");
            } else {
                holder.intialHivStatus.setText("Negative");
            }

            holder.initialHivStatusDate.setText(childModel.getDate_screened() != null ? childModel.getDate_screened() : "Date not set");
        }

        if (visit != null) {
            String visitHivStatus = visit.getIs_hiv_positive();
            if ("yes".equalsIgnoreCase(visitHivStatus)) {
                holder.updateHivStatus.setText("Positive");
            } else if ("unknown".equalsIgnoreCase(visitHivStatus)) {
                holder.updateHivStatus.setText("Unknown");
            } else {
                holder.updateHivStatus.setText("Negative");
            }

            holder.updatedHivStatusDate.setText(visit.getVisit_date() != null ? visit.getVisit_date() : "Date not set");
        }

        Household household = HouseholdDao.getHousehold(childModel.getHousehold_id());

        String encodedSignature = visit.getSignature();
        String encodeSignatureHousehold = household.getSignature();


        if(encodedSignature != null && encodedSignature != "") {
            setImageViewFromBase64(encodedSignature, holder.signatureView);
        } else {
            if(encodeSignatureHousehold != null && encodeSignatureHousehold != "") {
                setImageViewFromBase64(encodeSignatureHousehold, holder.signatureView);
            } else {
                holder.signatureView.setVisibility(View.GONE);
            }
        }
    }
    private void setImageViewFromBase64(String base64Str, ImageView imageView) {
        try {
            // Decode the Base64 string into bytes
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);

            // Convert bytes to a Bitmap
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (originalBitmap != null) {
                // Resize the Bitmap to 36x36
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 80, 80, true);

                // Set the resized Bitmap to the ImageView
                imageView.setImageBitmap(resizedBitmap);
            } else {
                Log.e("ImageDecode", "Bitmap is null. Check Base64 input.");
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid Base64 string
            Log.e("ImageDecode", "Invalid Base64 string: " + e.getMessage());
        }
    }
    public void openFormUsingFormUtils(Context context, String formName, VcaVisitationModel visit) throws JSONException {

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
        VcaScreeningModel vcaScreeningModel = VCAScreeningDao.getVcaScreening(visit.getUnique_id());


        Double vAge = getAndCalculateAge(vcaScreeningModel.getAdolescent_birthdate());

        JSONObject hiv_infection = getFieldJSONObject(fields(formToBeOpened, "step1"), "hiv_infection");


        if (vAge >= 10.0 && vAge <= 17.0) {
            hiv_infection.put("type", "native_radio");

        } else {
            hiv_infection.put("type", "hidden");

        }


        JSONObject under_five = getFieldJSONObject(fields(formToBeOpened, "step1"), "under_five");
        JSONObject nutrition_status = getFieldJSONObject(fields(formToBeOpened, "step1"), "nutrition_status");
        if (vAge == 0.5 || vAge == 0.4 || vAge == 0.3 || vAge == 0.1 || vAge == 0.0) {
            under_five.put("type", "native_radio");
            nutrition_status.put("type", "native_radio");
        } else if (vAge <= 5.0) {
            under_five.put("type", "native_radio");
            nutrition_status.put("type", "native_radio");
        } else {
            under_five.put("type", "hidden");
            nutrition_status.put("type", "hidden");
        }

        JSONObject eid_test = getFieldJSONObject(fields(formToBeOpened, "step1"), "eid_test");
        JSONObject age_appropriate = getFieldJSONObject(fields(formToBeOpened, "step1"), "age_appropriate");


        if (vAge == 0.5 || vAge == 0.4 || vAge == 0.3 || vAge == 0.1 || vAge == 0.0) {
            eid_test.put("type", "edit_text");
            age_appropriate.put("type", "native_radio");

        } else if (vAge <= 2.0) {
            eid_test.put("type", "edit_text");
            age_appropriate.put("type", "native_radio");
        } else {
            eid_test.put("type", "hidden");
            age_appropriate.put("type", "hidden");

        }


        JSONObject currently_in_school = getFieldJSONObject(fields(formToBeOpened, "step1"), "currently_in_school");
        JSONObject verified_by_school = getFieldJSONObject(fields(formToBeOpened, "step1"), "verified_by_school");
        JSONObject current_calendar = getFieldJSONObject(fields(formToBeOpened, "step1"), "current_calendar");
        JSONObject school_administration_name = getFieldJSONObject(fields(formToBeOpened, "step1"), "school_administration_name");
        JSONObject telephone_number = getFieldJSONObject(fields(formToBeOpened, "step1"), "telephone_number");
        JSONObject school_administration_date_signed = getFieldJSONObject(fields(formToBeOpened, "step1"), "school_administration_date_signed");
        JSONObject school_administration_signature = getFieldJSONObject(fields(formToBeOpened, "step1"), "school_administration_signature");



        if(vAge < 5.0){
            currently_in_school.put("type", "hidden");
            verified_by_school.put("type", "hidden");
            current_calendar.put("type", "hidden");
            school_administration_name.put("type", "hidden");
            telephone_number.put("type", "hidden");
            school_administration_date_signed.put("type", "hidden");
            school_administration_signature.put("type", "hidden");
        }

        JSONObject question = getFieldJSONObject(fields(formToBeOpened, "step1"), "child_ever_experienced_sexual_violence");

//        if(visit.getChild_ever_experienced_sexual_violence().equals("yes")){

        question.put(JsonFormUtils.VALUE, "yes");
//        } else if (hivRiskAssessmentUnder15Model.getQuestion().equals("no")) {
//            question.put(JsonFormUtils.VALUE, "no");
//        } else{
//            question.put(JsonFormUtils.VALUE, "");
//        }


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

                case "Household Visitation Form 0-20 years Edit":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD_VCA);
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
    @Override
    public int getItemCount() {

        return visits.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate,intialHivStatus,initialHivStatusDate,updateHivStatus,updatedHivStatusDate;

        LinearLayout linearLayout, exPandableView;
        ImageView expMore, expLess,editme, delete;
        ImageView signatureView;

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
            signatureView = itemView.findViewById(R.id.signature_view);

        }


        @Override
        public void onClick(View v) {

        }
    }
    private double getAndCalculateAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today = LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);

        int years = periodBetweenDateOfBirthAndNow.getYears();
        int months = periodBetweenDateOfBirthAndNow.getMonths();

        if (years == 0) {
            if (months >= 10) return 0.5; // 10–11 months
            else if (months >= 7) return 0.4; // 7–9 months
            else if (months >= 4) return 0.3; // 4–6 months
            else if (months >= 1) return 0.1; // 1–3 months
            else return 0.0; // < 1 month
        } else {
            return (double) years;
        }
    }

}
