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

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;
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

public class CaregiverVisitAdapter extends RecyclerView.Adapter<CaregiverVisitAdapter.ViewHolder>{

    Context context;

    List<CaregiverVisitationModel> visits;
    ObjectMapper oMapper;

    public CaregiverVisitAdapter(List<CaregiverVisitationModel> visits, Context context){

        super();

        this.visits = visits;
        this.context = context;
    }

    @Override
    public CaregiverVisitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_visit, parent, false);

        CaregiverVisitAdapter.ViewHolder viewHolder = new CaregiverVisitAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CaregiverVisitAdapter.ViewHolder holder, final int position) {

        final CaregiverVisitationModel visit = visits.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(visit.getVisit_date());

        final String rowTag = visit.getBase_entity_id() != null ? visit.getBase_entity_id()
                : (visit.getHousehold_id() != null ? visit.getHousehold_id() + ":" + position : String.valueOf(position));
        holder.itemView.setTag(R.id.tag_row_id, rowTag);

        String encodedSignature = visit.getSignature();
        if (encodedSignature != null && !encodedSignature.isEmpty()) {
            setImageViewFromBase64(encodedSignature, holder.signatureView);
        } else {
            holder.signatureView.setVisibility(View.GONE);
        }

        holder.intialHivStatus.setText("Loading…");
        holder.initialHivStatusDate.setText("");

        final String hid = visit.getHousehold_id();
        Threading.ioBestEffort(() -> {
            Household household = null;
            try { household = HouseholdDao.getHousehold(hid); } catch (Exception ignored) {}
            final Household finalHousehold = household;
            Threading.main(() -> {
                Object tag = holder.itemView.getTag(R.id.tag_row_id);
                if (!(tag instanceof String) || !rowTag.equals(tag)) return;

                if (finalHousehold != null) {
                    String hSig = finalHousehold.getSignature();
                    if ((encodedSignature == null || encodedSignature.isEmpty()) && hSig != null && !hSig.isEmpty()) {
                        holder.signatureView.setVisibility(View.VISIBLE);
                        setImageViewFromBase64(hSig, holder.signatureView);
                    }

                    String caregiverStatus = finalHousehold.getCaregiver_hiv_status();
                    if ("positive".equalsIgnoreCase(caregiverStatus)) {
                        holder.exPandableView.setVisibility(View.GONE);
                        holder.expMore.setVisibility(View.GONE);
                        holder.expLess.setVisibility(View.GONE);
                        holder.intialHivStatus.setText("Positive");
                    } else if ("unknown".equalsIgnoreCase(caregiverStatus)) {
                        holder.intialHivStatus.setText("Unknown");
                    } else {
                        holder.intialHivStatus.setText("Negative");
                    }
                    holder.initialHivStatusDate.setText(finalHousehold.getScreening_date());
                } else {
                    holder.intialHivStatus.setText("N/A");
                }
            });
        });
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


        // initial HIV status/date are set asynchronously above

        if(visit.getCaregiver_hiv_status() != null && visit.getCaregiver_hiv_status().equals("positive")){
            holder.updateHivStatus.setText("Positive");
        } else if (visit.getCaregiver_hiv_status().equals("unknown")) {
            holder.updateHivStatus.setText("Unknown");

        } else {
            holder.updateHivStatus.setText("Negative");
        }
        holder.updatedHivStatusDate.setText(visit.getVisit_date());


        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                try {

                    openFormUsingFormUtils(context, "household_visitation_for_caregiver_edit", visit);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        holder.editme.setOnClickListener(v -> Threading.io(() -> {
            Household household = null;
            try { household = HouseholdDao.getHousehold(hid); } catch (Exception ignored) {}
            final Household finalHousehold = household;
            Threading.main(() -> {
                Object tag = holder.itemView.getTag(R.id.tag_row_id);
                if (!(tag instanceof String) || !rowTag.equals(tag)) return;
                String status = finalHousehold != null ? finalHousehold.getHousehold_case_status() : null;
                if (status != null && ("0".equals(status) || "2".equals(status))) {
                    showDialogBox(hid, "`s has been inactive or de-registered");
                } else {
                    try {
                        openFormUsingFormUtils(context, "household_visitation_for_caregiver_edit", visit);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }));
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
                    JSONObject vcaScreeningForm = formUtils.getFormJson("household_visitation_for_caregiver_edit");
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

    public void openFormUsingFormUtils(Context context, String formName, CaregiverVisitationModel visit) throws JSONException {

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

                case "Household Visitation For Caregiver Edit":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_household_visitation_for_caregiver");
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

    private void setImageViewFromBase64(String base64Str, ImageView imageView) {
        try {

            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (originalBitmap != null) {
                // Resize the Bitmap to 36x36
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 80, 80, true);
                imageView.setImageBitmap(resizedBitmap);
            } else {
                Log.e("ImageDecode", "Bitmap is null. Check Base64 input.");
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid Base64 string
            Log.e("ImageDecode", "Invalid Base64 string: " + e.getMessage());
        }
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
