package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ViewPagerAdapterFragment;
import com.bluecodeltd.ecap.chw.api.MemberApi;
import com.bluecodeltd.ecap.chw.api.VolleyCallback;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.MembersFragment;
import com.bluecodeltd.ecap.chw.fragment.ServicesFragment;
import com.bluecodeltd.ecap.chw.fragment.WeGroupFragmentMembers;
import com.bluecodeltd.ecap.chw.fragment.WeGroupProfileSummary;
import com.bluecodeltd.ecap.chw.interceptor.AuthInterceptor;
import com.bluecodeltd.ecap.chw.model.Credentials;
import com.bluecodeltd.ecap.chw.model.MemberListModel;
import com.bluecodeltd.ecap.chw.model.MembersModel;
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
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

public class WeGroupProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout myAppbar;
    private ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton mAddFab, addSavings, addLoan, addFine,addSocialFund, addRepayment, addIga;

    TextView addSavingsActionText, addLoanActionText,  addFineActionText,addSocialFundActionText, addRepaymentActionText, addIgaActionText;

    View dimBackground;
    Boolean isAllFabsVisible;
    TextView userName,userId;

    String groupName, groupId,username,password;
    MembersModel membersModel;
    Handler handler = new Handler();
    private final int FIVE_SECONDS = 2000;
    Runnable runnable;


    TextView groupTabCount;
    ObjectMapper oMapper;

    @SuppressLint({"RestrictedApi", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_group_profile);

        oMapper = new ObjectMapper();

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = findViewById(R.id.collapsing_toolbar_appbarlayout);
        NavigationMenu.getInstance(this, null, toolbar);

        groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getStringExtra("groupId");

        SharedPreferences sharedPreferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        password = sharedPreferences.getString("password", "");

        membersModel =  WeGroupMembersDao.getWeGroupMemberById(groupId);

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
//                addLoan.hide();
//                addFine.hide();
//                addSocialFund.hide();
//                addRepayment.hide();
//                addIga.hide();
                addSavingsActionText.setVisibility(View.GONE);
//                addFineActionText.setVisibility(View.GONE);
//                addLoanActionText.setVisibility(View.GONE);
//                addSocialFundActionText.setVisibility(View.GONE);
//                addRepaymentActionText.setVisibility(View.GONE);
//                addIgaActionText.setVisibility(View.GONE);
//                dimBackground.setVisibility(View.GONE);
                isAllFabsVisible = false;
            }
        });

        mAddFab = findViewById(R.id.add_fab);
        dimBackground = findViewById(R.id.dimBackground);

        returnViewPager();
        updateMemberTabTitle();

        addSavings = findViewById(R.id.add_saving_fab);
//        addLoan = findViewById(R.id.add_loan_fab);
//        addFine = findViewById(R.id.add_fine_fab);
//        addSocialFund = findViewById(R.id.add_social_fund_fab);
//        addRepayment = findViewById(R.id.add_repayment_fab);
//        addIga = findViewById(R.id.add_iga_fab);

        addSavingsActionText = findViewById(R.id.add_saving_action_text);
//        addLoanActionText = findViewById(R.id.add_loan_action_text);
//        addFineActionText = findViewById(R.id.add_fine_action_text);
//        addSocialFundActionText = findViewById(R.id.add_social_fund_action_text);
//        addRepaymentActionText = findViewById(R.id.add_repayment_action_text);
//        addIgaActionText = findViewById(R.id.add_iga_action_text);

        addSavings.setVisibility(View.GONE);
//        addLoan.setVisibility(View.GONE);
//        addFine.setVisibility(View.GONE);
//        addSocialFund.setVisibility(View.GONE);
//        addRepayment.setVisibility(View.GONE);
//        addIga.setVisibility(View.GONE);
        addSavingsActionText.setVisibility(View.GONE);
//        addLoanActionText.setVisibility(View.GONE);
//        addFineActionText.setVisibility(View.GONE);
//        addSocialFundActionText.setVisibility(View.GONE);
//        addRepaymentActionText.setVisibility(View.GONE);
//        addIgaActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddFab.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                dimBackground.setVisibility(View.VISIBLE);
                addSavings.show();
//                addLoan.show();
//                addFine.show();
//                addSocialFund.show();
//                addRepayment.show();
//                addIga.show();
                addSavingsActionText.setVisibility(View.VISIBLE);
//                addLoanActionText.setVisibility(View.VISIBLE);
//                addFineActionText.setVisibility(View.VISIBLE);
//                addSocialFundActionText.setVisibility(View.VISIBLE);
//                addRepaymentActionText.setVisibility(View.VISIBLE);
//                addIgaActionText.setVisibility(View.VISIBLE);
                isAllFabsVisible = true;
            } else {
                addSavings.hide();
//                addLoan.hide();
//                addFine.hide();
//                addSocialFund.hide();
//                addRepayment.hide();
//                addIga.hide();
                addSavingsActionText.setVisibility(View.GONE);
//                addFineActionText.setVisibility(View.GONE);
//                addLoanActionText.setVisibility(View.GONE);
//                addSocialFundActionText.setVisibility(View.GONE);
//                addRepaymentActionText.setVisibility(View.GONE);
//                addIgaActionText.setVisibility(View.GONE);
                dimBackground.setVisibility(View.GONE);

                isAllFabsVisible = false;
            }
        });
        addSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(getBaseContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject indexRegisterForm;

                indexRegisterForm = formUtils.getFormJson("we_group_member");
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
                        memberID.put(JsonFormUtils.VALUE, generateGroupId(9));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                JSONObject WeGroupID = getFieldJSONObject(fields(indexRegisterForm, STEP1), "group_id");
                if (WeGroupID != null) {
                    WeGroupID.remove(JsonFormUtils.VALUE);
                    try {
                        WeGroupID.put(JsonFormUtils.VALUE, groupId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                startFormActivity(indexRegisterForm);
            }
        });
//

//        addLoan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FormUtils formUtils = null;
//                try {
//                    formUtils = new FormUtils(getBaseContext());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                JSONObject indexRegisterForm;
//
//                indexRegisterForm = formUtils.getFormJson("we_group_member_loan");
//
//                JSONObject dateClientCreated = getFieldJSONObject(fields(indexRegisterForm, STEP1), "date_created");
//                if (dateClientCreated  != null) {
//                    dateClientCreated.remove(JsonFormUtils.VALUE);
//                    try {
//                        dateClientCreated.put(JsonFormUtils.VALUE, getFormattedDate());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                JSONObject memberID = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
//                if (memberID  != null) {
//                    memberID.remove(JsonFormUtils.VALUE);
//                    try {
//                        memberID.put(JsonFormUtils.VALUE, id);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                startFormActivity(indexRegisterForm);
//            }
//        });
//        addFine.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FormUtils formUtils = null;
//                try {
//                    formUtils = new FormUtils(getBaseContext());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                JSONObject indexRegisterForm;
//
//                indexRegisterForm = formUtils.getFormJson("we_group_member_fine");
//
//                JSONObject dateClientCreated = getFieldJSONObject(fields(indexRegisterForm, STEP1), "date_created");
//                if (dateClientCreated  != null) {
//                    dateClientCreated.remove(JsonFormUtils.VALUE);
//                    try {
//                        dateClientCreated.put(JsonFormUtils.VALUE, getFormattedDate());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                JSONObject memberID = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
//                if (memberID  != null) {
//                    memberID.remove(JsonFormUtils.VALUE);
//                    try {
//                        memberID.put(JsonFormUtils.VALUE, id);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                startFormActivity(indexRegisterForm);
//            }
//        });
//        addSocialFund.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FormUtils formUtils = null;
//                try {
//                    formUtils = new FormUtils(getBaseContext());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                JSONObject indexRegisterForm;
//
//                indexRegisterForm = formUtils.getFormJson("we_group_member_social_fund");
//                JSONObject dateClientCreated = getFieldJSONObject(fields(indexRegisterForm, STEP1), "date_created");
//                if (dateClientCreated  != null) {
//                    dateClientCreated.remove(JsonFormUtils.VALUE);
//                    try {
//                        dateClientCreated.put(JsonFormUtils.VALUE, getFormattedDate());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                JSONObject memberID = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
//                if (memberID  != null) {
//                    memberID.remove(JsonFormUtils.VALUE);
//                    try {
//                        memberID.put(JsonFormUtils.VALUE, id);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//                startFormActivity(indexRegisterForm);
//            }
//        });
//        addRepayment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FormUtils formUtils = null;
//                try {
//                    formUtils = new FormUtils(getBaseContext());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                JSONObject indexRegisterForm;
//
//                indexRegisterForm = formUtils.getFormJson("we_group_member_repayment");
//
//                JSONObject dateClientCreated = getFieldJSONObject(fields(indexRegisterForm, STEP1), "date_created");
//                if (dateClientCreated  != null) {
//                    dateClientCreated.remove(JsonFormUtils.VALUE);
//                    try {
//                        dateClientCreated.put(JsonFormUtils.VALUE, getFormattedDate());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                JSONObject memberID = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
//                if (memberID  != null) {
//                    memberID.remove(JsonFormUtils.VALUE);
//                    try {
//                        memberID.put(JsonFormUtils.VALUE, id);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                startFormActivity(indexRegisterForm);
//            }
//        });
//        addIga.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FormUtils formUtils = null;
//                try {
//                    formUtils = new FormUtils(getBaseContext());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                JSONObject indexRegisterForm;
//
//                indexRegisterForm = formUtils.getFormJson("we_group_member_iga");
//                JSONObject dateClientCreated = getFieldJSONObject(fields(indexRegisterForm, STEP1), "date_created");
//                if (dateClientCreated  != null) {
//                    dateClientCreated.remove(JsonFormUtils.VALUE);
//                    try {
//                        dateClientCreated.put(JsonFormUtils.VALUE, getFormattedDate());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                JSONObject memberID = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
//                if (memberID  != null) {
//                    memberID.remove(JsonFormUtils.VALUE);
//                    try {
//                        memberID.put(JsonFormUtils.VALUE, id);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                startFormActivity(indexRegisterForm);
//            }
//        });
    }

    public  void returnViewPager(){
        List<Fragment> fragments = new ArrayList<>();
//        fragments.add(new WeGroupProfileSummary());

        WeGroupProfileSummary weGroupProfileSummary = new WeGroupProfileSummary();
        Bundle argsSummary = new Bundle();
        argsSummary.putString("groupId", groupId);
        weGroupProfileSummary.setArguments(argsSummary);
        fragments.add(weGroupProfileSummary);


        WeGroupFragmentMembers weGroupFragmentMembers = new WeGroupFragmentMembers();
        Bundle args = new Bundle();
        args.putString("groupId", groupId);
        weGroupFragmentMembers.setArguments(args);
        fragments.add(weGroupFragmentMembers);

//        fragments.add(new ServicesFragment());
//        fragments.add(new ConstituitionFragment());


        ViewPagerAdapterFragment adapter = new ViewPagerAdapterFragment(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("SUMMARY");
        tabLayout.getTabAt(1).setText("MEMBERS");

//        tabLayout.getTabAt(2).setText("SERVICES");
//        tabLayout.getTabAt(3).setText("CONSTITUTION");
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (position == 1) {
//                    mAddFab.hide();
////                    addSavings.hide();
////                    addLoan.hide();
////                    addFine.hide();
////                    addSocialFund.hide();
////                    addRepayment.hide();
////                    addIga.hide();
//                    addSavingsActionText.setVisibility(View.GONE);
////                    addFineActionText.setVisibility(View.GONE);
////                    addLoanActionText.setVisibility(View.GONE);
////                    addSocialFundActionText.setVisibility(View.GONE);
////                    addRepaymentActionText.setVisibility(View.GONE);
////                    addIgaActionText.setVisibility(View.GONE);
//                    dimBackground.setVisibility(View.GONE);
//                } else {
//                    mAddFab.show();
//                }
//            }

//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

    }
    private void updateMemberTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.update_tab_layout, null);
        TextView groupTabTitle = taskTabTitleLayout.findViewById(R.id.tab_title);
        groupTabCount = taskTabTitleLayout.findViewById(R.id.tab_count);
        groupTabTitle.setText("Members");

        int count = WeGroupMembersDao.getMembersCountById(groupId);

        groupTabCount.setText(String.valueOf(count));
        tabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }
    public void setUserName(TextView userName, TextView userId) {
        if (userName != null && userId != null) {
            userName.setText(groupName != null ? groupName + " Group" : "");
            userId.setText(groupId != null ? "ID: " + groupId : "");
        } else {

        }
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

//                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);
//
//                if (childIndexEventClient == null) {
//                    return;
//                }
//
//                saveRegistration(childIndexEventClient, is_edit_mode, EncounterType);

                switch (EncounterType) {

                    case "We Group Member":

                        JSONObject date_created = getFieldJSONObject(fields(jsonFormObject, "step1"), "date_created");
                        String stringDateCreated = date_created.optString("value");

                        JSONObject group_id = getFieldJSONObject(fields(jsonFormObject, "step1"), "group_id");
                        String stringGroupId = group_id.optString("value");

                        JSONObject unique_id = getFieldJSONObject(fields(jsonFormObject, "step1"), "unique_id");
                        String stringUniqueId = unique_id.optString("value");

                        JSONObject first_name = getFieldJSONObject(fields(jsonFormObject, "step1"), "first_name");
                        String stringFirstName = first_name.optString("value");

                        JSONObject last_name = getFieldJSONObject(fields(jsonFormObject, "step1"), "last_name");
                        String stringLastName = last_name.optString("value");

                        JSONObject gender = getFieldJSONObject(fields(jsonFormObject, "step1"), "gender");
                        String stringGender = gender.optString("value");

                        JSONObject nrc = getFieldJSONObject(fields(jsonFormObject, "step1"), "nrc");
                        String stringNrc = nrc.optString("value");

                        JSONObject email = getFieldJSONObject(fields(jsonFormObject, "step1"), "email");
                        String stringEmail = email.optString("value");

                        JSONObject username = getFieldJSONObject(fields(jsonFormObject, "step1"), "username");
                        String stringUsername = username.optString("value");

                        JSONObject password = getFieldJSONObject(fields(jsonFormObject, "step1"), "password");
                        String stringPassword = password.optString("value");

                        JSONObject admission_date = getFieldJSONObject(fields(jsonFormObject, "step1"), "admission_date");
                        String stringAdmissionDate = admission_date.optString("value");

                        JSONObject ecap_hh_ID = getFieldJSONObject(fields(jsonFormObject, "step1"), "ecap_hh_ID");
                        String stringEcapHhID = ecap_hh_ID.optString("value");

                        JSONObject role = getFieldJSONObject(fields(jsonFormObject, "step1"), "role");
                        String stringRole = role.optString("value");

                        JSONObject phone_number = getFieldJSONObject(fields(jsonFormObject, "step1"), "phone_number");
                        String stringPhoneNumber = phone_number.optString("value");

                        JSONObject single_female_caregiver = getFieldJSONObject(fields(jsonFormObject, "step1"), "single_female_caregiver");
                        String stringSingleFemaleCaregiver = single_female_caregiver.optString("value");

                        JSONObject next_of_kin = getFieldJSONObject(fields(jsonFormObject, "step1"), "next_of_kin");
                        String stringNextOfKin = next_of_kin.optString("value");

                        JSONObject next_of_kin_phone = getFieldJSONObject(fields(jsonFormObject, "step1"), "next_of_kin_phone");
                        String stringNextOfKinPhone = next_of_kin_phone.optString("value");

                        JSONObject delete_status = getFieldJSONObject(fields(jsonFormObject, "step1"), "delete_status");
                        String stringDeleteStatus = delete_status.optString("value");



                        postMember(stringDateCreated, stringGroupId, stringUniqueId, stringFirstName, stringLastName, stringGender,
                                stringNrc, stringEmail, stringUsername, stringPassword, stringAdmissionDate, stringEcapHhID,
                                stringRole, stringPhoneNumber, stringSingleFemaleCaregiver, stringNextOfKin, stringNextOfKinPhone, stringDeleteStatus);


                        Toasty.success(getApplicationContext(), "Member Added", Toast.LENGTH_LONG, true).show();
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
                case "We Group Member":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group_member");
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



    private void getToken(String username, String password, String clientId, String clientSecret, final VolleyCallback callback) {
        String url = "https://keycloak.zeir.smartregister.org/auth/realms/ecap-stage/protocol/openid-connect/token";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String accessToken = jsonObject.getString("access_token");
                            callback.onSuccess(accessToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("Error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "password");
                params.put("client_id", clientId);
                params.put("client_secret", clientSecret);
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(WeGroupProfileActivity.this).add(stringRequest);
    }
    private void getCreds(String token){

        Log.i("chobela_token ", "chobela_token" + token);

        String tag_string_creds = "req_creds";

        String url = "https://keycloak.zeir.smartregister.org/auth/realms/ecap-stage/protocol/openid-connect/userinfo";
        StringRequest
                stringRequest
                = new StringRequest(
                Request.Method.GET,
                url,
                (Response.Listener<String>) response -> {

                    try {
                        JSONObject jObj = new JSONObject(response);

                        String sub = jObj.getString("sub");
                        String code = jObj.getString("code");
                        String name = jObj.getString("name");
                        String given_name = jObj.getString("given_name");
                        String family_name = jObj.getString("family_name");
                        String province = jObj.getString("province");
                        String partner = jObj.getString("partner");
                        String phone = jObj.getString("phone");
                        String district = jObj.getString("district");
                        String facility = jObj.getString("facility");
                        String email = jObj.getString("email");
                        String nrc = jObj.getString("nrc");
                        String first_name = jObj.getString("first_name");

                        // save user data
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeGroupProfileActivity.this);
                        SharedPreferences.Editor edit = sp.edit();


                        edit.putString("sub", sub);
                        edit.putString("code", code);
                        edit.putString("caseworker_name", name);
                        edit.putString("given_name", given_name);
                        edit.putString("family_name", family_name);
                        edit.putString("province", province);
                        edit.putString("partner", partner);
                        edit.putString("phone", phone);
                        edit.putString("district", district);
                        edit.putString("facility", facility);
                        edit.putString("email", email);
                        edit.putString("nrc", nrc);

                        edit.commit();

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                },
                error -> {

                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }};


        ChwApplication.getApplicationFlavor().chwAppInstance().addToRequestQueue(stringRequest, tag_string_creds);

    }

    public void postMember(String stringDateCreated, String stringGroupId, String stringUniqueId,
                           String stringFirstName, String stringLastName, String stringGender,
                           String stringNrc, String stringEmail,String stringUsername, String stringPassword,
                           String stringAdmissionDate, String stringEcapHhID, String stringRole,
                           String stringPhoneNumber, String stringSingleFemaleCaregiver,
                           String stringNextOfKin, String stringNextOfKinPhone, String stringDeleteStatus) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                getToken(username, password, BuildConfig.OAUTH_CLIENT_ID, BuildConfig.OAUTH_CLIENT_SECRET, result -> {
                    Log.d("Access Token", result);
                    String token = result;
                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                    httpClient.addInterceptor(new AuthInterceptor(token));
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://keycloak.zeir.smartregister.org/")
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(token)).build())
                            .build();
                    MemberApi memberApi = retrofit.create(MemberApi.class);
                    Map<String, String> attributes = new HashMap<>();
                    attributes.put("date_created", stringDateCreated);
                    attributes.put("group_id", stringGroupId);
                    attributes.put("unique_id",stringUniqueId);
                    attributes.put("gender", stringGender);
                    attributes.put("nrc", stringNrc);
                    attributes.put("admission_date", stringAdmissionDate);
                    attributes.put("ecap_hh_ID", stringEcapHhID);
                    attributes.put("role", stringRole);
                    attributes.put("phone_number", stringPhoneNumber);
                    attributes.put("single_female_caregiver", stringSingleFemaleCaregiver);
                    attributes.put("next_of_kin", stringNextOfKin);
                    attributes.put("next_of_kin_phone", stringNextOfKinPhone);
                    attributes.put("delete_status", stringDeleteStatus);
                    Credentials credential = new Credentials("password",stringPassword,false);
                    List<Credentials> credentialsList = new ArrayList<>();
                    credentialsList.add(credential);
                    MemberListModel member = new MemberListModel(
                            stringUsername,
                            true,
                            false,
                            stringFirstName,
                            stringLastName,
                            stringEmail,
                            attributes,
                            credentialsList
                    );
                    Call<MemberListModel> userCall = memberApi.createUser(member);
                    userCall.enqueue(new Callback<MemberListModel>() {
                        @Override
                        public void onResponse(Call<MemberListModel> userCall, retrofit2.Response<MemberListModel> response) {
                            try {
                                if (response.body() != null && !response.body().toString().isEmpty()) {
                                    Log.d("API Response", "Response received successfully");

                                } else {
                                    Log.e("API Response", "Empty response body");
                                }
                            } catch (Exception e) {
                                Log.e("YourActivityName", "Exception in onResponse", e);
                            }
                        }
                        @Override
                        public void onFailure(Call<MemberListModel> call, Throwable t) {
                            if (t.getMessage() != null) {
                                Log.e("postusererror", "perror : " + t.getMessage());

                                MembersModel member = new MembersModel();
                                member.setDate_created(stringDateCreated);
                                member.setUnique_id(stringUniqueId);
                                member.setGroup_id(stringGroupId);
                                member.setUsername(stringUsername);
                                member.setFirst_name(stringFirstName);
                                member.setLast_name(stringLastName);
                                member.setGender(stringGender);
                                member.setNrc(stringNrc);
                                member.setEmail(stringEmail);
                                member.setAdmission_date(stringAdmissionDate);
                                member.setPassword(stringPassword);
                                member.setEcap_hh_ID(stringEcapHhID);
                                member.setRole(stringRole);
                                member.setPhone_number(stringPhoneNumber);
                                member.setSingle_female_caregiver(stringSingleFemaleCaregiver);
                                member.setNext_of_kin(stringNextOfKin);
                                member.setNext_of_kin_phone(stringNextOfKinPhone);
                                member.setDelete_status(stringDeleteStatus);
//                                    member.setFacilitator_id(stringFacilitatorId);
                                FormUtils formUtils = null;
                                try {
                                    formUtils = new FormUtils(WeGroupProfileActivity.this);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }

                                JSONObject weGroupMemberForm = formUtils.getFormJson("we_group_member");
                                try {
                                    CoreJsonFormUtils.populateJsonForm(weGroupMemberForm, new ObjectMapper().convertValue(member, Map.class));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                ChildIndexEventClient childIndexEventClient = processRegistration(weGroupMemberForm.toString());

                                if (childIndexEventClient == null) {
                                    return;
                                }
                                saveRegistration(childIndexEventClient,false,"We Group Member");



                            } else {
                                Log.e("postusererror", "perror : Throwable t has a null message");
                            }
                        }
                    });
                });
            } else {
                Toast.makeText(WeGroupProfileActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("YourActivityName", "Exception in postMember", e);
        }

    }
    public static String generateGroupId(int length) {
        String numbers = "23456789";
        StringBuilder stringBuilder = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(numbers.length());
            char randomChar = numbers.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }



}