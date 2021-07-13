package com.bluecodeltd.ecap.chw.custom_view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexRegisterActivity;

import org.json.JSONObject;
import org.smartregister.util.FormUtils;

import timber.log.Timber;

public class AlertDialogForIndexRegistrationRegister {
    Context context;
    String[] items = {"Child", "Mother"};
    String selectedItem;

    // IndexRegisterActivity indexRegisterActivity = new IndexRegisterActivity();
    public AlertDialogForIndexRegistrationRegister(Context context) {
        this.context = context;
    }

    public String[] getStringResources(Context context) {
        return context.getResources().getStringArray(R.array.index_register_dialog_options);
    }

    public AlertDialog buildAlertDialogForIndexRegistration() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Choose Index to register")
                .setItems(R.array.index_register_dialog_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //items= context.getResources().getStringArray(R.array.index_register_dialog_options);
                        //items=getStringResources(context);
                        IndexRegisterActivity indexRegisterActivity = (IndexRegisterActivity) context;
                        selectedItem = items[i];

                        try {
                            FormUtils formUtils = new FormUtils(context);
                            if (selectedItem.equals("Mother")) {
                                JSONObject indexRegisterForm = formUtils.getFormJson("mother_index");
                                indexRegisterActivity.startFormActivity(indexRegisterForm);
                            } else {
                                JSONObject indexRegisterForm = formUtils.getFormJson("child_index");
                                indexRegisterActivity.startFormActivity(indexRegisterForm);
                            }

                        } catch (Exception e) {
                            Timber.e(e);
                        }

                    } /*else
                         {
                             indexRegisterActivity.startFormActivity("mother_index","","");
                         }
                         */
                });
        return builder.create();
    }
}
