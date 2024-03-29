package com.bluecodeltd.ecap.chw.custom_view;

import android.app.AlertDialog;
import android.content.Context;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexRegisterActivity;
import com.bluecodeltd.ecap.chw.util.Constants;

import org.json.JSONObject;
import org.smartregister.util.FormUtils;

import timber.log.Timber;

public class AlertDialogForVcaProfile {
    Context context;
    String[] items = {"VCA Screening", "Mother","Sex Worker"};
    String selectedItem;

    public AlertDialogForVcaProfile(Context context) {
        this.context = context;
    }

    public String[] getStringResources(Context context) {
        return context.getResources().getStringArray(R.array.index_register_dialog_options);
    }

    public AlertDialog buildAlertDialogForIndexRegistration() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Choose Index to register")
                .setItems(R.array.index_register_dialog_options, (dialogInterface, i) -> {
                    IndexRegisterActivity idRegisterActivity = (IndexRegisterActivity) context;
                    selectedItem = items[i];

                    try {
                        FormUtils formUtils = new FormUtils(context);
                        JSONObject indexRegisterForm;

                        if(Constants.SCREENING.equalsIgnoreCase(selectedItem)){

                            indexRegisterForm = formUtils.getFormJson("vca_screening");

                        } else if (Constants.MOTHER.equalsIgnoreCase(selectedItem)) {

                            indexRegisterForm = formUtils.getFormJson("mother_index");

                        } else {

                            indexRegisterForm = formUtils.getFormJson("female_sex_worker");

                        }

                        idRegisterActivity.startFormActivity(indexRegisterForm);

                    } catch (Exception e) {
                        Timber.e(e);
                    }

                });
        return builder.create();
    }
}
