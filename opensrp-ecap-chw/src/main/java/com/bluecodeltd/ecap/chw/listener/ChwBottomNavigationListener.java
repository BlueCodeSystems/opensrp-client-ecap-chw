package com.bluecodeltd.ecap.chw.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.DashboardActivity;
import com.bluecodeltd.ecap.chw.activity.FamilyRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.HivTestingServiceActivity;
import com.bluecodeltd.ecap.chw.activity.HouseholdIndexActivity;
import com.bluecodeltd.ecap.chw.activity.IndexRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.MotherIndexActivity;

import org.json.JSONObject;
import org.smartregister.chw.core.listener.CoreBottomNavigationListener;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import timber.log.Timber;

public class ChwBottomNavigationListener extends CoreBottomNavigationListener {
    private Activity context;

    public ChwBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_family) {
            if (context instanceof DashboardActivity) {
//                BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;
//                baseRegisterActivity.switchToBaseFragment();
            } else {
//                Intent intent = new Intent(context, IndexRegisterActivity.class);
//                intent.putExtra("username", "");
//                intent.putExtra("password", "");
//                context.startActivity(intent);
//                context.finish();
                Intent intent = new Intent(context,DashboardActivity.class);
                context.startActivity(intent);
            }
        } else if (item.getItemId() == R.id.action_scan_qr) {
            BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;
            baseRegisterActivity.startQrCodeScanner();
            return false;
        } else if (item.getItemId() == R.id.action_register_index) {

            if ( context instanceof IndexRegisterActivity ) {

                IndexRegisterActivity idRegisterActivity = (IndexRegisterActivity) context;

                try {

                    FormUtils formUtils = new FormUtils(context);

                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("vca_screening");

                    idRegisterActivity.startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    Timber.e(e);
                }

            }
            else if ( context instanceof IndexRegisterActivity ) {

                IndexRegisterActivity idRegisterActivity = (IndexRegisterActivity) context;

                try {

                    FormUtils formUtils = new FormUtils(context);

                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("household_visitation_for_vca_0_20_years");

                    idRegisterActivity.startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    Timber.e(e);
                }

            }



            else if(context instanceof MotherIndexActivity){

                MotherIndexActivity motherIndexActivity = (MotherIndexActivity) context;

                try {
                    FormUtils formUtils = new FormUtils(context);

                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("mother_index");

                    motherIndexActivity.startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    Timber.e(e);
                }
            }else if(context instanceof HouseholdIndexActivity){

                HouseholdIndexActivity householdIndexActivity = (HouseholdIndexActivity) context;

                try {

                    householdIndexActivity.startFormActivity("hh_screening_entry",null,"");

                } catch (Exception e) {
                    Timber.e(e);
                }
            }
            return true;
        } else if (item.getItemId() == R.id.action_register) {
            FamilyRegisterActivity.startFamilyRegisterForm(context);
            return true;
        }
        else if (item.getItemId() == R.id.action_fsw) {

            if ( context instanceof IndexRegisterActivity) {

                IndexRegisterActivity idRegisterActivity = (IndexRegisterActivity) context;

                try {

                    idRegisterActivity.startFormActivity("female_sex_worker",null,"");

                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
        else if (item.getItemId() == R.id.action_hts) {

            if ( context instanceof HivTestingServiceActivity) {

                HivTestingServiceActivity hivTestingServiceActivity = (HivTestingServiceActivity) context;

                try {

                    hivTestingServiceActivity.startFormActivity("hiv_testing_service",null,"");

                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }

        else if (item.getItemId() == R.id.action_identifcation) {
            if ( context instanceof IndexRegisterActivity ) {

                IndexRegisterActivity idRegisterActivity = (IndexRegisterActivity) context;

                try {

                    idRegisterActivity.startFormActivity("vca_screening",null,"");

                } catch (Exception e) {
                    Timber.e(e);
                }

            } else if(context instanceof MotherIndexActivity){

                MotherIndexActivity motherIndexActivity = (MotherIndexActivity) context;

                try {
                    FormUtils formUtils = new FormUtils(context);

                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("mother_index");

                    motherIndexActivity.startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    Timber.e(e);
                }
            } else if(context instanceof HouseholdIndexActivity) {

                HouseholdIndexActivity householdIndexActivity = (HouseholdIndexActivity) context;

                try {

                    householdIndexActivity.startFormActivity("hh_screening_entry",null,"");

                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }

        return true;
    }
}
