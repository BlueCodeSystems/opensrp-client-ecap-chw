package com.bluecodeltd.ecap.chw.activity;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static org.smartregister.family.util.JsonFormUtils.fields;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.IndexMotherDao;
import com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.EcClientIndexSummary;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.JsonFormUtils;
import com.bluecodeltd.ecap.chw.util.MotherIndexEnrollmentUtils;
import com.bluecodeltd.ecap.chw.util.PmtctEnrollmentUtils;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.appbar.AppBarLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class SignatureActivity extends AppCompatActivity {
    private AppBarLayout myAppbar;
    private Toolbar toolbar;
    public Button clearSignature;
    public Button saveSignature;
    public SignaturePad signaturePad;
    JSONObject screeningFormObject;
    String householdId;
    String childId,intent_vcaid;
    String intent_caregivername;

    String hivstatus;
    String household_id;
    String signature;
    String c_name;
    String intent_cname;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showExitConfirmationDialog();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        screeningFormObject = null;
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = findViewById(R.id.collapsing_toolbar_appbarlayout);
        NavigationMenu.getInstance(this, null, toolbar);  signaturePad = findViewById(R.id.signature_pad);
        // Prevent the pad's bitmap from being embedded in onSaveInstanceState (caused TransactionTooLargeException)
        signaturePad.setSaveEnabled(false);
        clearSignature = findViewById(R.id.clearSignarture);
        saveSignature = findViewById(R.id.saveSignature);
        childId = getIntent().getExtras().getString("Child");
        householdId = getIntent().getExtras().getString("householdId");

        intent_caregivername = getIntent().getExtras().getString("cname");

        intent_vcaid = getIntent().getExtras().getString("vcaid");
        intent_cname = getIntent().getExtras().getString("vcaname");
        hivstatus = getIntent().getExtras().getString("hivstatus");
        household_id = getIntent().getExtras().getString("hh_id");
        c_name = getIntent().getExtras().getString("vcaname");
        signature =  getIntent().getExtras().getString("signature");


        // Get the JSON string from the Bundle
        String jsonString = getIntent().getStringExtra("jsonForm");
        try {
            screeningFormObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        clearSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        saveSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the signature pad is empty
                if (signaturePad.isEmpty()) {
                    Toasty.warning(SignatureActivity.this, "Please provide a signature before saving.", Toast.LENGTH_SHORT, true).show();
                    return; // Exit the method if the signature pad is empty
                }

                // If the signature pad is not empty, proceed with saving
                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                String base64Signature = encodeBitmapToBase64(signatureBitmap);

                JSONObject signatureField = findSignatureField(screeningFormObject);

                if (signatureField != null) {
                    signatureField.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                    try {
                        signatureField.put(JsonFormUtils.VALUE, base64Signature);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                boolean is_edit_mode = false;

                String encounterType = screeningFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");
                if (!screeningFormObject.optString("entity_id").isEmpty()) {
                    is_edit_mode = true;
                }
                debugToast("SAVE: encounter=" + encounterType + " edit=" + is_edit_mode +
                        " entity_id=" + safe(screeningFormObject.optString("entity_id")));

                final boolean isEditModeFinal = is_edit_mode;

                //householdId = FormUtils.getFieldJSONObject(FormUtils.fields(screeningFormObject, STEP2), "household_id").optString("value");

                String signatureString = screeningFormObject.toString();
                try {
                    ChildIndexEventClient childIndexEventClient = processRegistration(signatureString);

                    if (childIndexEventClient == null) {
                        Toasty.error(SignatureActivity.this, "Error processing registration.", Toast.LENGTH_SHORT, true).show();
                        return;
                    }

                    boolean isSaved = saveRegistration(childIndexEventClient, is_edit_mode, () -> {
                    switch (encounterType) {
                        case "Household Screening":
                        case "Household Screening Edit":
                            Toasty.success(getApplicationContext(), "Form Saved", Toast.LENGTH_LONG, true).show();
                            String hid = getFieldValue(screeningFormObject, "step2", "household_id");
                            Intent refreshActivity = new Intent(getApplicationContext(), HouseholdDetails.class);
                            refreshActivity.putExtra("householdId", hid);
                            startActivity(refreshActivity);
                            finish();
                            break;

                        case "Sub Population":
                            String vca_id = getFieldValue(screeningFormObject, "step1", "unique_id");
//                            CommonPersonObjectClient client =(CommonPersonObjectClient) view.getTag();
                            Intent openVcaProfile = new Intent(getApplicationContext(), IndexDetailsActivity.class);
                            openVcaProfile.putExtra("Child", vca_id);
//                            openVcaProfile.putExtra("baseId",  client);
                            startActivity(openVcaProfile);
                            finish();
                            break;

                        case "Household Service Report":
                            if (!isEditModeFinal) {
                                maybeAutoEnrollMotherFromService(screeningFormObject);
                            }
                            Toasty.success(getApplicationContext(), "Service Report Saved", Toast.LENGTH_LONG, true).show();
                            Intent openHouseholdIntent = new Intent(SignatureActivity.this, HouseholdServiceActivity.class);
                            openHouseholdIntent.putExtra("cname", intent_caregivername);
                            openHouseholdIntent.putExtra("householdId", householdId);
                            startActivity(openHouseholdIntent);
                            finish();
                            break;

                        case "VCA Service Report":
                            if (!isEditModeFinal) {
                                maybeAutoEnrollMotherFromVcaService(screeningFormObject);
                            }
                            Toasty.success(getApplicationContext(), "Service Report Saved", Toast.LENGTH_LONG, true).show();
                            Intent openVcaIntent = new Intent(SignatureActivity.this, VcaServiceActivity.class);
                            openVcaIntent.putExtra("vcaid", intent_vcaid);
                            openVcaIntent.putExtra("vcaname", c_name);
                            openVcaIntent.putExtra("hivtstatus", hivstatus);
                            openVcaIntent.putExtra("hh_id", household_id);
                            startActivity(openVcaIntent);
                            finish();
                            break;

                        case "Household Visitation Form 0-20 years":
                            Toasty.success(getApplicationContext(), "Visitation Saved", Toast.LENGTH_LONG, true).show();
                            Intent intent = new Intent(SignatureActivity.this, IndexDetailsActivity.class);
                            intent.putExtra("Child", childId);
                            startActivity(intent);
                            finish();
                            break;

                        case "Household Visitation For Caregiver":
                            Toasty.success(getApplicationContext(), "Visitation Saved", Toast.LENGTH_LONG, true).show();
                            Intent openHouseholdProfile = new Intent(SignatureActivity.this, HouseholdDetails.class);
                            openHouseholdProfile.putExtra("householdId", householdId);
                            startActivity(openHouseholdProfile);
                            finish();
                            break;

                        case "Hiv Assessment For Caregiver":
                            Toasty.success(getApplicationContext(), "HIV assessment Saved", Toast.LENGTH_LONG, true).show();
                            finish();
                            break;

                        case "Referral":
                            Toasty.success(getApplicationContext(), "Referral Saved", Toast.LENGTH_LONG, true).show();
                            finish();
                            break;

                        default:
                            finish();
                            break;
                    }
                    });

                    if (!isSaved) {
                        Toasty.error(SignatureActivity.this, "Failed to save data.", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    Timber.e(e);
                    Toasty.error(SignatureActivity.this, "An error occurred while saving.", Toast.LENGTH_SHORT, true).show();
                }
            }
        });



    }

    private JSONObject findSignatureField(JSONObject form) {
        if (form == null) return null;
        for (String step : new String[]{"step1", "step2", "step3", "step4"}) {
            if (!form.has(step)) continue;
            try {
                JSONObject field = getFieldJSONObject(fields(form, step), "signature");
                if (field != null) {
                    return field;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    String getFieldValue(JSONObject form, String step, String targetKey) {
        if (form == null) return null;
        JSONObject stepObj = form.optJSONObject(step);
        if (stepObj == null) return null;
        JSONArray fields = stepObj.optJSONArray("fields");
        if (fields == null) return null;

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.optJSONObject(i);
            if (field != null && targetKey.equals(field.optString("key"))) {
                // returns null if "value" missing instead of ""
                return field.optString("value", null);
            }
        }
        return null; // not found
    }

    public void goToHouseholdProfile(String id){
        Intent householdProfile = new Intent(getBaseContext(),HouseholdDetails.class);
        householdProfile.putExtra("householdId",id);
        startActivity(householdProfile);
    }


    private String encodeBitmapToBase64(Bitmap signatureBitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit registration?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If the user agrees to exit
                        SignatureActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If the user refuses to exit
                        dialog.dismiss();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                case "Member Sub Population":
                case "Sub Population":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CLIENT_INDEX);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

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

                case "Case Worker Service Report":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_SERVICE_REPORT);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;

                case "Household Screening":
                case "Household Screening Edit":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;

                case "VCA Assessment":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_ASSESSMENT);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;



                case "Household Visitation For Caregiver":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD_CAREGIVER);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "HIV Risk Assessment Above 15":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HIV_ASSESSMENT_ABOVE_15);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "HIV Risk Assessment Below 15":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HIV_ASSESSMENT_BELOW_15);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;


                case "Sub Population Edit":
                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CLIENT_INDEX);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;


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

                case "VCA Service Report":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_VCA_SERVICE_REPORT);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Referral":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_REFERRAL);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Household Visitation Form 0-20 years":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD_VCA);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Hiv Assessment For Caregiver":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable. EC_CAREGIVER_HIV_ASSESSMENT);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;


                case "Household Service Report":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable. EC_HOUSEHOLD_SERVICE);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Mother Pmtct":
                case "Mother PMTCT Register From Service":

                    if (fields != null) {
                        PmtctEnrollmentUtils.alignPmtctIdWithHouseholdId(fields);
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_MOTHER_PMTCT);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Mother Register":
                case "Mother Register From Service":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_MOTHER_INDEX);
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
        return saveRegistration(childIndexEventClient, isEditMode, null);
    }

    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode, Runnable onComplete) {

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

                    if (BuildConfig.DEBUG) {
                        String eventType = safe(event.getEventType());
                        if ("Mother Register From Service".equalsIgnoreCase(eventType)
                                || "Mother PMTCT Register From Service".equalsIgnoreCase(eventType)) {
                            debugCreatedToast("CREATED: " + eventType);
                        }
                    }

                } catch (Exception e) {
                    Timber.e(e);
                }
            }

            if (onComplete != null) {
                Threading.main(onComplete);
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

    private ClientProcessorForJava getClientProcessorForJava() {
        return ChwApplication.getInstance().getClientProcessorForJava();
    }

    public FormTag getFormTag() {
        FormTag formTag = new FormTag();
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
        return formTag;
    }

    private void maybeAutoEnrollMotherFromService(JSONObject serviceForm) {
        if (serviceForm == null) {
            return;
        }
        String services = PmtctEnrollmentUtils.getFieldValue(serviceForm, "services");
        if (!"caregiver".equalsIgnoreCase(safe(services))) {
            return;
        }
        String breastfeeding = PmtctEnrollmentUtils.getFieldValue(serviceForm, "pregnant_breastfeeding");
        if (!"yes".equalsIgnoreCase(safe(breastfeeding))) {
            return;
        }
        String hivStatus = PmtctEnrollmentUtils.getFieldValue(serviceForm, "is_hiv_positive");
        if ("positive".equalsIgnoreCase(safe(hivStatus))) {
            enrollMotherIndexFromService(serviceForm, hivStatus);
            enrollPmtctMotherFromService(serviceForm);
        } else if ("negative".equalsIgnoreCase(safe(hivStatus))) {
            enrollMotherIndexFromService(serviceForm, hivStatus);
        }
    }

    private void enrollMotherIndexFromService(JSONObject serviceForm, String hivStatus) {
        if (householdId == null || householdId.trim().isEmpty()) {
            return;
        }
        Threading.io(() -> {
            try {
                if (IndexMotherDao.hasIndexMother(householdId)) {
                    return;
                }
                Household household = HouseholdDao.getHousehold(householdId);
                if (household == null) {
                    return;
                }
                if (household.getHousehold_id() == null || household.getHousehold_id().trim().isEmpty()) {
                    household.setHousehold_id(householdId);
                }
                if (household.getHousehold_id() == null || household.getHousehold_id().trim().isEmpty()) {
                    return;
                }
                String serviceDate = PmtctEnrollmentUtils.resolveServiceDate(serviceForm);
                JSONObject indexForm = MotherIndexEnrollmentUtils.buildMotherIndexForm(this, household, serviceDate, hivStatus);
                if (indexForm == null) {
                    return;
                }
                indexForm.put(JsonFormConstants.ENCOUNTER_TYPE, "Mother Register From Service");
                ChildIndexEventClient childIndexEventClient = processRegistration(indexForm.toString());
                if (childIndexEventClient == null) {
                    return;
                }
                saveRegistration(childIndexEventClient, false);
            } catch (Exception e) {
                Timber.e(e);
            }
        });
    }

    private void enrollPmtctMotherFromService(JSONObject serviceForm) {
        if (householdId == null || householdId.trim().isEmpty()) {
            return;
        }
        Threading.io(() -> {
            try {
                if (PMTCTMotherDao.hasMotherRecord(householdId)) {
                    return;
                }
                Household household = HouseholdDao.getHousehold(householdId);
                if (household == null) {
                    return;
                }
                if (household.getHousehold_id() == null || household.getHousehold_id().trim().isEmpty()) {
                    household.setHousehold_id(householdId);
                }
                if (household.getHousehold_id() == null || household.getHousehold_id().trim().isEmpty()) {
                    return;
                }
                String serviceDate = PmtctEnrollmentUtils.resolveServiceDate(serviceForm);
                JSONObject pmtctForm = PmtctEnrollmentUtils.buildMotherPmtctForm(this, household, serviceDate);
                if (pmtctForm == null) {
                    return;
                }
                pmtctForm.put(JsonFormConstants.ENCOUNTER_TYPE, "Mother PMTCT Register From Service");
                ChildIndexEventClient childIndexEventClient = processRegistration(pmtctForm.toString());
                if (childIndexEventClient == null) {
                    return;
                }
                saveRegistration(childIndexEventClient, false);
            } catch (Exception e) {
                Timber.e(e);
            }
        });
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private void debugToast(String message) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        Threading.main(() -> {
            try {
                Toasty.info(SignatureActivity.this, message, Toast.LENGTH_LONG, true).show();
            } catch (Exception ignored) {
            }
        });
    }

    private void debugCreatedToast(String message) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        Threading.main(() -> {
            try {
                Toasty.success(SignatureActivity.this, message, Toast.LENGTH_LONG, true).show();
            } catch (Exception ignored) {
            }
        });
    }

    private void maybeAutoEnrollMotherFromVcaService(JSONObject serviceForm) {
        if (serviceForm == null) {
            debugToast("AUTO(VCA): serviceForm null");
            return;
        }

        String breastfeeding = PmtctEnrollmentUtils.getFieldValue(serviceForm, "pregnant_breastfeeding");
        if (!"yes".equalsIgnoreCase(safe(breastfeeding))) {
            debugToast("AUTO(VCA): skip breastfeeding=" + safe(breastfeeding));
            return;
        }

        String hivStatus = PmtctEnrollmentUtils.getFieldValue(serviceForm, "is_hiv_positive");
        String hivStatusSafe = safe(hivStatus);
        debugToast("AUTO(VCA): breastfeeding=yes hiv=" + hivStatusSafe);

        final String vcaId = intent_vcaid;
        if (vcaId == null || vcaId.trim().isEmpty()) {
            debugToast("AUTO(VCA): skip missing vcaId");
            return;
        }

        Threading.io(() -> {
            try {
                EcClientIndexSummary summary = IndexPersonDao.getClientSummaryByUniqueId(vcaId);
                if (summary == null) {
                    debugToast("AUTO(VCA): skip no index summary for " + vcaId);
                    return;
                }

                String gender = safe(summary.getGender());
                if (!"female".equalsIgnoreCase(gender)) {
                    debugToast("AUTO(VCA): skip gender=" + gender);
                    return;
                }

                String baseEntityId = safe(summary.getBaseEntityId());
                if (baseEntityId.isEmpty()) {
                    debugToast("AUTO(VCA): skip baseEntityId empty");
                    return;
                }

                // VCA service form uses is_hiv_positive keys: yes/no/unknown
                if ("no".equalsIgnoreCase(hivStatusSafe)) {
                    String householdIdFromIndex = safe(summary.getHouseholdId());
                    if (householdIdFromIndex.isEmpty()) {
                        debugToast("AUTO(VCA): negative skip householdId empty");
                        return;
                    }
                    if (IndexMotherDao.hasIndexMother(vcaId)) {
                        debugToast("AUTO(VCA): negative skip mother index exists for " + vcaId);
                        return;
                    }

                    JSONObject indexForm = buildMotherIndexFormFromVca(serviceForm, summary, vcaId);
                    if (indexForm == null) {
                        debugToast("AUTO(VCA): negative skip indexForm null");
                        return;
                    }
                    setStep1FieldValue(indexForm, "source_from", "service_report_vca");

                    indexForm.put(JsonFormConstants.ENCOUNTER_TYPE, "Mother Register From Service");
                    indexForm.put("entity_id", "");

                    ChildIndexEventClient childIndexEventClient = processRegistration(indexForm.toString());
                    if (childIndexEventClient == null) {
                        debugToast("AUTO(VCA): negative skip processRegistration null");
                        return;
                    }
                    debugToast("AUTO(VCA): creating Mother Index");
                    saveRegistration(childIndexEventClient, false);
                } else if ("yes".equalsIgnoreCase(hivStatusSafe)) {
                    String householdIdFromIndex = safe(summary.getHouseholdId());
                    if (householdIdFromIndex.isEmpty()) {
                        debugToast("AUTO(VCA): positive skip householdId empty");
                        return;
                    }
                    // Use a targeted check (pmtct_id only) to avoid false positives from the
                    // broader hasMotherRecord subquery, which can match PMTCT records enrolled
                    // via household service after the mother index was created on a prior visit.
                    if (PMTCTMotherDao.hasMotherRecordByHouseholdId(vcaId)) {
                        debugToast("AUTO(VCA): positive skip PMTCT exists for household_id=" + vcaId);
                        return;
                    }
                    Household household = HouseholdDao.getHousehold(householdIdFromIndex);
                    if (household == null) {
                        debugToast("AUTO(VCA): positive skip household null for " + householdIdFromIndex);
                        return;
                    }
                    if (household.getHousehold_id() == null || household.getHousehold_id().trim().isEmpty()) {
                        household.setHousehold_id(householdIdFromIndex);
                    }
                    if (household.getHousehold_id() == null || household.getHousehold_id().trim().isEmpty()) {
                        debugToast("AUTO(VCA): positive skip household_id empty");
                        return;
                    }
                    String serviceDate = safe(PmtctEnrollmentUtils.resolveServiceDate(serviceForm));
                    JSONObject pmtctForm = PmtctEnrollmentUtils.buildMotherPmtctForm(this, household, serviceDate);
                    if (pmtctForm == null) {
                        debugToast("AUTO(VCA): positive skip pmtctForm null");
                        return;
                    }
                    pmtctForm.put(JsonFormConstants.ENCOUNTER_TYPE, "Mother PMTCT Register From Service");
                    setStep1FieldValue(pmtctForm, "household_id", vcaId);
                    setStep1FieldValue(pmtctForm, "source_from", "service_report_vca");
                    // Use the VCA's own details, not the household caregiver's.
                    String vcaFullName = (safe(summary.getFirstName()) + " " + safe(summary.getLastName())).trim();
                    if (!vcaFullName.isEmpty()) {
                        setStep1FieldValue(pmtctForm, "caregiver_name", vcaFullName);
                    }
                    setStep1FieldValue(pmtctForm, "first_name", safe(summary.getFirstName()));
                    setStep1FieldValue(pmtctForm, "last_name", safe(summary.getLastName()));
                    setStep1FieldValue(pmtctForm, "caregiver_birth_date", safe(summary.getAdolescentBirthdate()));
                    setStep1FieldValue(pmtctForm, "district", safe(summary.getDistrict()));
                    setStep1FieldValue(pmtctForm, "ward", safe(summary.getWard()));
                    pmtctForm.put("entity_id", "");
                    ChildIndexEventClient childIndexEventClient = processRegistration(pmtctForm.toString());
                    if (childIndexEventClient == null) {
                        debugToast("AUTO(VCA): positive skip processRegistration null");
                        return;
                    }
                    debugToast("AUTO(VCA): creating PMTCT date=" + serviceDate);
                    saveRegistration(childIndexEventClient, false);
                } else {
                    debugToast("AUTO(VCA): skip hiv=" + hivStatusSafe);
                }
            } catch (Exception e) {
                Timber.e(e);
                debugToast("AUTO(VCA): exception=" + e.getClass().getSimpleName());
            }
        });
    }

    private JSONObject buildMotherIndexFormFromVca(JSONObject vcaServiceForm, EcClientIndexSummary summary, String vcaUniqueId) {
        try {
            FormUtils formUtils = new FormUtils(this);
            JSONObject form = formUtils.getFormJson("mother_index");
            if (form == null) {
                return null;
            }

            String fullName = (safe(summary.getFirstName()) + " " + safe(summary.getLastName())).trim();
            if (!fullName.isEmpty()) {
                setStep1FieldValue(form, "caregiver_name", fullName);
            }

            String birthdate = safe(summary.getAdolescentBirthdate());
            if (!birthdate.isEmpty()) {
                setStep1FieldValue(form, "caregiver_birth_date", birthdate);
            }

            setStep1FieldValue(form, "caregiver_sex", "female");

            // service_report_vca uses is_hiv_positive keys: yes/no/unknown
            // mother_index uses caregiver_hiv_status keys: positive/negative
            String hivStatusRaw = safe(PmtctEnrollmentUtils.getFieldValue(vcaServiceForm, "is_hiv_positive"));
            if ("yes".equalsIgnoreCase(hivStatusRaw)) {
                setStep1FieldValue(form, "caregiver_hiv_status", "positive");
            } else if ("no".equalsIgnoreCase(hivStatusRaw)) {
                setStep1FieldValue(form, "caregiver_hiv_status", "negative");
            }

            // Mother index household_id should use the VCA unique_id for this flow.
            String household = safe(vcaUniqueId);
            if (!household.isEmpty()) {
                setStep1FieldValue(form, "household_id", household);
            }

            // District/Ward should come from ec_client_index, not household.
            setStep1FieldValue(form, "district", safe(summary.getDistrict()));
            setStep1FieldValue(form, "ward", safe(summary.getWard()));

            String serviceDate = safe(PmtctEnrollmentUtils.resolveServiceDate(vcaServiceForm));
            if (!serviceDate.isEmpty()) {
                setStep1FieldValue(form, "mother_screening_date", serviceDate);
            }

            return form;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private void setStep1FieldValue(JSONObject form, String key, String value) {
        if (form == null || key == null || key.trim().isEmpty() || value == null || value.trim().isEmpty()) {
            return;
        }
        try {
            JSONObject step = form.optJSONObject(JsonFormConstants.STEP1);
            if (step == null) {
                return;
            }
            JSONArray fields = step.optJSONArray(JsonFormConstants.FIELDS);
            if (fields == null) {
                return;
            }
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.optJSONObject(i);
                if (field != null && key.equals(field.optString(JsonFormConstants.KEY))) {
                    field.put(JsonFormConstants.VALUE, value);
                    return;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }



}
