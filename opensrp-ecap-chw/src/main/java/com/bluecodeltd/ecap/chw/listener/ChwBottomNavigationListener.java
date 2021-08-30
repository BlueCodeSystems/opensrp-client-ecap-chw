package com.bluecodeltd.ecap.chw.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import timber.log.Timber;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.FamilyRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.activity.IndexRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.JobAidsActivity;
import com.bluecodeltd.ecap.chw.activity.MotherIndexActivity;
import com.bluecodeltd.ecap.chw.activity.ReportsActivity;
import com.bluecodeltd.ecap.chw.custom_view.AlertDialogForIndexRegistrationRegister;
import com.bluecodeltd.ecap.chw.util.Constants;

import org.json.JSONObject;
import org.smartregister.chw.core.listener.CoreBottomNavigationListener;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

public class ChwBottomNavigationListener extends CoreBottomNavigationListener {
    private Activity context;

    public ChwBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_family) {
            if (context instanceof FamilyRegisterActivity) {
                BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;
                baseRegisterActivity.switchToBaseFragment();
            } else {
                Intent intent = new Intent(context, FamilyRegisterActivity.class);
                context.startActivity(intent);
                context.finish();
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
            }
            return true;
        } else if (item.getItemId() == R.id.action_register) {
            FamilyRegisterActivity.startFamilyRegisterForm(context);
            return true;
        } else if (item.getItemId() == R.id.action_identifcation) {
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
            }
            return true;
        } else if (item.getItemId() == R.id.action_report) {
            Intent intent = new Intent(context, ReportsActivity.class);
            context.startActivity(intent);
            return false;
        }

        return true;
    }
}
