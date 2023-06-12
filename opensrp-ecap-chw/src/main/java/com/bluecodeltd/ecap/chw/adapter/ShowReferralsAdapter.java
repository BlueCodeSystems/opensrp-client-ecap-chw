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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.ReferralModel;
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

public class ShowReferralsAdapter extends RecyclerView.Adapter<ShowReferralsAdapter.ViewHolder> {

    Context context;

    List<ReferralModel> referrals;
    ObjectMapper oMapper;

    public ShowReferralsAdapter(List<ReferralModel> referrals, Context context){

        super();

        this.referrals = referrals;
        this.context = context;
    }

    @Override
    public ShowReferralsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_referral, parent, false);

        ShowReferralsAdapter.ViewHolder viewHolder = new ShowReferralsAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShowReferralsAdapter.ViewHolder holder, final int position) {

        final ReferralModel showReferrals = referrals.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(showReferrals.getReferred_date());

        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                try {

                    openFormUsingFormUtils(context, "referral", showReferrals);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        CaseStatusModel caseStatusModel = IndexPersonDao.getCaseStatus(showReferrals.getUnique_id());

        holder.editme.setOnClickListener(v -> {
            if( caseStatusModel.getCase_status().equals("0") ||  caseStatusModel.getCase_status().equals("2")) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_layout);
                dialog.show();

                TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
                dialogMessage.setText(caseStatusModel.getFirst_name() + " " + caseStatusModel.getLast_name() + " was either de-registered or inactive in the program");

                Button dialogButton = dialog.findViewById(R.id.dialog_button);
                dialogButton.setOnClickListener(va -> dialog.dismiss());

            } else {
                if (v.getId() == R.id.edit_me) {

                    try {

                        openFormUsingFormUtils(context, "referral", showReferrals);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        holder.delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("You are about to delete this VCA referral");
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
                showReferrals.setDelete_status("1");
                JSONObject vcaScreeningForm = formUtils.getFormJson("referral");
                try {
                    CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue(showReferrals, Map.class));
                    vcaScreeningForm.put("entity_id", showReferrals.getBase_entity_id());
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
        });

    }

    public void openFormUsingFormUtils(Context context, String formName, ReferralModel referral) throws JSONException {

        oMapper = new ObjectMapper();

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.put("entity_id", referral.getBase_entity_id());

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(referral, Map.class));

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
        form.setActionBarBackground(R.color.dark_grey);
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

                case "Referral":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_REFERRAL);
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

        return referrals.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate;

        LinearLayout linearLayout;
        ImageView editme, delete;

        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtDate  = itemView.findViewById(R.id.date);
            editme = itemView.findViewById(R.id.edit_me);
            delete = itemView.findViewById(R.id.delete_record);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
