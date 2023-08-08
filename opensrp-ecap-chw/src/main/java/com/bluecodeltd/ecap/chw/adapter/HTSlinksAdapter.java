package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.HTSlinksModel;
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

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HTSlinksAdapter extends RecyclerView.Adapter<HTSlinksAdapter.View> {
    Context context;

    ArrayList<HTSlinksModel> links;
    ObjectMapper oMapper;

    public HTSlinksAdapter(Context context, ArrayList<HTSlinksModel> links) {
        this.context = context;
        this.links = links;
    }

    @NonNull
    @Override
    public HTSlinksAdapter.View onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View binder = LayoutInflater.from(parent.getContext()).inflate(R.layout.links_row, parent, false);

        HTSlinksAdapter.View viewHolder = new HTSlinksAdapter.View(binder);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HTSlinksAdapter.View holder, int position) {
        final  HTSlinksModel client = links.get(position);
        holder.clientNameTextView.setText(client.getFirst_name()+" "+ client.getLast_name());
        holder.clientAgeTextView.setText("Age: "+getAgeWithoutText(client.getBirthdate()));
        holder.clientDetails.setOnClickListener(view -> {
            showCustomDialog(client);
        });

        holder.editClient.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                try {

                    openFormUsingFormUtils(context, "hiv_testing_links", client);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private String getAgeWithoutText(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            return String.valueOf(periodBetweenDateOfBirthAndNow.getYears());
        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){
            return String.valueOf(periodBetweenDateOfBirthAndNow.getMonths());
        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return String.valueOf(periodBetweenDateOfBirthAndNow.getDays());
        }
        else return "Not Set";
    }
    @SuppressLint("MissingInflatedId")
    private void showCustomDialog(HTSlinksModel client) {

        android.view.View dialogView = LayoutInflater.from(context).inflate(R.layout.hts_links_details, null);
//        final TextView expandable, date_linked, sub_population, age, relationship, current_address, hiv_status, date_tested, test_results, enrolled_on_ARTLayout, date_enrolled_on_ART, initial_artLayout, initial_art_date, comment, caseworker_name, checked_by;


        LinearLayout  initial_artLayout,enrolled_on_ARTLayout;

        TextView date_linked = dialogView.findViewById(R.id.date_linked);
        TextView sub_population = dialogView.findViewById(R.id.sub_population);
        TextView age = dialogView.findViewById(R.id.age);
        TextView relationship = dialogView.findViewById(R.id.relationship);
        TextView current_address = dialogView.findViewById(R.id.current_address);
        TextView hiv_status = dialogView.findViewById(R.id.hiv_status);
        TextView date_tested = dialogView.findViewById(R.id.date_tested);
        TextView test_results = dialogView.findViewById(R.id.test_results);
        TextView date_enrolled_on_ART = dialogView.findViewById(R.id.date_enrolled_on_ART);
        TextView initial_art_date = dialogView.findViewById(R.id.initial_art_date);
        TextView comment = dialogView.findViewById(R.id.comment);
        TextView caseworker_name = dialogView.findViewById(R.id.caseworker_name);
        TextView checked_by = dialogView.findViewById(R.id.checked_by);

        enrolled_on_ARTLayout = dialogView.findViewById(R.id.enrolled_on_ARTLayout);
        initial_artLayout = dialogView.findViewById(R.id.initial_artLayout);

        if (client != null) {
            if (client.getHiv_status() != null && client.getHiv_status().equals("positive") ||
                    client.getHiv_result() != null && client.getHiv_result().equals("positive")) {
                initial_artLayout.setVisibility(android.view.View.VISIBLE);
                enrolled_on_ARTLayout.setVisibility(android.view.View.VISIBLE);
            }

            String datLinked = client.getDate_linked();
            date_linked.setText(datLinked != null ? datLinked : "Not Set");

            String subPopulation = client.getSub_population();
            String str = subPopulation.replace("\"", "").replace("[", "").replace("]", "").replace(",", ",\n");
            sub_population.setText(str  != null ? str  : "Not Set");

            String linkAge = getAgeWithoutText(client.getBirthdate());
            age.setText(linkAge != null ? linkAge : "Not Set");

            String relation = client.getRelationship();
            relationship.setText(relation != null ? relation : "Not Set");

            String address = client.getAddress();
            current_address.setText(address != null ? address : "Not Set");

            String comments = client.getComment();
            comment.setText(comments != null ? comments : "Not Set");

            String caseWorker = client.getCaseworker_name();
            caseworker_name.setText(caseWorker != null ? caseWorker : "Not Set");

            String checkedBy = client.getChecked_by();
            checked_by.setText(checkedBy != null ? checkedBy : "Not Set");

            String hivStatusValue = client.getHiv_status();
            hiv_status.setText(hivStatusValue != null ? hivStatusValue : "Not Set");

            String dateTestedValue = client.getDate_tested();
            date_tested.setText(dateTestedValue != null ? dateTestedValue : "Not Set");

            String testResultsValue = client.getHiv_result();
            test_results.setText(testResultsValue != null ? testResultsValue : "Not Set");

            String dateEnrolledOnARTValue = client.getArt_date();
            date_enrolled_on_ART.setText(dateEnrolledOnARTValue != null ? dateEnrolledOnARTValue : "Not Set");

            String initialArtDateValue = client.getArt_date_initiated();
            initial_art_date.setText(initialArtDateValue != null ? initialArtDateValue : "Not Set");


        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        TextView title = new TextView(context);
        title.setText(client.getFirst_name()+" "+client.getLast_name()+" details");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        builder.setCustomTitle(title);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public int getItemCount() {
        return links.size();
    }

    public class View extends RecyclerView.ViewHolder {
        TextView clientNameTextView, clientAgeTextView,clientDetails;
        ImageView editClient;
        public View(@NonNull android.view.View itemView) {
            super(itemView);
            clientNameTextView = itemView.findViewById(R.id.clientNameTextView);
            clientAgeTextView = itemView.findViewById(R.id.clientAgeTextView);
            clientDetails = itemView.findViewById(R.id.details);
            editClient = itemView.findViewById(R.id.edit_client);
        }
    }

    public void openFormUsingFormUtils(Context context, String formName, HTSlinksModel htSlinksModel) throws JSONException {

        oMapper = new ObjectMapper();

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.put("entity_id", htSlinksModel.getBase_entity_id());

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(htSlinksModel, Map.class));

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

                case "HIV Testing Links":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HIV_TESTING_LINKS);
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
    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode, String encounterType) {

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


}
