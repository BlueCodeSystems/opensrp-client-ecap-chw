package com.bluecodeltd.ecap.chw.activity;

import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.WeGroupDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberRegisterModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;
import com.bluecodeltd.ecap.chw.model.WeGroupRegisterModel;
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
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class UserRegisterActivity extends AppCompatActivity {
    public static final String TAG = "ListViewExample";

    Context context;
    private ListView listView;
    private Button conduct_register;
    String groupName, groupId;
    WeGroupModel weGroupModel;
    TextView txtGroupName, txtGroupId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        this.listView = findViewById(R.id.listView);
        this.conduct_register = findViewById(R.id.conduct_register);

        this.listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getStringExtra("groupId");

        txtGroupName = findViewById(R.id.user_name);
        txtGroupId = findViewById(R.id.user_id);
        txtGroupName.setText(groupName);
        txtGroupId.setText(groupId);

        weGroupModel = WeGroupDao.getWeGroupsById(groupId);

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "onItemClick: " + position);
            CheckedTextView v = (CheckedTextView) view;
            boolean currentCheck = v.isChecked();
            WeGroupMemberRegisterModel user = (WeGroupMemberRegisterModel) listView.getItemAtPosition(position);
            user.setActive(!currentCheck);
        });

        // Initialize and set the adapter
        initListViewData();

        this.conduct_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WeGroupRegisterModel register = new WeGroupRegisterModel();
                register.setGroup_id(groupId);
                register.setAttendance(getSelectedItemsString());
                register.setDate_register_conducted(getFormattedDate());



                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(UserRegisterActivity.this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                JSONObject weGroupMemberForm = formUtils.getFormJson("we_group_register_form");
                try {
                    CoreJsonFormUtils.populateJsonForm(weGroupMemberForm, new ObjectMapper().convertValue(register, Map.class));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                ChildIndexEventClient childIndexEventClient = processRegistration(weGroupMemberForm.toString());

                if (childIndexEventClient == null) {
                    return;
                }
                saveRegistration(childIndexEventClient,false,"We Group Member Register");
                Toasty.success(getApplicationContext(), "Register Conducted", Toast.LENGTH_LONG, true).show();
                onBackPressed();
            }
        });

    }

    private String getFormattedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return today.format(formatter);
    }
    private void initListViewData() {
        List<WeGroupMemberRegisterModel> userList = new ArrayList<>();
        List<MembersModel> membersList = WeGroupMembersDao.getGroupMembersByGroupId(groupId);

        if (membersList != null) {
            for (MembersModel member : membersList) {
                WeGroupMemberRegisterModel user = new WeGroupMemberRegisterModel(member.getFirst_name(), member.getLast_name(), member.getGender(), member.getUnique_id(), false);
                userList.add(user);
            }
        }

        WeGroupMemberRegisterModel[] users = userList.toArray(new WeGroupMemberRegisterModel[userList.size()]);
        ArrayAdapter<WeGroupMemberRegisterModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, users);
        listView.setAdapter(adapter);
    }


//private void initListViewData() {
//    // Retrieve the members for the group
//    List<MembersModel> listMembers = new ArrayList<>();
//    listMembers.addAll(WeGroupMembersDao.getWeGroupMembersByGroupId(groupId));
//
//    // Create and set the adapter
//    ArrayAdapter<MembersModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, listMembers);
//    listView.setAdapter(adapter);
//}

    public String getSelectedItemsString() {
        SparseBooleanArray sp = listView.getCheckedItemPositions();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sp.size(); i++) {
            if (sp.valueAt(i)) {
                WeGroupMemberRegisterModel user = (WeGroupMemberRegisterModel) listView.getItemAtPosition(i);
                String s = user.getFirst_name() + " " + user.getLast_name() + " " + user.getUnique_id();
                sb = sb.append(s).append(", ");
            }
        }

        return sb.toString();
    }


    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("");
        form.setHideSaveLabel(true);
        form.setNextLabel(getString(R.string.next));
        form.setPreviousLabel(getString(R.string.previous));
        form.setSaveLabel(getString(R.string.submit));
        form.setNavigationBackground(R.color.primary);
        Intent intent = new Intent(getApplicationContext(), org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);



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
                case "We Group Member Register":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group_member_register");
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

    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode,String encounterType) {

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

    public FormTag getFormTag() {
        FormTag formTag = new FormTag();
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
        return formTag;
    }

    public AllSharedPreferences getAllSharedPreferences () {
        return ChwApplication.getInstance().getContext().allSharedPreferences();
    }

    private ClientProcessorForJava getClientProcessorForJava() {
        return ChwApplication.getInstance().getClientProcessorForJava();
    }

    public void returnToGroupProfile(){
        finish();
        Intent register = new Intent(UserRegisterActivity.this,WeGroupProfileActivity.class);
        register.putExtra("groupName", weGroupModel.getGroup_name());
        register.putExtra("groupId", weGroupModel.getGroup_id());
        startActivity(register);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent register = new Intent(UserRegisterActivity.this,WeGroupProfileActivity.class);
        register.putExtra("groupName", weGroupModel.getGroup_name());
        register.putExtra("groupId", weGroupModel.getGroup_id());
        startActivity(register);
    }
}
