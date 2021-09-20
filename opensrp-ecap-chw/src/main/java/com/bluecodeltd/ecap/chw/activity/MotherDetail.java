package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import timber.log.Timber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.UUID;

public class MotherDetail extends AppCompatActivity {

    private FloatingActionButton fab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private RelativeLayout rhousehold, rassessment, rcase_plan, referral;
    private TextView txtName, txtVillage, txtPhone, txtArt, txtFacility, txtOvc, txtComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mother_detail);

        fab = findViewById(R.id.fabx);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        rassessment = findViewById(R.id.cassessment);
        rcase_plan = findViewById(R.id.ccase_plan);

        txtName = findViewById(R.id.mynamex);
        txtFacility = findViewById(R.id.mfacilityx);
        txtVillage = findViewById(R.id.village);
        txtPhone = findViewById(R.id.phone);
        txtArt = findViewById(R.id.art);
        txtOvc = findViewById(R.id.ovc_date);
        txtComment = findViewById(R.id.comment);

        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("mothers");

        assert client != null;

        String full_name = client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name");
        String facility =  client.getColumnmaps().get("health_facility");
        String village =  client.getColumnmaps().get("village_compound");
        String phone =  client.getColumnmaps().get("mothers_number");
        String art =  client.getColumnmaps().get("art_number");
        String ovc_date =  client.getColumnmaps().get("ovc_date");
        String comment =  client.getColumnmaps().get("comment");

        txtName.setText(full_name);
        txtFacility.setText(facility);
        txtVillage.setText(village);
        txtPhone.setText(phone);
        txtArt.setText(art);
        txtOvc.setText(ovc_date);
        txtComment.setText(comment);

    }



    public void startFormActivity(JSONObject jsonObject) {


        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);

        Form form = new Form();
        try {
            if (jsonObject.has(JsonFormConstants.ENCOUNTER_TYPE) &&
                    jsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE)
                            .equalsIgnoreCase(Constants.EcapEncounterType.CHILD_INDEX)) {
                form.setWizard(true);
                form.setName(getString(R.string.child_details));
                form.setHideSaveLabel(true);
                form.setNextLabel(getString(R.string.next));
                form.setPreviousLabel(getString(R.string.previous));
                form.setSaveLabel(getString(R.string.submit));
                form.setNavigationBackground(R.color.primary);
            } else {
                form.setWizard(false);
                form.setHideSaveLabel(true);
                form.setNextLabel("");
            }
            intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        } catch (JSONException e) {
            Timber.e(e);
        }
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }

    public void onClick(View v) {
        int id = v.getId();
        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("mothers");
       // assert client != null;
        switch (id){
            case R.id.fabx:

                animateFAB();

                break;
            case R.id.cassessment:

                try {
                    FormUtils formUtils = new FormUtils(MotherDetail.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("caregiver_assessment");

                    //  startFormActivity(indexRegisterForm);
                    Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);

                    Form form = new Form();
                    try {
                        if (indexRegisterForm.has(JsonFormConstants.ENCOUNTER_TYPE) &&
                                indexRegisterForm.getString(JsonFormConstants.ENCOUNTER_TYPE)
                                        .equalsIgnoreCase(Constants.EcapEncounterType.CHILD_INDEX)) {
                            form.setWizard(true);
                            form.setName("Caregiver Vulnerability Assessment");
                            form.setHideSaveLabel(true);
                            form.setNextLabel(getString(R.string.next));
                            form.setPreviousLabel(getString(R.string.previous));
                            form.setSaveLabel(getString(R.string.submit));
                            form.setNavigationBackground(R.color.primary);
                        } else {
                            form.setWizard(false);
                            form.setHideSaveLabel(true);
                            form.setNextLabel("");
                        }
                        intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
                    } catch (JSONException e) {
                        Timber.e(e);
                    }

                    //TODO Pre populate fields here

                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, indexRegisterForm.toString());
                    startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.ccase_plan:

                try {
                    FormUtils formUtils = new FormUtils(MotherDetail.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("caregiver_case_plan");

                    //  startFormActivity(indexRegisterForm);
                    Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);

                    Form form = new Form();
                    try {
                        if (indexRegisterForm.has(JsonFormConstants.ENCOUNTER_TYPE) &&
                                indexRegisterForm.getString(JsonFormConstants.ENCOUNTER_TYPE)
                                        .equalsIgnoreCase(Constants.EcapEncounterType.CHILD_INDEX)) {
                            form.setWizard(true);
                            form.setName("Caregiver Case Plan");
                            form.setHideSaveLabel(true);
                            form.setNextLabel(getString(R.string.next));
                            form.setPreviousLabel(getString(R.string.previous));
                            form.setSaveLabel(getString(R.string.submit));
                            form.setNavigationBackground(R.color.primary);
                        } else {
                            form.setWizard(false);
                            form.setHideSaveLabel(true);
                            form.setNextLabel("");
                        }
                        intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
                    } catch (JSONException e) {
                        Timber.e(e);
                    }

                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, indexRegisterForm.toString());
                    startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }
    public void animateFAB(){


        if (isFabOpen){

            fab.startAnimation(rotate_backward);
            isFabOpen = false;
            rhousehold.setVisibility(View.GONE);
            rassessment.setVisibility(View.GONE);
            rcase_plan.setVisibility(View.GONE);
            referral.setVisibility(View.GONE);

        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);
            rassessment.setVisibility(View.VISIBLE);
            rcase_plan.setVisibility(View.VISIBLE);

        }
    }

}