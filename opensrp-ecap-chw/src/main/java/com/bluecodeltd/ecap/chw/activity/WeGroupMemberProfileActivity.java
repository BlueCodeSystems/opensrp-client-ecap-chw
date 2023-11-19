package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.family.util.JsonFormUtils.STEP2;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_EXTRA.STEP3;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ViewPagerAdapterFragment;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.WeGroupDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.MyGroupMembersFragment;
import com.bluecodeltd.ecap.chw.fragment.ServicesFragment;
import com.bluecodeltd.ecap.chw.fragment.SummaryFragment;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class WeGroupMemberProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBarLayout myAppbar;
    private ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton mAddFab, addSavings, addLoan, addFine,addSocialFund, addRepayment, addIga;

    TextView addSavingsActionText, addLoanActionText,  addFineActionText,addSocialFundActionText, addRepaymentActionText, addIgaActionText;

    View dimBackground;
    Boolean isAllFabsVisible;
    TextView userName,userId;

    String name,id;
    MembersModel membersModel;
    Handler handler = new Handler();
    private final int FIVE_SECONDS = 2000;
    Runnable runnable;
    TextView groupTabCount;
    String memberRole;
    WeGroupModel weGroupModel;
    MembersModel model;
    @SuppressLint({"MissingInflatedId", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = findViewById(R.id.collapsing_toolbar_appbarlayout);
        NavigationMenu.getInstance(this, null, toolbar);

        SharedPreferences sp = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);

        memberRole = sp.getString("userRole", "").toLowerCase();

        name = getIntent().getStringExtra("userName");
        id = getIntent().getStringExtra("userId");

        membersModel =  WeGroupMembersDao.getWeGroupMemberById(id);
        model = WeGroupMembersDao.getWeGroupMemberById(id);
        weGroupModel = WeGroupDao.getWeGroupsById(model.getGroup_id());

        userName = findViewById(R.id.user_name);
        userId = findViewById(R.id.user_id);
        setUserName(userName,userId);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        View rootView = findViewById(R.id.root);



        rootView.setOnClickListener(view -> {

            if (isAllFabsVisible) {
//                mAddFab.hide();
                addSavings.hide();
                addLoan.hide();
                addFine.hide();
                addSocialFund.hide();
                addRepayment.hide();
                addIga.hide();
                addSavingsActionText.setVisibility(View.GONE);
                addFineActionText.setVisibility(View.GONE);
                addLoanActionText.setVisibility(View.GONE);
                addSocialFundActionText.setVisibility(View.GONE);
                addRepaymentActionText.setVisibility(View.GONE);
                addIgaActionText.setVisibility(View.GONE);
                dimBackground.setVisibility(View.GONE);
                isAllFabsVisible = false;
            }
        });

        mAddFab = findViewById(R.id.add_fab);

        mAddFab = findViewById(R.id.add_fab);
        if(memberRole != null && (memberRole.equals("bookwriter") || memberRole.equals("facilitator"))){
            mAddFab.setVisibility(View.VISIBLE);
        } else {
            mAddFab.setVisibility(View.GONE);
        }


        dimBackground = findViewById(R.id.dimBackground);
        returnViewPager();
        updateMemberTabTitle();

        addSavings = findViewById(R.id.add_members_fab);
        addLoan = findViewById(R.id.we_group_data_collection_fab);
        addFine = findViewById(R.id.add_fine_fab);
        addSocialFund = findViewById(R.id.add_social_fund_fab);
        addRepayment = findViewById(R.id.add_repayment_fab);
        addIga = findViewById(R.id.add_iga_fab);

        addSavingsActionText = findViewById(R.id.add_members_action_text);
        addLoanActionText = findViewById(R.id.add_loan_action_text);
        addFineActionText = findViewById(R.id.add_fine_action_text);
        addSocialFundActionText = findViewById(R.id.add_social_fund_action_text);
        addRepaymentActionText = findViewById(R.id.add_repayment_action_text);
        addIgaActionText = findViewById(R.id.add_iga_action_text);

        addSavings.setVisibility(View.GONE);
        addLoan.setVisibility(View.GONE);
        addFine.setVisibility(View.GONE);
        addSocialFund.setVisibility(View.GONE);
        addRepayment.setVisibility(View.GONE);
        addIga.setVisibility(View.GONE);
        addSavingsActionText.setVisibility(View.GONE);
        addLoanActionText.setVisibility(View.GONE);
        addFineActionText.setVisibility(View.GONE);
        addSocialFundActionText.setVisibility(View.GONE);
        addRepaymentActionText.setVisibility(View.GONE);
        addIgaActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddFab.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                dimBackground.setVisibility(View.VISIBLE);
                addSavings.show();
                addLoan.show();
                addFine.show();
                addSocialFund.show();
                addRepayment.show();
                addIga.show();
                addSavingsActionText.setVisibility(View.VISIBLE);
                addLoanActionText.setVisibility(View.VISIBLE);
                addFineActionText.setVisibility(View.VISIBLE);
                addSocialFundActionText.setVisibility(View.VISIBLE);
                addRepaymentActionText.setVisibility(View.VISIBLE);
                addIgaActionText.setVisibility(View.VISIBLE);
                isAllFabsVisible = true;
            } else {
                addSavings.hide();
                addLoan.hide();
                addFine.hide();
                addSocialFund.hide();
                addRepayment.hide();
                addIga.hide();
                addSavingsActionText.setVisibility(View.GONE);
                addFineActionText.setVisibility(View.GONE);
                addLoanActionText.setVisibility(View.GONE);
                addSocialFundActionText.setVisibility(View.GONE);
                addRepaymentActionText.setVisibility(View.GONE);
                addIgaActionText.setVisibility(View.GONE);
                dimBackground.setVisibility(View.GONE);

                isAllFabsVisible = false;
            }
        });
        addSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForm("we_group_member_savings");

            }
        });
//

        addLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openForm("we_group_member_loan");

            }
        });
        addFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForm("we_group_member_fine");

            }
        });
        addSocialFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForm("we_group_member_social_fund");

            }
        });
        addRepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               openForm("we_group_member_repayment");

            }
        });
        addIga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FormUtils formUtils = null;
//                try {
//                    formUtils = new FormUtils(getBaseContext());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                JSONObject indexRegisterForm;
//
//                indexRegisterForm = formUtils.getFormJson("we_group_member_iga");
              openForm("we_group_member_iga");
            }
        });
    }
    public void openForm(String formName){

        org.smartregister.util.FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject indexRegisterForm = formUtils.getFormJson(formName);
        JSONObject dateClientCreated = getFieldJSONObject(fields(indexRegisterForm, STEP1), "date_created");
        if (dateClientCreated  != null) {
            dateClientCreated.remove(JsonFormUtils.VALUE);
            try {
                dateClientCreated.put(JsonFormUtils.VALUE, getFormattedDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject memberID = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
        if (memberID  != null) {
            memberID.remove(JsonFormUtils.VALUE);
            try {
                memberID.put(JsonFormUtils.VALUE, id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject genderID = getFieldJSONObject(fields(indexRegisterForm, STEP1), "group_id");
        if (genderID  != null) {
            genderID.remove(JsonFormUtils.VALUE);
            try {

                genderID.put(JsonFormUtils.VALUE, model.getGroup_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject WeGroupName = getFieldJSONObject(fields(indexRegisterForm, STEP1), "group_name");
        if (WeGroupName != null) {
            WeGroupName.remove(JsonFormUtils.VALUE);
            try {
                WeGroupName.put(JsonFormUtils.VALUE, weGroupModel.getGroup_name());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject currentCycle = getFieldJSONObject(fields(indexRegisterForm, STEP1), "cycle_number");
        if (currentCycle!= null) {
            currentCycle.remove(JsonFormUtils.VALUE);
            try {
                currentCycle.put(JsonFormUtils.VALUE, weGroupModel.getCycle_number());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject groupNumber = getFieldJSONObject(fields(indexRegisterForm, STEP1), "group_number");
        if (groupNumber!= null) {
            groupNumber.remove(JsonFormUtils.VALUE);
            try {
                groupNumber.put(JsonFormUtils.VALUE, weGroupModel.getGroup_number());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject memberFirstName = getFieldJSONObject(fields(indexRegisterForm, STEP1), "first_name");
        if (memberFirstName!= null) {
            memberFirstName.remove(JsonFormUtils.VALUE);
            try {
                memberFirstName.put(JsonFormUtils.VALUE, membersModel.getFirst_name());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject memberLastName = getFieldJSONObject(fields(indexRegisterForm, STEP1), "last_name");
        if (memberLastName!= null) {
            memberLastName.remove(JsonFormUtils.VALUE);
            try {
                memberLastName.put(JsonFormUtils.VALUE, membersModel.getLast_name());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        startFormActivity(indexRegisterForm);

    }

    public  void returnViewPager(){
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SummaryFragment());

        MyGroupMembersFragment myGroupMembersFragment = new MyGroupMembersFragment();
        Bundle args = new Bundle();
        args.putString("userId", id);
        myGroupMembersFragment.setArguments(args);
        fragments.add(myGroupMembersFragment);

        fragments.add(new ServicesFragment());



        ViewPagerAdapterFragment adapter = new ViewPagerAdapterFragment(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("SUMMARY");
        tabLayout.getTabAt(1).setText("MEMBERS");
        tabLayout.getTabAt(2).setText("SERVICES");
//        tabLayout.getTabAt(3).setText("CONSTITUTION");
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    mAddFab.hide();
                    addSavings.hide();
                    addLoan.hide();
                    addFine.hide();
                    addSocialFund.hide();
                    addRepayment.hide();
                    addIga.hide();
                    addSavingsActionText.setVisibility(View.GONE);
                    addFineActionText.setVisibility(View.GONE);
                    addLoanActionText.setVisibility(View.GONE);
                    addSocialFundActionText.setVisibility(View.GONE);
                    addRepaymentActionText.setVisibility(View.GONE);
                    addIgaActionText.setVisibility(View.GONE);
                    dimBackground.setVisibility(View.GONE);
                } else {
                    mAddFab.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    private void updateMemberTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.update_tab_layout, null);
        TextView groupTabTitle = taskTabTitleLayout.findViewById(R.id.tab_title);
        groupTabCount = taskTabTitleLayout.findViewById(R.id.tab_count);
        groupTabTitle.setText("GROUP MEMBERS");
        MembersModel model = WeGroupMembersDao.getWeGroupMemberById(id);

        int count = WeGroupMembersDao.getMembersCountById(model.getGroup_id());

        groupTabCount.setText(String.valueOf(count));
        tabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }
    public void setUserName(TextView userName,TextView userId){
//        DatabaseHelper dbHelper = new DatabaseHelper(this);
//        Cursor cursor = dbHelper.select();
//        while(cursor.moveToNext()){
//            @SuppressLint("Range") String firstname = cursor.getString(cursor.getColumnIndex("firstname"));
//            @SuppressLint("Range") String lastname = cursor.getString(cursor.getColumnIndex("lastname"));
//            @SuppressLint("Range") String userId = cursor.getString(cursor.getColumnIndex("user_id"));
//
//            name.setText(firstname+" "+lastname);
//            id.setText("ID: "+userId);
//        }
//        cursor.close();

            userName.setText(name);
            userId.setText("ID: "+id);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(getApplicationContext(),WeGroupDashBoardActivity.class);
        startActivity(intent);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {

            boolean is_edit_mode = false;

            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);

            JSONObject jsonFormObject = null;
            try {
                jsonFormObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!jsonFormObject.optString("entity_id").isEmpty()) {
                is_edit_mode = true;
            }
            String EncounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, is_edit_mode, EncounterType);

                switch (EncounterType) {

                    case "Group":
                    case "We Group Member Saving":
                    case "We Group Member loan":
                    case "We Group Member Fine":
                    case "We Group Member Social Fund":
                    case "We Group Member IGA":
                        Toasty.success(getApplicationContext(), "Form Saved", Toast.LENGTH_LONG, true).show();
                        finish();
                        startActivity(getIntent());
                        break;


                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
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
                case "Group":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "We Group Member Saving":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group_member_saving");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "We Group Member loan":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group_member_loan");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "We Group Member Fine":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group_member_fine");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "We Group Member Social Fund":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group_member_social_fund");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Member Repayment Form":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group_member_iga");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "We Group Member IGA":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group_member_iga");
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
    public HashMap<String, MembersModel> getData() {


        HashMap<String, MembersModel> map = new HashMap<>();

        map.put("uniqueID",membersModel);

        return map;

    }
    private String getFormattedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return today.format(formatter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.we_group_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                finish();
                startActivity(getIntent());
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    public void refreshActivity() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, FIVE_SECONDS);
              recreate();
            }
        }, FIVE_SECONDS);
        super.onResume();
    }

}
