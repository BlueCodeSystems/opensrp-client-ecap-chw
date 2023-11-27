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
import android.widget.Button;
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
import com.bluecodeltd.ecap.chw.dao.WeGroupDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberLoanDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSavingDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSocialFundDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.WeGroupDataCollectionFragment;
import com.bluecodeltd.ecap.chw.fragment.WeGroupFragmentMembers;
import com.bluecodeltd.ecap.chw.fragment.WeGroupMemberMeetingRegister;
import com.bluecodeltd.ecap.chw.fragment.WeGroupProfileSummary;
import com.bluecodeltd.ecap.chw.fragment.WeGroupServiceLedgerFragment;
import com.bluecodeltd.ecap.chw.interceptor.AuthInterceptor;
import com.bluecodeltd.ecap.chw.model.Credentials;
import com.bluecodeltd.ecap.chw.model.MemberListModel;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupDataCollectionModel;
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
    FloatingActionButton mAddFab,editGroupFab,addMembersFab, addGroupGIGASFab,addCashBookFab, addCashInBoxFab, addSocialDiscussionsFab,weGroupDataCollectionFab,socialDiscussionsFab;

    TextView editGroupActionText,addMembersActionText, addGroupGIGASActionText, addCashBookActionText, addCashBookInBoxActionText,socialDiscussionsActionText, weGroupDataCollectionActionText;

    View dimBackground;
    Boolean isAllFabsVisible;
    TextView userName,userId;

    String groupName, groupId,username,password;
    MembersModel membersModel;
    WeGroupModel weGroupModel;
    WeGroupDataCollectionModel weGroupDataCollectionModel;
    Handler handler = new Handler();
    private final int FIVE_SECONDS = 2000;
    Runnable runnable;


    TextView groupTabCount;
    ObjectMapper oMapper;

    String memberRole;
    SharedPreferences sp,credentials;
    Button go_to_register;

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

        go_to_register = findViewById(R.id.go_to_register);
        go_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent register = new Intent(WeGroupProfileActivity.this,UserRegisterActivity.class);
                register.putExtra("groupName", weGroupModel.getGroup_name());
                register.putExtra("groupId", weGroupModel.getGroup_id());
                startActivity(register);
            }
        });

        groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getStringExtra("groupId");

        credentials = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        username = credentials.getString("username", "");
        password = credentials.getString("password", "");

        sp = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        memberRole = sp.getString("userRole", "").toLowerCase();

        membersModel =  WeGroupMembersDao.getWeGroupMemberById(groupId);
        weGroupModel = WeGroupDao.getWeGroupsById(groupId);
//        weGroupDataCollectionModel = WeGroupDataCollectionDao.getWeGroupDataCollectionId(groupId);

        userName = findViewById(R.id.user_name);
        userId = findViewById(R.id.user_id);
        setUserName(userName,userId);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        View rootView = findViewById(R.id.root);



        rootView.setOnClickListener(view -> {

            if (isAllFabsVisible) {
//                mAddFab.hide();
                editGroupFab.setVisibility(View.GONE);
                addMembersFab.setVisibility(View.GONE);
                addGroupGIGASFab.setVisibility(View.GONE);
                addCashBookFab.setVisibility(View.GONE);
                addCashInBoxFab.setVisibility(View.GONE);
                addSocialDiscussionsFab.setVisibility(View.GONE);
                weGroupDataCollectionFab.setVisibility(View.GONE);


                editGroupActionText.setVisibility(View.GONE);
                addMembersActionText.setVisibility(View.GONE);
                addGroupGIGASActionText.setVisibility(View.GONE);
                addCashBookActionText.setVisibility(View.GONE);
                addCashBookInBoxActionText.setVisibility(View.GONE);
                socialDiscussionsActionText.setVisibility(View.GONE);
                weGroupDataCollectionActionText.setVisibility(View.GONE);
//                dimBackground.setVisibility(View.GONE);
                isAllFabsVisible = false;
            }
        });

        mAddFab = findViewById(R.id.add_fab);
        if(memberRole != null && (memberRole.equals("bookwriter") || memberRole.equals("facilitator"))){
            mAddFab.setVisibility(View.VISIBLE);
        } else {
            mAddFab.setVisibility(View.GONE);
        }


        dimBackground = findViewById(R.id.dimBackground);

        returnViewPager();
        updateMemberTabTitle();



        editGroupFab = findViewById(R.id.edit_group_fab);
        addMembersFab = findViewById(R.id.add_members_fab);
        addGroupGIGASFab = findViewById(R.id.add_add_gigas_fab);
        addCashBookFab = findViewById(R.id.add_cash_book_fab);
        addCashInBoxFab = findViewById(R.id.add_cash_in_box_fab);
        addSocialDiscussionsFab = findViewById(R.id.add_social_discussions_fab);
        weGroupDataCollectionFab = findViewById(R.id.we_group_data_collection_fab);


        editGroupActionText = findViewById(R.id.add_fine_action_text);
        addMembersActionText = findViewById(R.id.add_members_action_text);
        addGroupGIGASActionText = findViewById(R.id.add_gigas_action_text);
        addCashBookActionText = findViewById(R.id.add_cashbook_action_text);
        addCashBookInBoxActionText = findViewById(R.id.add_cash_in_box_action_text);
        socialDiscussionsActionText = findViewById(R.id.add_social_discussions_text);
        weGroupDataCollectionActionText = findViewById(R.id.we_group_data_collection_action_text);

        editGroupFab.setVisibility(View.GONE);
        addMembersFab.setVisibility(View.GONE);
        addGroupGIGASFab.setVisibility(View.GONE);
        addCashBookFab.setVisibility(View.GONE);
        addCashInBoxFab.setVisibility(View.GONE);
        addSocialDiscussionsFab.setVisibility(View.GONE);
        weGroupDataCollectionFab.setVisibility(View.GONE);


        editGroupActionText.setVisibility(View.GONE);
        addMembersActionText.setVisibility(View.GONE);
        addGroupGIGASActionText.setVisibility(View.GONE);
        addCashBookActionText.setVisibility(View.GONE);
        addCashBookInBoxActionText.setVisibility(View.GONE);
        socialDiscussionsActionText.setVisibility(View.GONE);
        weGroupDataCollectionActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddFab.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                dimBackground.setVisibility(View.VISIBLE);
                // Set visibility to View.VISIBLE for FABs
                editGroupFab.setVisibility(View.VISIBLE);
                addMembersFab.setVisibility(View.VISIBLE);
                addGroupGIGASFab.setVisibility(View.VISIBLE);
                addCashBookFab.setVisibility(View.VISIBLE);
                addCashInBoxFab.setVisibility(View.VISIBLE);
                addSocialDiscussionsFab.setVisibility(View.VISIBLE);
                weGroupDataCollectionFab.setVisibility(View.VISIBLE);

// Set visibility to View.VISIBLE for action text views
                editGroupActionText.setVisibility(View.VISIBLE);
                addMembersActionText.setVisibility(View.VISIBLE);
                addGroupGIGASActionText.setVisibility(View.VISIBLE);
                addCashBookActionText.setVisibility(View.VISIBLE);
                addCashBookInBoxActionText.setVisibility(View.VISIBLE);
                socialDiscussionsActionText.setVisibility(View.VISIBLE);
                weGroupDataCollectionActionText.setVisibility(View.VISIBLE);
                isAllFabsVisible = true;
            } else {
                editGroupFab.setVisibility(View.GONE);
                addMembersFab.setVisibility(View.GONE);
                addGroupGIGASFab.setVisibility(View.GONE);
                addCashBookFab.setVisibility(View.GONE);
                addCashInBoxFab.setVisibility(View.GONE);
                addSocialDiscussionsFab.setVisibility(View.GONE);
                weGroupDataCollectionFab.setVisibility(View.GONE);

                editGroupActionText.setVisibility(View.GONE);
                addMembersActionText.setVisibility(View.GONE);
                addGroupGIGASActionText.setVisibility(View.GONE);
                addCashBookActionText.setVisibility(View.GONE);
                addCashBookInBoxActionText.setVisibility(View.GONE);
                socialDiscussionsActionText.setVisibility(View.GONE);
                weGroupDataCollectionActionText.setVisibility(View.GONE);

                dimBackground.setVisibility(View.GONE);

                isAllFabsVisible = false;
            }
        });
        addMembersFab.setOnClickListener(new View.OnClickListener() {
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
                startFormActivity(indexRegisterForm);
            }
        });
//

        weGroupDataCollectionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(getBaseContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject indexRegisterForm;

                indexRegisterForm = formUtils.getFormJson("we_group_data_collection_form");

                JSONObject dateClientCreated = getFieldJSONObject(fields(indexRegisterForm, STEP1), "date_created");
                if (dateClientCreated  != null) {
                    dateClientCreated.remove(JsonFormUtils.VALUE);
                    try {
                        dateClientCreated.put(JsonFormUtils.VALUE, getFormattedDate());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                WeGroupDataCollectionModel model = new WeGroupDataCollectionModel();
                model.setDate_created(getFormattedDate());
                model.setGroup_id(weGroupModel.getGroup_id());
                model.setGroup_name(weGroupModel.getGroup_name());
                model.setCycle_number(weGroupModel.getCycle_number());
                model.setBase_entity_id(weGroupModel.getBase_entity_id());
                model.setAnnual_interest_rate(weGroupModel.getAnnual_interest_rate());
                model.setFirst_training_meeting_date(weGroupModel.getFirst_training_meeting_date());
                model.setDate_savings_started(weGroupModel.getDate_savings_started());
                model.setReinvested_savings_cycle_start(weGroupModel.getReinvested_savings_cycle_start());
                model.setRegistered_members_cycle_start(weGroupModel.getRegistered_members_cycle_start());
                model.setGroup_mgt(weGroupModel.getGroup_mgt());

                //Saving and Loan Info
                model.setCumulative_savings_current_cycle(String.valueOf(WeGroupMemberSavingDao.getTotalGroupAmount(groupId)));
                model.setCumulative_loans_current_cycle(String.valueOf(WeGroupMemberLoanDao.getTotalGroupAmount(groupId)));
                model.setSocial_fund_balance(String.valueOf(WeGroupMemberSocialFundDao.getTotalGroupAmount(groupId)));
                model.setNumber_member_never_taken_loan(String.valueOf(WeGroupMemberLoanDao.getTotalGroupMemberWithoutLoan(groupId)));
                model.setMembers_taken_loan(String.valueOf(WeGroupMemberLoanDao.getTotalGroupMemberWithLoan(groupId)));

                //Members Info
                model.setRegistered_members_current_cycle(String.valueOf(WeGroupMembersDao.getMembersCountById(groupId)));
                model.setRegistered_members_time_visit(String.valueOf(WeGroupMembersDao.getMembersCountById(groupId)));
                model.setRegistered_women_time_visit(String.valueOf(WeGroupMembersDao.getMembersCountByFemale(groupId)));
                model.setNumber_members_single_female_caregivers(String.valueOf(WeGroupMembersDao.getMembersCountByFemaleCaregiver(groupId)));
                model.setNumber_members_enrolled_ecapii(String.valueOf(WeGroupMembersDao.getMembersCountByEcapId(groupId)));



                try {
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, new ObjectMapper().convertValue(model, Map.class));
//                    indexRegisterForm.put("entity_id", weGroupModel.getBase_entity_id());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startFormActivity(indexRegisterForm);
            }
        });
        editGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(getBaseContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject indexRegisterForm;

                indexRegisterForm = formUtils.getFormJson("we_group_form");
                try {
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, new ObjectMapper().convertValue(weGroupModel, Map.class));
                    indexRegisterForm.put("entity_id", weGroupModel.getBase_entity_id());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                JSONObject dateClientCreated = getFieldJSONObject(fields(indexRegisterForm, STEP1), "date_created");
//                if (dateClientCreated  != null) {
//                    dateClientCreated.remove(JsonFormUtils.VALUE);
//                    try {
//                        dateClientCreated.put(JsonFormUtils.VALUE, getFormattedDate());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }

                startFormActivity(indexRegisterForm);
            }
        });
        addGroupGIGASFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(getBaseContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject indexRegisterForm;

                indexRegisterForm = formUtils.getFormJson("wegroup_gigas");
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

                startFormActivity(indexRegisterForm);
            }
        });
        addCashBookFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(getBaseContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject indexRegisterForm;

                indexRegisterForm = formUtils.getFormJson("we_cashbook");

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
                startFormActivity(indexRegisterForm);
            }
        });

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

        fragments.add(new WeGroupServiceLedgerFragment());
        fragments.add(new WeGroupDataCollectionFragment());
        fragments.add(new WeGroupMemberMeetingRegister());


        ViewPagerAdapterFragment adapter = new ViewPagerAdapterFragment(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("SUMMARY");
        tabLayout.getTabAt(1).setText("MEMBERS");
        tabLayout.getTabAt(2).setText("LEDGER");
        tabLayout.getTabAt(3).setText("REPORTS");
        tabLayout.getTabAt(4).setText("REGISTER");

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


                if(EncounterType != null && (EncounterType.equals("Group")  || EncounterType.equals("WE Group Data Collection") )){
                    ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);
                    if (childIndexEventClient == null) {
                        return;
                    }
                    saveRegistration(childIndexEventClient, is_edit_mode, EncounterType);
                }


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

                        if(EncounterType != null && (EncounterType.equals("Group")  || EncounterType.equals("WE Group Data Collection") )){
                            Toasty.success(getApplicationContext(), "Form Saved", Toast.LENGTH_LONG, true).show();
                            finish();
                            startActivity(getIntent());
                        }

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
                case "WE Group Data Collection":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_we_group_data_collection");
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
                                if (response.isSuccessful()) {
                                    MemberListModel memberListModel = response.body();

                                    if (memberListModel != null) {
                                        // Check if the response body is empty
                                        if (!memberListModel.toString().isEmpty()) {
                                            // Handle the response
                                            Log.d("API Response", "Response received successfully");
                                        } else {
                                            // Handle empty response body
                                            Log.e("API Response", "Empty response body");
                                        }
                                    } else {
                                        // Handle null response body
                                        Log.e("API Response", "Null response body");
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    Log.e("API Response", "Unsuccessful response: " + response.code());
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
                                Toasty.success(getApplicationContext(), "Member Added", Toast.LENGTH_LONG, true).show();
                                finish();
                                startActivity(getIntent());


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
    public HashMap<String, WeGroupModel> getGroupData() {


        HashMap<String, WeGroupModel> map = new HashMap<>();

        map.put("groupID",weGroupModel);

        return map;

    }



}