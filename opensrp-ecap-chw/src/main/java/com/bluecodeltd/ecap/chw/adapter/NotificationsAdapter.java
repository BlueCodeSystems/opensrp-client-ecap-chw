package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.DueVisitsHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {


    Context context;
    List<DueVisitsHelper.DueVisit> visits;
    ObjectMapper oMapper;


    public NotificationsAdapter(List<DueVisitsHelper.DueVisit> visits, Context context){

        super();

        this.visits = visits;
        this.context = context;
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification, parent, false);

        NotificationsAdapter.ViewHolder viewHolder = new NotificationsAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.ViewHolder holder, final int position) {

        final DueVisitsHelper.DueVisit visit = visits.get(position);

//        holder.setIsRecyclable(false);

       /* holder.linearLayout.setOnClickListener(v -> {

            Intent i = new Intent(context, IndexDetailsActivity.class);
            i.putExtra("childId",  visit.getUnique_id());
            context.startActivity(i);
        });*/
        holder.register_columns.setOnClickListener(v -> {


                try {

                    openFormUsingFormUtils(context, "household_visitation_for_vca_0_20_years",visit);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


//            FormUtils formUtils = null;
//            try {
//                formUtils = new FormUtils(context);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            JSONObject formToBeOpened;
//
//            formToBeOpened = formUtils.getFormJson("household_visitation_for_vca_0_20_years");
//            try {
//
//                ChildIndexEventClient childIndexEventClient = processRegistration(formToBeOpened.toString());
//                if (childIndexEventClient == null) {
//                    return;
//                }
//                saveRegistration(childIndexEventClient,false);
//
//
//            } catch (Exception e) {
//                Timber.e(e);
//            }

        });

        holder.txtName.setText(visit.name);
        holder.txtVisitDate.setText(visit.visitDate);

        boolean overdue = "red".equalsIgnoreCase(visit.statusColor);
        holder.statusPill.setBackgroundResource(overdue ? R.drawable.bg_chip_danger : R.drawable.bg_chip_warning);
        holder.statusPill.setText(overdue ? "Overdue" : "Due Soon");
        holder.statusStripe.setBackgroundColor(ContextCompat.getColor(context, overdue ? R.color.pie_chart_red : R.color.pie_chart_orange));
    }

    @Override
    public int getItemCount() {

        return visits.size();
    }

    public void openFormUsingFormUtils(Context context, String formName, DueVisitsHelper.DueVisit visit) throws JSONException {

        oMapper = new ObjectMapper();

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

//        formToBeOpened.put("entity_id", referral.getBase_entity_id());
//
//        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(referral, Map.class));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String caseworkerphone = prefs.getString("phone", "Anonymous");
        String caseworkername = prefs.getString("caseworker_name", "Anonymous");



        JSONObject  vcaId = getFieldJSONObject(fields(formToBeOpened, "step1"), "unique_id");
        if ( vcaId != null) {
            vcaId.remove(JsonFormUtils.VALUE);
            try {
                vcaId.put(JsonFormUtils.VALUE, visit.uniqueId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject  vcaAge = getFieldJSONObject(fields(formToBeOpened, "step1"), "age");
        if ( vcaAge != null) {
            vcaAge.remove(JsonFormUtils.VALUE);
            try {
                vcaAge.put(JsonFormUtils.VALUE, calculateAge(visit.birthdate));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject cphone = getFieldJSONObject(fields(formToBeOpened, "step1"), "phone");
        if (cphone != null) {
            cphone.remove(JsonFormUtils.VALUE);
            try {
                cphone.put(JsonFormUtils.VALUE, caseworkerphone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject cname = getFieldJSONObject(fields(formToBeOpened, "step1"), "caseworker_name");
        if (cname  != null) {
            cname.remove(JsonFormUtils.VALUE);
            try {
                cname.put(JsonFormUtils.VALUE, caseworkername);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



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

                case "Household Visitation Form 0-20 years":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_household_visitation_for_vca_0_20_years");
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

    public static int calculateAge(String dob) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dateOfBirth = sdf.parse(dob);
            Date currentDate = new Date();

            long diffInMillis = currentDate.getTime() - dateOfBirth.getTime();
            int age = (int) (diffInMillis / (1000L * 60 * 60 * 24 * 365));

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtName, txtVisitDate, statusPill;

        LinearLayout linearLayout;
        View register_columns;
        View statusStripe;

        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtName  = itemView.findViewById(R.id.name);
            txtVisitDate = itemView.findViewById(R.id.visit_date);
            statusPill = itemView.findViewById(R.id.index_icon);
            statusStripe = itemView.findViewById(R.id.status_stripe);
            register_columns = itemView.findViewById(R.id.register_columns);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
