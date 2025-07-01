package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
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
import com.bluecodeltd.ecap.chw.activity.HouseholdServiceActivity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.HouseholdServiceReportModel;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HouseholdServiceAdapter extends RecyclerView.Adapter<HouseholdServiceAdapter.ViewHolder>{


    Context context;
    List<HouseholdServiceReportModel> services;
    ObjectMapper oMapper;
    private static final long REFRESH_DELAY = 100;
    private Handler handler = new Handler();

    public interface OnDataUpdateListener {
        void onDataUpdate();
    }
    private HouseholdServiceAdapter.OnDataUpdateListener onDataUpdateListener;

    public void setOnDataUpdateListener(HouseholdServiceAdapter.OnDataUpdateListener onDataUpdateListener) {
        this.onDataUpdateListener = onDataUpdateListener;
    }


    public HouseholdServiceAdapter(List<HouseholdServiceReportModel> services, Context context){

        super();

        this.services = services;
        this.context = context;

    }

    @Override
    public HouseholdServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service, parent, false);

        HouseholdServiceAdapter.ViewHolder viewHolder = new HouseholdServiceAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HouseholdServiceAdapter.ViewHolder holder, final int position) {

        final HouseholdServiceReportModel service = services.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(service.getDate());
//        holder.txtServices.setText("household");
        holder.txtserviceType.setText(service.getServices());
        //holder.txtserviceType.setText(service.getServices());

//        if(service.getServices().equals("caregiver")){
//
//            JSONArray jsonArray = null;
//            try {
//                jsonArray = new JSONArray(service.getServices_caregiver());
//
//                String[] strArr = new String[jsonArray.length()];
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    strArr[i] = jsonArray.getString(i);
//                }
//
//                holder.txtServices.setText(strArr.length + " Services");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//
//            JSONArray jsonArray = null;
//            try {
//                jsonArray = new JSONArray(service.getServices_household());
//
//                String[] strArr = new String[jsonArray.length()];
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    strArr[i] = jsonArray.getString(i);
//                }
//
//                holder.txtServices.setText(strArr.length + " Services");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
        Household household = HouseholdDao.getHousehold(service.getHousehold_id());


        String encodedSignature = service.getSignature();
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
        holder.edit.setOnClickListener(v -> {
            if (household.getHousehold_case_status() != null &&
                    (household.getHousehold_case_status().equals("0") || household.getHousehold_case_status().equals("2"))) {
                showDialogBox(service.getHousehold_id(), "`s has been inactive or de-registered");
            } else {
                try {
                    FormUtils formUtils = new FormUtils(context);
                    openFormUsingFormUtils(context, "service_report_household_edit", service);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.linearLayout.setOnClickListener(v -> {

           if (household.getHousehold_case_status() != null && (household.getHousehold_case_status().equals("0") || household.getHousehold_case_status().equals("2"))) {
                showDialogBox(service.getHousehold_id(), "`s has been inactive or de-registered");
            } else {
                if (v.getId() == R.id.itemm) {

                    FormUtils formUtils = null;
                    try {
                        formUtils = new FormUtils(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        openFormUsingFormUtils(context, "service_report_household_edit", service);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
        holder.delete.setOnClickListener(v -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("You are about to delete this household service ");
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
                    service.setDelete_status("1");
                    JSONObject vcaScreeningForm = formUtils.getFormJson("service_report_household");
                    try {
                        CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue(service, Map.class));
                        vcaScreeningForm.put("entity_id", service .getBase_entity_id());
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
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }

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
    public void refreshActivity() {
        handler.postDelayed(refreshRunnable, REFRESH_DELAY);
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            Activity activity = (HouseholdServiceActivity) context;
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

    public void openFormUsingFormUtils(Context context, String formName, HouseholdServiceReportModel service) throws JSONException {

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
        HouseholdServiceReportModel householdReport = new HouseholdServiceReportModel();
        householdReport.setServices(service.getServices());
        householdReport.setServices_household(service.getServices_household());

        if (service.getHealth_services() == null && service.getServices_caregiver() != null){
            householdReport.setHealth_services(service.getServices_caregiver());
        } else {
            householdReport.setHealth_services(service.getHealth_services());
        }

        householdReport.setOther_health_services(service.getOther_health_services());
        householdReport.setHh_service_location(service.getHh_service_location());
        householdReport.setSchooled_services(service.getSchooled_services());
        householdReport.setOther_schooled_services(service.getOther_schooled_services());
        householdReport.setSafe_services(service.getSafe_services());
        householdReport.setOther_safe_services(service.getOther_safe_services());
        householdReport.setStable_services(service.getStable_services());
        householdReport.setOther_stable_services(service.getOther_stable_services());
        householdReport.setHh_level_services(service.getHh_level_services());
        householdReport.setOther_hh_level_services(service.getOther_hh_level_services());
        householdReport.setDate(service.getDate());
        householdReport.setIs_hiv_positive(service.getIs_hiv_positive());
        householdReport.setArt_clinic(service.getArt_clinic());
        householdReport.setDate_last_vl(service.getDate_last_vl());
        householdReport.setVl_last_result(service.getVl_last_result());
        householdReport.setDate_next_vl(service.getDate_next_vl());
        householdReport.setCaregiver_mmd(service.getCaregiver_mmd());
        householdReport.setLevel_mmd(service.getLevel_mmd());
        householdReport.setHousehold_id(service.getHousehold_id());
        householdReport.setBase_entity_id(service.getBase_entity_id());
        householdReport.setOther_services_caregiver(service.getOther_services_caregiver());
        householdReport.setOther_services_household(service.getOther_services_household());
        householdReport.setDelete_status(service.getDelete_status());
        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(householdReport, Map.class));

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

                case "Household Service Report Edit":
                case "Household Service Report":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_household_service_report");
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

        return services.size();
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


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate,txtserviceType, txtServices ;
        ImageView delete, edit;
        ImageView signatureView;
        LinearLayout linearLayout;


        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtDate  = itemView.findViewById(R.id.date);
            txtserviceType = itemView.findViewById(R.id.service);
            txtServices = itemView.findViewById(R.id.services);
            delete = itemView.findViewById(R.id.delete_record);
            signatureView = itemView.findViewById(R.id.signature_view);
            edit = itemView.findViewById(R.id.edit_me);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
