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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.CaregiverHivAssessmentModel;
import com.bluecodeltd.ecap.chw.model.Household;
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

public class CaregiverHivAssessmentAdapter extends RecyclerView.Adapter<CaregiverHivAssessmentAdapter.ViewHolder> {
    Context context;
    List<CaregiverHivAssessmentModel> hivAssessment;
    ObjectMapper oMapper;

    public CaregiverHivAssessmentAdapter(Context context, List<CaregiverHivAssessmentModel> hivAssessment) {
        this.context = context;
        this.hivAssessment = hivAssessment;
    }

    @NonNull
    @Override
    public CaregiverHivAssessmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.caregiver_hiv_assessment_list, parent, false);
        CaregiverHivAssessmentAdapter.ViewHolder viewHolder = new CaregiverHivAssessmentAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CaregiverHivAssessmentAdapter.ViewHolder holder, int position) {
        final CaregiverHivAssessmentModel visit = hivAssessment.get(position);

        holder.setIsRecyclable(false);
        String rowTag = (visit.getBase_entity_id() != null && !visit.getBase_entity_id().isEmpty())
                ? visit.getBase_entity_id()
                : (visit.getHousehold_id() + ":" + position);
        holder.itemView.setTag(R.id.tag_row_id, rowTag);

        holder.txtDate.setText(visit.getDate_edited());
        holder.intialHivStatus.setText("Unknown");
        holder.initialHivStatusDate.setText("");

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


        Threading.ioBestEffort(() -> {
            Household householdModel = null;
            try { householdModel = HouseholdDao.getHousehold(visit.getHousehold_id()); } catch (Exception ignored) {}
            String caregiverHivStatus = householdModel != null ? householdModel.getCaregiver_hiv_status() : null;
            String screeningDate = householdModel != null ? householdModel.getScreening_date() : null;
            Threading.main(() -> {
                Object currentTag = holder.itemView.getTag(R.id.tag_row_id);
                if (!(currentTag instanceof String) || !rowTag.equals(currentTag)) return;
                if(caregiverHivStatus != null && caregiverHivStatus.equals("positive")){
                    holder.intialHivStatus.setText("Positive");
                } else if("unknown".equals(caregiverHivStatus)) {
                    holder.intialHivStatus.setText("Unknown");
                } else if (caregiverHivStatus != null) {
                    holder.intialHivStatus.setText("Negative");
                } else {
                    holder.intialHivStatus.setText("Unknown");
                }
                holder.initialHivStatusDate.setText(screeningDate != null ? screeningDate : "");
            });
        });
        try {
            if (visit.getHiv_status() != null && visit.getHiv_status().equals("positive")) {
                holder.updateHivStatus.setText("Positive");
            } else if (visit.getHiv_status().equals("unknown")) {
                holder.updateHivStatus.setText("Unknown");
            } else {
                holder.updateHivStatus.setText("Negative");
            }
        } catch (NullPointerException e) {
            holder.updateHivStatus.setText("Unknown");
        }


        holder.updatedHivStatusDate.setText(visit.getStart_date());


        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                try {

                    openFormUsingFormUtils(context, "hh_hiv_assessment_caregiver_edit", visit);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        holder.editme.setOnClickListener(v -> {
            Threading.io(() -> {
                Household household = null;
                try { household = HouseholdDao.getHousehold(visit.getHousehold_id()); } catch (Exception ignored) {}
                Household finalHousehold = household;
                Threading.main(() -> {
                    if (finalHousehold != null && finalHousehold.getHousehold_case_status() != null &&
                            ("0".equals(finalHousehold.getHousehold_case_status()) || "2".equals(finalHousehold.getHousehold_case_status()))) {
                        showDialogBox(finalHousehold.getCaregiver_name(), "`s has been inactive or de-registered");
                    } else{
                        if (v.getId() == R.id.edit_me) {
                            try {
                                openFormUsingFormUtils(context, "hh_hiv_assessment_caregiver_edit", visit);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            });

        });
        holder.delete.setOnClickListener(v -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("You are about to delete this household visit ");
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
                    JSONObject vcaScreeningForm = formUtils.getFormJson("hh_hiv_assessment_caregiver");
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


        String encodedSignature = visit.getSignature();
        holder.signatureView.setVisibility(View.GONE);
        if(encodedSignature != null && !encodedSignature.isEmpty()) {
            setImageViewFromBase64(encodedSignature, holder.signatureView);
            holder.signatureView.setVisibility(View.VISIBLE);
        } else {
            Threading.ioBestEffort(() -> {
                Household household = null;
                try { household = HouseholdDao.getHousehold(visit.getHousehold_id()); } catch (Exception ignored) {}
                String encodeSignatureHousehold = household != null ? household.getSignature() : null;
                Threading.main(() -> {
                    Object currentTag = holder.itemView.getTag(R.id.tag_row_id);
                    if (!(currentTag instanceof String) || !rowTag.equals(currentTag)) return;
                    if(encodeSignatureHousehold != null && !encodeSignatureHousehold.isEmpty()) {
                        setImageViewFromBase64(encodeSignatureHousehold, holder.signatureView);
                        holder.signatureView.setVisibility(View.VISIBLE);
                    } else {
                        holder.signatureView.setVisibility(View.GONE);
                    }
                });
            });
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
    public void showDialogBox(String caregiverName,String message){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();

        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        dialogMessage.setText((caregiverName != null ? caregiverName : "") + message);

        android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

    }

    public void openFormUsingFormUtils(Context context, String formName, CaregiverHivAssessmentModel visit) throws JSONException {

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

                case "Hiv Assessment For Caregiver Edit":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable. EC_CAREGIVER_HIV_ASSESSMENT);
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

        return hivAssessment.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate,intialHivStatus,initialHivStatusDate,updateHivStatus,updatedHivStatusDate;

        LinearLayout linearLayout, exPandableView;
        ImageView expMore, expLess,editme,delete;
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
}
