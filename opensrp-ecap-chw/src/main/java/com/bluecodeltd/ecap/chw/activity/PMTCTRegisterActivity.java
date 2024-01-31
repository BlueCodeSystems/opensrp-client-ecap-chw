package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.family.util.JsonFormUtils.STEP2;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.fragment.PMTCTRegisterFragment;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import com.bluecodeltd.ecap.chw.presenter.PMTCTRegisterPresenter;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class PMTCTRegisterActivity extends BaseRegisterActivity implements IndexRegisterContract.View {

    public String action = null;
    ObjectMapper oMapper;
    ObjectMapper oMapper_hh_screen;
    private UniqueIdRepository uniqueIdRepository;
    Random Number;
    int Rnumber;
    private String uniqueId, hid;
    TextView textCartItemCount;
    int mCartItemCount = 0;
    private ArrayList<VcaVisitationModel> notificationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PMTCTRegisterActivity.this);
        String phone = sp.getString("phone", "anonymous");

        notificationsList.addAll(VcaVisitationDao.getVisitsByCaseWorkerPhone(phone));
        mCartItemCount = notificationsList.size();

    }


    @Override
    protected void initializePresenter() {
        this.presenter = new PMTCTRegisterPresenter(this);
    }

    private IndexRegisterContract.Presenter indexRegisterPresenter(){
        return (PMTCTRegisterPresenter) this.presenter;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new PMTCTRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {

    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            String locationId = Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            ((PMTCTRegisterPresenter) this.presenter).startForm(formName, entityId, metaData, locationId);

        } catch (Exception e) {
            Timber.e(e);
            displayToast(org.smartregister.family.R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

        oMapper = new ObjectMapper();
        oMapper_hh_screen = new ObjectMapper();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PMTCTRegisterActivity.this);
        String code = sp.getString("code", "00000");
        String facility = sp.getString("facility","anonymous");
        String partner = sp.getString("partner","anonymous");
        Object obj = sp.getAll();

        //******** HOUSEHOLD ID ******//
        Number = new Random();
        Rnumber = Number.nextInt(100000000);
        String xId =  Integer.toString(Rnumber);
        String household_id = code + "/" + xId;


        Number = new Random();
        Rnumber = Number.nextInt(900000000);
        String newEntityId =  Integer.toString(Rnumber);

//        //******** POPULATE AS INDEX VCA ******//
//        JSONObject indexCheckObject = getFieldJSONObject(fields(jsonObject, STEP1), "index_check_box");
//
//        if (indexCheckObject != null) {
//            indexCheckObject.remove(JsonFormUtils.VALUE);
//            try {
//                indexCheckObject.put(JsonFormUtils.VALUE, "1");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        //******** POPULATE FACILITY ******//
        JSONObject facilityObject = getFieldJSONObject(fields(jsonObject, STEP1), "health_facility");

        if (facilityObject != null) {
            facilityObject.remove(JsonFormUtils.VALUE);
            try {
                facilityObject.put(JsonFormUtils.VALUE, facility);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        //******** POPULATE PARTNER ******//
//        JSONObject partnerObject = getFieldJSONObject(fields(jsonObject, STEP1), "implementing_partner");

//        if (partnerObject != null) {
//            partnerObject.remove(JsonFormUtils.VALUE);
//            try {
//                partnerObject.put(JsonFormUtils.VALUE, partner);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }




//        //******** POPULATE JSON FORM VCA UNIQUE ID ******//
//        JSONObject stepOneUniqueId = getFieldJSONObject(fields(jsonObject, STEP1), "client_number");
//
//        if (stepOneUniqueId != null) {
//            stepOneUniqueId.remove(JsonFormUtils.VALUE);
//            try {
//                stepOneUniqueId.put(JsonFormUtils.VALUE, newEntityId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
        try {
            //******** POPULATE JSON FORM WITH HOUSEHOLD ID ******//
            CoreJsonFormUtils.populateJsonForm(jsonObject,oMapper.convertValue(obj, Map.class));

            JSONObject stepHouseholdId = getFieldJSONObject(fields(jsonObject, STEP1), "pmtct_id");

//            if (stepHouseholdId != null) {
//                stepHouseholdId.remove(JsonFormUtils.VALUE);
//                try {
//                    stepHouseholdId.put(JsonFormUtils.VALUE, household_id);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        JSONObject dateClientCreated = getFieldJSONObject(fields(jsonObject, STEP1), "date_client_created");
//        if (dateClientCreated  != null) {
//            dateClientCreated.remove(JsonFormUtils.VALUE);
//            try {
//                dateClientCreated.put(JsonFormUtils.VALUE, getFormattedDate());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        JSONObject dateEdited = getFieldJSONObject(fields(jsonObject, STEP1), "date_edited");
//        if (dateEdited  != null) {
//            dateEdited.remove(JsonFormUtils.VALUE);
//            try {
//                dateEdited.put(JsonFormUtils.VALUE, getFormattedDate());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
            Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
            Form form = new Form();
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
            startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }
    private String getFormattedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return today.format(formatter);
    }
    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if(requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK){

            String jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
            try {

                JSONObject jsonFormObject = new JSONObject(jsonString);


                if (Constants.EcapEncounterType.CHILD_INDEX.equalsIgnoreCase(
                        jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))) {
                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));
                    showProgressDialog(R.string.saving_dialog_title);
                    indexRegisterPresenter().saveForm(jsonString, registerParam);
                    uniqueId = getFieldJSONObject(fields(jsonFormObject, STEP1), "unique_id").optString("value");

                   gotToChildProfile(uniqueId);

                } else if(Constants.EcapEncounterType.MOTHER_PMTCT.equalsIgnoreCase(
                        jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))){

                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));
                    showProgressDialog(R.string.saving_dialog_title);
                    indexRegisterPresenter().saveForm(jsonString, registerParam);

                   // hid = getFieldJSONObject(fields(jsonFormObject, STEP2), "household_id").optString("value");

                    //goToHouseholdProfile(hid);

                }

                else if(Constants.EcapEncounterType.HIV_TESTING_SERVICE.equalsIgnoreCase(
                        jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))){

                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));
                    showProgressDialog(R.string.saving_dialog_title);
                    indexRegisterPresenter().saveForm(jsonString, registerParam);

//                    hid = getFieldJSONObject(fields(jsonFormObject, STEP2), "household_id").optString("value");
//
//                    goToHouseholdProfile(hid);
                }
                else if (Constants.EcapEncounterType.FSW.equalsIgnoreCase(
                        jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))) {


                    String age = getFieldJSONObject(fields(jsonFormObject, STEP1), "age").optString("value");
                    String children = getFieldJSONObject(fields(jsonFormObject, STEP1), "children_under18").optString("value");


                    if (Integer.parseInt(age) > 19 && children.equals("yes")){

                        openForm("hh_screening_entry", jsonFormObject);


                    } else if (Integer.parseInt(age) > 10 && Integer.parseInt(age) < 18) {

                        openForm("vca_screening", jsonFormObject);

                    } else {
                        Toasty.error(this, "This FSW Cannot be enrolled into the program", Toast.LENGTH_LONG, true).show();
                    }

                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    public void openForm(String formName, JSONObject jsonFormObject){

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(PMTCTRegisterActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PMTCTRegisterActivity.this);
        Object obj = sp.getAll();


        String fname = getFieldJSONObject(fields(jsonFormObject, STEP1), "first_name").optString("value");
        String lname = getFieldJSONObject(fields(jsonFormObject, STEP1), "last_name").optString("value");
        String dob = getFieldJSONObject(fields(jsonFormObject, STEP1), "adolescent_birthdate").optString("value");
        String hh_id = getFieldJSONObject(fields(jsonFormObject, STEP1), "household_id").optString("value");
        String u_id = getFieldJSONObject(fields(jsonFormObject, STEP1), "unique_id").optString("value");
        String gender = "female";
        String sexually_active = "yes";

        JSONObject indexRegisterForm = formUtils.getFormJson(formName);

        if (formName.equals("vca_screening")){

            JSONObject hh_idObject = getFieldJSONObject(fields(indexRegisterForm, STEP1), "household_id");
            JSONObject unique_idObject = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
            JSONObject first_name = getFieldJSONObject(fields(indexRegisterForm, STEP1), "first_name");
            JSONObject last_name = getFieldJSONObject(fields(indexRegisterForm, STEP1), "last_name");
            JSONObject vca_dob = getFieldJSONObject(fields(indexRegisterForm, STEP1), "adolescent_birthdate");
            JSONObject vca_gender = getFieldJSONObject(fields(indexRegisterForm, STEP1), "gender");
            JSONObject agyActive = getFieldJSONObject(fields(indexRegisterForm, STEP1), "agyw_sexually_active");

            try {
                CoreJsonFormUtils.populateJsonForm(jsonFormObject,oMapper.convertValue(obj, Map.class));
                hh_idObject.put(JsonFormUtils.VALUE, hh_id);
                unique_idObject.put(JsonFormUtils.VALUE, u_id);
                first_name.put(JsonFormUtils.VALUE, fname);
                last_name.put(JsonFormUtils.VALUE, lname);
                vca_dob.put(JsonFormUtils.VALUE, dob);
                vca_gender.put(JsonFormUtils.VALUE, gender);
                agyActive.put(JsonFormUtils.VALUE, sexually_active);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            JSONObject hh_idObject = getFieldJSONObject(fields(indexRegisterForm, STEP2), "household_id");
            JSONObject unique_idObject = getFieldJSONObject(fields(indexRegisterForm, STEP2), "unique_id");
            JSONObject cc_name = getFieldJSONObject(fields(indexRegisterForm, STEP2), "caregiver_name");
            JSONObject cc_dob = getFieldJSONObject(fields(indexRegisterForm, STEP2), "caregiver_birth_date");
            JSONObject cc_sex = getFieldJSONObject(fields(indexRegisterForm, STEP2), "caregiver_sex");


            try {
                CoreJsonFormUtils.populateJsonForm(jsonFormObject,oMapper.convertValue(obj, Map.class));
                hh_idObject.put(JsonFormUtils.VALUE, hh_id);
                unique_idObject.put(JsonFormUtils.VALUE, u_id);
                cc_name.put(JsonFormUtils.VALUE, fname + " " + lname);
                cc_dob.put(JsonFormUtils.VALUE, dob);
                cc_sex.put(JsonFormUtils.VALUE, gender);
                JSONArray subPopulation = getFieldJSONObject(fields(indexRegisterForm, STEP2), "sub_population").getJSONArray("options");
                subPopulation.getJSONObject(5).put("value", "true");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        startFormActivity(indexRegisterForm);

    }

    public void gotToChildProfile(String id){
        Intent intent = new Intent(this,IndexDetailsActivity.class);
        intent.putExtra("Child",id);
        intent.putExtra("fromIndex","321");
        finish();
        startActivity(getIntent());
        Toasty.success(this, "Form Saved", Toast.LENGTH_LONG, true).show();
        startActivity(intent);
    }

    public void goToHouseholdProfile(String id){
        Intent intent = new Intent(this,HouseholdDetails.class);
        intent.putExtra("householdId", id);
        intent.putExtra("fromHousehold","000");
        Toasty.success(this, "Form Saved", Toast.LENGTH_LONG, true).show();
        startActivity(intent);
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void startRegistration() {
        //Overridden
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView,
                    new ChwBottomNavigationListener(this));
            bottomNavigationView.getMenu().removeItem(R.id.action_register);
            bottomNavigationView.getMenu().removeItem(R.id.action_register_index);
            bottomNavigationView.getMenu().removeItem(R.id.action_fsw);
            bottomNavigationView.getMenu().removeItem(R.id.action_hts);
            bottomNavigationView.getMenu().findItem(R.id.action_identifcation).setTitle( "Add Mother");

        }
    }

    @Override
    public void toggleDialogVisibility(boolean showDialog) {
        if (showDialog) {
            showProgressDialog(R.string.saving_index);
        } else {
            hideProgressDialog();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.profilemenu, menu);
//
//        final MenuItem menuItem = menu.findItem(R.id.action_notifications);
//
//        View actionView = menuItem.getActionView();
//        textCartItemCount =  actionView.findViewById(R.id.notification_badge);
//
//        setupBadge();
//
//        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));
//
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        item.setVisible(true);
//        item.setEnabled(false);
//        // Handle item selection
//        switch (item.getItemId()) {
//
//            case R.id.user:
//
//                Intent i = new Intent(this, Profile.class);
//                startActivity(i);
//
//                break;
//
//            case R.id.action_notifications:
//
//                Intent i2 = new Intent(this, NotificationActivity.class);
//                startActivity(i2);
//
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }
    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }


}
