package com.bluecodeltd.ecap.chw.view_holder;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_EXTRA.STEP1;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexRegisterActivity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.dao.VcaAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.VCAServiceModel;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.JsonFormUtils;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.sync.helper.ECSyncHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class IndexRegisterViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private TextView familyNameTextView;

    private TextView villageTextView, gender_age;

    //public Button caseplan_layout;

    private View myStatus;

    private final ImageView  visitLayout, caseplan_layout, warningIcon;
    private final TextView index_icon_layout;
    private final Button dueButton;
    private final ImageView notification;
    private final LinearLayout notification_wrapper;
    JSONObject indexRegisterForm;

    VcaScreeningModel indexVCA;
    VCAServiceModel serviceModel;

    public IndexRegisterViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        caseplan_layout = itemView.findViewById(R.id.index_case_plan);
        index_icon_layout = itemView.findViewById(R.id.index_icon);
        myStatus = itemView.findViewById(R.id.mystatusx);
        visitLayout = itemView.findViewById(R.id.index_visit);
        gender_age = itemView.findViewById(R.id.gender_age);
        warningIcon = itemView.findViewById(R.id.index_warning);
        dueButton = itemView.findViewById(R.id.due_button);
        notification = itemView.findViewById(R.id.notifications);
        notification_wrapper = itemView.findViewById(R.id.notification_wrapper);


    }


    public void setupViews(String family, String village, int plans, int visits, String is_index, String status, String gender, String age, String is_screened,String vcaAge){

        familyNameTextView.setText(family);
        villageTextView.setText("ID : "+village);
        gender_age.setText(gender + " : " + age+" ");



        if(status != null && status.equals("1")){
            myStatus.setBackgroundColor(0xff05b714);
        } else if (status != null && status.equals("0")) {
            myStatus.setBackgroundColor(0xffff0000);
        } else if(status != null && status.equals("2")){
            myStatus.setBackgroundColor(0xffffa500);
        }

        if (is_index != null && is_index.equals("1")){

            index_icon_layout.setVisibility(View.VISIBLE);
        } else {

            index_icon_layout.setVisibility(View.GONE);
        }


        if(plans > 0){
            caseplan_layout.setVisibility(View.VISIBLE);
        } else {
            caseplan_layout.setVisibility(View.GONE);
        }

        if(visits > 0){
            visitLayout.setVisibility(View.VISIBLE);
        } else {
            visitLayout.setVisibility(View.GONE);
        }


        if(is_screened != null && is_screened.equals("true")){
            warningIcon.setVisibility(View.GONE);
        } else {
            warningIcon.setVisibility(View.VISIBLE);
        }
        VcaVisitationModel visitStatus = VcaVisitationDao.getVcaVisitationNotification(village);
        VcaAssessmentModel assessmentModel = VcaAssessmentDao.getVcaVisitationNotificationFromAssessment(village);
        String statusColor = null;
        String visitDate = null;

        if (visitStatus != null) {
            statusColor = visitStatus.getStatus_color();
            visitDate = visitStatus.getVisit_date();
        } else {
            if (assessmentModel != null) {
                statusColor = assessmentModel.getStatus_color();
                visitDate = assessmentModel.getDate_edited();

            }
        }

// Check if both the status color and the visit date are not null and trim the status color string

        indexVCA = VCAScreeningDao.getVcaScreening(village);

        if (statusColor != null && (indexVCA.getCase_status() == null || indexVCA.getCase_status().equals("1") )) {
            statusColor = statusColor.trim();

            // Assign resource ids based on status color
            int backgroundResource;
            int textColorResource;
            String buttonText;

            if ("green".equalsIgnoreCase(statusColor) && visitDate != null) {
                backgroundResource = R.drawable.home_visit_due;
                textColorResource = org.smartregister.chw.core.R.color.colorGreen;
                buttonText = "Visit Due: " + visitDate;
            } else if ("yellow".equalsIgnoreCase(statusColor) && visitDate != null) {
                backgroundResource = R.drawable.home_visit_10days_less;
                textColorResource = R.color.pie_chart_yellow;
                buttonText = "Visit Due: " + visitDate;
            } else if ("red".equalsIgnoreCase(statusColor) && visitDate != null) {
                backgroundResource = R.drawable.home_visit_overdue;
                textColorResource = com.nerdstone.neatformcore.R.color.colorRed;
                buttonText = "Visit Overdue: " + visitDate;
            } else {
                backgroundResource = org.smartregister.family.R.drawable.due_contact;
                textColorResource = org.smartregister.R.color.btn_blue;
                buttonText = "Conduct Visit";
            }

            // Apply the resources to the button
            dueButton.setBackgroundResource(backgroundResource);
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), textColorResource));
            dueButton.setText(buttonText);
        }
        else {
            if(indexVCA != null && (indexVCA.getCase_status().equals("0") || indexVCA.getCase_status().equals("2")) ){
                dueButton.setBackgroundResource(R.drawable.inactive_button);
                dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), org.smartregister.R.color.btn_blue));
                dueButton.setText("Case Closed");
            } else {
                dueButton.setBackgroundResource(org.smartregister.family.R.drawable.due_contact);
                dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), org.smartregister.R.color.btn_blue));
                dueButton.setText("Conduct Visit");
            }
        }

       //Setting up the notification bell
//        serviceModel = VCAServiceReportDao.getVcaService(indexVCA.getUnique_id());
//        if (serviceModel != null && indexVCA != null && indexVCA.getAdolescent_birthdate() != null) {
//            String schooled = removeBrackets(serviceModel.getSchooled_services());
//            String stable = removeBrackets(serviceModel.getStable_services());
//
//            int compareAge = calculateAge(indexVCA.getAdolescent_birthdate());
//
//            if (compareAge >= 18 && compareAge <= 20
//                    && (indexVCA.getCase_status() == null || "1".equals(indexVCA.getCase_status()))
//                    && ("not applicable".equals(schooled) || "not applicable".equals(stable))) {
//                notification_wrapper.setVisibility(View.VISIBLE);
//            } else {
//                notification_wrapper.setVisibility(View.GONE);
//            }
//        } else {
//
//        }


        //Onclick of the notification bell
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, dueButton);

                String defaultTitle = "This beneficiary has not received a service associated with:";
                SpannableString spannableString = new SpannableString(defaultTitle);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, defaultTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                MenuItem defaultItem = popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 0, spannableString);

                popupMenu.getMenuInflater().inflate(R.menu.ecap_schedules, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    String title = String.valueOf(menuItem.getTitle());
                    if (title.equals("Quarterly Re-assessment")) {
                        openVisitationForm(village, vcaAge);
                    }
                    return true;
                }
                );

// Show the popup menu
                popupMenu.show();

            }
        });
        dueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(indexVCA.getCase_status() != null && (indexVCA.getCase_status().equals("0") || indexVCA.getCase_status().equals("2")) ){
                    Toasty.warning(context, "Unable to conduct a visitation for "+indexVCA.getFirst_name()+" "+indexVCA.getLast_name()+ " because the record is closed", Toast.LENGTH_LONG, true).show();
                } else {
                    if (indexVCA.getDate_screened() != null) {

                        openVisitationForm(village, vcaAge);
                    } else {
                        Toasty.warning(context, "Unable to conduct a visitation for " + indexVCA.getFirst_name() + " " + indexVCA.getLast_name() + " VCA Screening has not been done", Toast.LENGTH_LONG, true).show();
                    }
                }
            }
        });


    }
    public void openVisitationForm(String village,String vcaAge){

                try {

                    FormUtils formUtils = new FormUtils();

                    indexRegisterForm = formUtils.getFormJson(this.context,"household_visitation_for_vca_0_20_years");


                    JSONObject cId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
//                    cId.remove(JsonFormUtils.VALUE);
//                    cId.put(JsonFormUtils.VALUE, village);


                    if (cId  != null) {
                        cId.remove(JsonFormUtils.VALUE);
                        try {
                            cId.put(JsonFormUtils.VALUE, village);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    JSONObject cDate = getFieldJSONObject(fields(indexRegisterForm, STEP1), "age");
                    int passAge = extractAgeFromString(vcaAge);
                    cDate.put("value",passAge);
//
                    SharedPreferences cp = PreferenceManager.getDefaultSharedPreferences(context);
                    String  caseworkerphone = cp.getString("phone", "Anonymous");
                    String caseworkername = cp.getString("caseworker_name", "Anonymous");

                    JSONObject cphone = getFieldJSONObject(fields(indexRegisterForm, "step1"), "phone");

                    if (cphone  != null) {
                        cphone .remove(JsonFormUtils.VALUE);
                        try {
                            cphone .put(JsonFormUtils.VALUE, caseworkerphone);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONObject caseworker_name_object = getFieldJSONObject(fields(indexRegisterForm, "step1"), "caseworker_name");
                    if (caseworker_name_object != null) {
                        caseworker_name_object.remove(JsonFormUtils.VALUE);
                        try {
                            caseworker_name_object.put(JsonFormUtils.VALUE, caseworkername);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    IndexRegisterActivity idRegisterActivity = (IndexRegisterActivity) context;
                    idRegisterActivity.startFormActivityFromTheVcaProfile(indexRegisterForm);

                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {

                    ChildIndexEventClient childIndexEventClient = processRegistration(indexRegisterForm.toString());
                    if (childIndexEventClient == null) {
                        return;
                    }
                    saveRegistration(childIndexEventClient,true);


                } catch (Exception e) {
                    Timber.e(e);
                }
    }
    public void startFormActivity(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setName("Visitation");
        form.setHideSaveLabel(true);
        form.setNextLabel(context.getString(R.string.next));
        form.setPreviousLabel(context.getString(R.string.previous));
        form.setSaveLabel(context.getString(R.string.submit));
        form.setNavigationBackground(R.color.primary);
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        context.startActivity(intent);
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
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD_VCA);
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

    public static int extractAgeFromString(String ageString) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(ageString);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(0));
        }

        throw new IllegalArgumentException("Invalid age string format");
    }

    private int calculateAge(String inputDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date dateOfBirth = sdf.parse(inputDate);

            Calendar dob = Calendar.getInstance();
            dob.setTime(dateOfBirth);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String removeBrackets(String input) {
        if (input != null) {
            return input.replaceAll("^\\[\"|\"\\]$", "");
        }
        return null;
    }
}
