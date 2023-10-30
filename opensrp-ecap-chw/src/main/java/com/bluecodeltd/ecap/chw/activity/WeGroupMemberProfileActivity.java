package com.bluecodeltd.ecap.chw.activity;

import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ViewPagerAdapterFragment;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.ConstituitionFragment;
import com.bluecodeltd.ecap.chw.fragment.MembersFragment;
import com.bluecodeltd.ecap.chw.fragment.ServicesFragment;
import com.bluecodeltd.ecap.chw.fragment.SummaryFragment;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class WeGroupMemberProfileActivity extends AppCompatActivity {
    private ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton mAddFab, addSavings, addLoan, addFine,addSocialFund, addRepayment, addIga;

    TextView addSavingsActionText, addLoanActionText,  addFineActionText,addSocialFundActionText, addRepaymentActionText, addIgaActionText;

    View dimBackground;
    Boolean isAllFabsVisible;
    TextView userName,userId;

    String name,id;

    @SuppressLint({"MissingInflatedId", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        name = getIntent().getStringExtra("userName");
        id = getIntent().getStringExtra("userId");

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
        dimBackground = findViewById(R.id.dimBackground);
        returnViewPager();

        addSavings = findViewById(R.id.add_saving_fab);
        addLoan = findViewById(R.id.add_loan_fab);
        addFine = findViewById(R.id.add_fine_fab);
        addSocialFund = findViewById(R.id.add_social_fund_fab);
        addRepayment = findViewById(R.id.add_repayment_fab);
        addIga = findViewById(R.id.add_iga_fab);

        addSavingsActionText = findViewById(R.id.add_saving_action_text);
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
                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(getBaseContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject indexRegisterForm;

                indexRegisterForm = formUtils.getFormJson("we_group_member_savings");
                startFormActivity(indexRegisterForm);
            }
        });
//

        addLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(getBaseContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject indexRegisterForm;

                indexRegisterForm = formUtils.getFormJson("we_group_member_loan");
                startFormActivity(indexRegisterForm);
            }
        });
        addFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(getBaseContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject indexRegisterForm;

                indexRegisterForm = formUtils.getFormJson("we_group_member_fine");
                startFormActivity(indexRegisterForm);
            }
        });
        addSocialFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(getBaseContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject indexRegisterForm;

                indexRegisterForm = formUtils.getFormJson("we_group_member_social_fund");
                startFormActivity(indexRegisterForm);
            }
        });
//        addRepayment.setOnClickListener(
//                view -> Toast.makeText(NewMemberActivity.this, "Repayment Added", Toast.LENGTH_SHORT
//                ).show());
//        addIga.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                Intent intent = new Intent(NewMemberActivity.this, BookWriterPostIGA.class);
//                intent.putExtra("userName", name);
//                intent.putExtra("userId", id);
//                startActivity(intent);
//                startActivity( intent);
//            }
//        });
    }

    public  void returnViewPager(){
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SummaryFragment());
        fragments.add(new MembersFragment());
        fragments.add(new ServicesFragment());
//        fragments.add(new ConstituitionFragment());


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
                        Toasty.success(getApplicationContext(), "Group Saved", Toast.LENGTH_LONG, true).show();
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

}
