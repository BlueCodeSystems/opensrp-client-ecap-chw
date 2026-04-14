package com.bluecodeltd.ecap.chw.activity;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.EcMotherIndexDao;
import com.bluecodeltd.ecap.chw.model.EcMotherIndexModel;
import com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao;
import com.bluecodeltd.ecap.chw.dao.PmctMotherAncDao;
import com.bluecodeltd.ecap.chw.dao.PmtctChildDao;
import com.bluecodeltd.ecap.chw.dao.PmtctDeliveryDao;
import com.bluecodeltd.ecap.chw.dao.PmtctMotherOutComeDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.AncMotherPmtctFragment;
import com.bluecodeltd.ecap.chw.fragment.PMTCTMotherOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.PmctMotherHeiFragment;
import com.bluecodeltd.ecap.chw.fragment.PostnatalCareFragment;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.PmctMotherAncModel;
import com.bluecodeltd.ecap.chw.model.PmctMotherOutcomeModel;
import com.bluecodeltd.ecap.chw.model.PmtctDeliveryDetailsModel;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;
import com.bluecodeltd.ecap.chw.model.PtmctMotherMonitoringModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.PmtctChildClientIndexUtils;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class MotherPmtctProfileActivity extends AppCompatActivity {

    private com.bluecodeltd.ecap.chw.databinding.ActivityMotherPmtctDetailBinding binding;


    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private Toolbar toolbar;
    public ProfileViewPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    public ViewPager2 mViewPager;
    private TabLayoutMediator tabMediator;
    private String refresh;
    private TextView childTabCount, motherName, txtAge;
    private FloatingActionButton fab;
    CommonPersonObjectClient commonPersonObjectClient, commonMother;
    ObjectMapper oMapper;
    private RelativeLayout cLayout, mLayout,ancLayout,labourLayout,postnatalLayout;
    private UniqueIdRepository uniqueIdRepository;
    public String vca_id;
    public Household family;
    public PtctMotherModel ptctMotherModel;
    public PtmctMotherMonitoringModel ptmctMotherMonitoringModel;
    public PmctMotherAncModel pmctMotherAncModel;
    public PmtctDeliveryDetailsModel pmtctDeliveryDetailsModel;
    public PmctMotherOutcomeModel pmctMotherOutcomeModel;

    Random Number;
    int Rnumber;
    ObjectMapper householdMapper;
    String clientId;
    String launchHouseholdId;

    AlertDialog.Builder builder;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.bluecodeltd.ecap.chw.databinding.ActivityMotherPmtctDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbarx;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);
        // Rely on NavigationMenu to set up navigation icon/drawer like other profile screens
        mTabLayout =  binding.tabs;
        mViewPager  = binding.viewpager;
        motherName = binding.motherName;
        txtAge = binding.motherAge;
        mLayout = binding.motherForm;
        cLayout = binding.childForm;
        ancLayout = binding.ancDetails;
        labourLayout = binding.labourDetails;
        postnatalLayout = binding.postnatalDetails;

        builder = new AlertDialog.Builder(MotherPmtctProfileActivity.this);


        //commonMother = (CommonPersonObjectClient) getIntent().getSerializableExtra("mother");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clientId = extras.getString("client_id");
            launchHouseholdId = extras.getString("household_id");
            if (isNullOrEmpty(launchHouseholdId)) launchHouseholdId = extras.getString("householdId");
            if (isNullOrEmpty(launchHouseholdId)) launchHouseholdId = extras.getString("hh_id");
            Object baseObj = extras.get("baseId");
            if (baseObj instanceof CommonPersonObjectClient) {
                CommonPersonObjectClient baseClient = (CommonPersonObjectClient) baseObj;
                commonPersonObjectClient = baseClient;
                if (isNullOrEmpty(launchHouseholdId)) {
                    launchHouseholdId = baseClient.getColumnmaps().get("household_id");
                }
                if (isNullOrEmpty(clientId)) {
                    clientId = baseClient.getColumnmaps().get("pmtct_id");
                }
            }
            if (isNullOrEmpty(clientId) && !isNullOrEmpty(launchHouseholdId)) {
                clientId = launchHouseholdId;
            }
        }

        ptctMotherModel = null;
        pmtctDeliveryDetailsModel = null;
        pmctMotherAncModel = null;
        pmctMotherOutcomeModel = null;
        motherName.setText("Loading…");
        txtAge.setText("");

        final String cid = clientId;
        final String hhid = launchHouseholdId;
        Threading.io(() -> {
            PtctMotherModel mother = null;
            PmtctDeliveryDetailsModel delivery = null;
            PmctMotherAncModel anc = null;
            PmctMotherOutcomeModel outcome = null;
            try { mother = PMTCTMotherDao.getPMCTMother(cid); } catch (Exception ignored) {}
            // Fallback: some rows are keyed by household_id rather than pmtct_id
            try {
                if (mother == null && !isNullOrEmpty(hhid) && (isNullOrEmpty(cid) || !hhid.equals(cid))) {
                    mother = PMTCTMotherDao.getPMCTMother(hhid);
                }
            } catch (Exception ignored) {}
            // Patch address/phone onto the model from the household record when missing
            applyMotherIndexContactFallback(mother, hhid);
            // Derive the ID to use for delivery, ANC, outcome lookups
            String relatedHouseholdId = null;
            if (mother != null && !isNullOrEmpty(mother.getHousehold_id())) {
                relatedHouseholdId = mother.getHousehold_id();
            }
            if (isNullOrEmpty(relatedHouseholdId) && !isNullOrEmpty(hhid)) relatedHouseholdId = hhid;
            if (isNullOrEmpty(relatedHouseholdId)) relatedHouseholdId = cid;
            try { delivery = PmtctDeliveryDao.getPmtctDeliveryDetails(relatedHouseholdId); } catch (Exception ignored) {}
            try { anc = PmctMotherAncDao.getPMCTMotherAnc(relatedHouseholdId); } catch (Exception ignored) {}
            try { outcome = PmtctMotherOutComeDao.getPMCTmothersOutcome(relatedHouseholdId); } catch (Exception ignored) {}

            final PtctMotherModel finalMother = mother;
            final PmtctDeliveryDetailsModel finalDelivery = delivery;
            final PmctMotherAncModel finalAnc = anc;
            final PmctMotherOutcomeModel finalOutcome = outcome;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                ptctMotherModel = finalMother;
                pmtctDeliveryDetailsModel = finalDelivery;
                pmctMotherAncModel = finalAnc;
                pmctMotherOutcomeModel = finalOutcome;

                if (ptctMotherModel != null) {
                    String mothersFullName = isNullOrEmpty(ptctMotherModel.getCaregiver_name())
                            ? String.format("%s %s", valueOrEmpty(ptctMotherModel.getFirst_name()), valueOrEmpty(ptctMotherModel.getLast_name())).trim()
                            : ptctMotherModel.getCaregiver_name();
                    motherName.setText(isNullOrEmpty(mothersFullName) ? "" : mothersFullName);

                    String mothersAge = ptctMotherModel.getCaregiver_birth_date();
                    if (mothersAge != null) {
                        txtAge.setText(getClientAge(mothersAge));
                    } else {
                        txtAge.setText("N/A");
                    }
                } else {
                    motherName.setText("Name not available");
                    txtAge.setText("Age not available");
                }
                updatePostnatalTitle();
                updateHeiTitle();
                updateAncTitle();
                refreshPmtctFragments();
            });
        });

       oMapper = new ObjectMapper();
//
        fab = binding.fabx;
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        setupViewPager();
        updatePostnatalTitle();
        updateHeiTitle();
        updateAncTitle();
        updateOverviewTitle();
        setupFabVisibility();

    }

    public HashMap<String, CommonPersonObjectClient> getData() {
        return  populateMapWithMother(commonPersonObjectClient);

    }

    public HashMap<String, CommonPersonObjectClient> populateMapWithMother(CommonPersonObjectClient commonPersonObjectClient)
    {
        HashMap<String, CommonPersonObjectClient> motherHashMap = new HashMap<>();
        motherHashMap.put("mother", commonPersonObjectClient);

        return motherHashMap;
    }




    private void setupViewPager(){
        // Rebuild ViewPager2 adapter
        java.util.List<androidx.fragment.app.Fragment> fragments = new java.util.ArrayList<>();
        fragments.add(new PMTCTMotherOverviewFragment());
        fragments.add(new PostnatalCareFragment());
        fragments.add(new PmctMotherHeiFragment());
        fragments.add(new AncMotherPmtctFragment());

        com.bluecodeltd.ecap.chw.adapter.ViewPager2Adapter adapter = new com.bluecodeltd.ecap.chw.adapter.ViewPager2Adapter(this, fragments);
        mViewPager.setAdapter(adapter);
        if (tabMediator != null) { try { tabMediator.detach(); } catch (Exception ignored) {} }
        tabMediator = new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("OVERVIEW"); break;
                case 1: tab.setText("POSTNATAL"); break;
                case 2: tab.setText("HEI"); break;
                case 3: tab.setText("ANC"); break;
            }
        });
        tabMediator.attach();
    }

    @SuppressWarnings("unchecked")
    private <T extends androidx.fragment.app.Fragment> T getPagerFragmentAt(int position, Class<T> type) {
        try {
            androidx.fragment.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);
            if (type.isInstance(fragment)) {
                return (T) fragment;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void refreshPmtctFragments() {
        PMTCTMotherOverviewFragment overviewFragment = getPagerFragmentAt(0, PMTCTMotherOverviewFragment.class);
        if (overviewFragment != null) {
            overviewFragment.refreshViews();
        }

        PmctMotherHeiFragment heiFragment = getPagerFragmentAt(2, PmctMotherHeiFragment.class);
        if (heiFragment != null) {
            heiFragment.refreshViews();
        }

        AncMotherPmtctFragment ancFragment = getPagerFragmentAt(3, AncMotherPmtctFragment.class);
        if (ancFragment != null) {
            ancFragment.refreshViews();
        }
    }

//    private void updateAncTabTitle() {
//        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.pmct_titles, null);
//        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
//        visitTabTitle.setText("ANC");
//        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);
//
//
//        String countANC = PmctMotherAncDao.countMotherAnc(clientId);
//        childTabCount.setText(countANC);
//
//        mTabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
//    }
    private void updatePostnatalTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.pmct_titles, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("POSTNATAL");
        final TextView countView = taskTabTitleLayout.findViewById(R.id.children_count);
        countView.setText("…");
        if (mTabLayout.getTabAt(1) != null) {
            mTabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
        }

        final String cid = clientId;
        Threading.ioBestEffort(() -> {
            String count = "0";
            try {
                if (!isNullOrEmpty(cid)) {
                    count = PMTCTMotherDao.countMotherPostnatal(cid);
                }
            } catch (Exception ignored) {}
            final String finalCount = count;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                countView.setText(finalCount != null ? finalCount : "0");
            });
        });
    }
    private void updateHeiTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.pmct_titles, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("HEI");
        final TextView countView = taskTabTitleLayout.findViewById(R.id.children_count);
        countView.setText("…");
        if (mTabLayout.getTabAt(2) != null) {
            mTabLayout.getTabAt(2).setCustomView(taskTabTitleLayout);
        }

        final String heiSearchId = (ptctMotherModel != null && !isNullOrEmpty(ptctMotherModel.getHousehold_id()))
                ? ptctMotherModel.getHousehold_id()
                : clientId;
        Threading.ioBestEffort(() -> {
            String count = "0";
            try {
                if (!isNullOrEmpty(heiSearchId)) {
                    count = PmtctChildDao.countMotherHei(heiSearchId);
                }
            } catch (Exception ignored) {}
            final String finalCount = count;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                countView.setText(finalCount != null ? finalCount : "0");
            });
        });
    }

    private void updateAncTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.pmct_titles, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("ANC");
        final TextView countView = taskTabTitleLayout.findViewById(R.id.children_count);
        countView.setText("…");
        if (mTabLayout.getTabAt(3) != null) {
            mTabLayout.getTabAt(3).setCustomView(taskTabTitleLayout);
        }

        final String hhId = (ptctMotherModel != null && !isNullOrEmpty(ptctMotherModel.getHousehold_id()))
                ? ptctMotherModel.getHousehold_id()
                : resolveHouseholdId();
        Threading.ioBestEffort(() -> {
            int count = 0;
            try {
                if (!isNullOrEmpty(hhId)) {
                    count = Integer.parseInt(PmctMotherAncDao.countMotherAnc(hhId));
                }
            } catch (Exception ignored) {}
            final String finalCount = String.valueOf(count);
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                countView.setText(finalCount);
            });
        });
    }

    private void updateOverviewTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.pmct_titles, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("OVERVIEW");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);


//        String children = IndexPersonDao.countChildren(commonPersonObjectClient.getColumnmaps().get("household_id"));

        childTabCount.setText("10");
        childTabCount.setVisibility(View.GONE);

        mTabLayout.getTabAt(0).setCustomView(taskTabTitleLayout);
    }
    private String getClientAge(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            if(periodBetweenDateOfBirthAndNow.getYears() == 1){

                return periodBetweenDateOfBirthAndNow.getYears() +" Year Old";

            } else {
                return periodBetweenDateOfBirthAndNow.getYears() +" Years Old";
            }

        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){

            if (periodBetweenDateOfBirthAndNow.getMonths() == 1){

                return periodBetweenDateOfBirthAndNow.getMonths() +" Month Old";

            } else {
                return periodBetweenDateOfBirthAndNow.getMonths() +" Months Old";
            }

        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return periodBetweenDateOfBirthAndNow.getDays() +" Days Old";
        }
        else return "Age Not Set";
    }


    private String getAge(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            return periodBetweenDateOfBirthAndNow.getYears() +" Years";
        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){
            return periodBetweenDateOfBirthAndNow.getMonths() +" Months ";
        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return periodBetweenDateOfBirthAndNow.getDays() +" Days ";
        }
        else return "Not Set";
    }

    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.mother_form:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this,"mother_pmtct");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.child_form:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this,"pmct_child_hei");
                } catch (JSONException e) {
                    e.printStackTrace();
                }





                break;
            case R.id.anc_details:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this,"anc_details");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.labour_details:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this,"labour_delivery");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.postnatal_details:
//                PtctMotherModel model = PMTCTMotherDao.getPostnatalDate(ptctMotherModel.getPmtct_id());


//                String todaysDate = getTodaysDateFormatted();
//                String postNatalCareDate = model != null ? model.getDate_of_st_post_natal_care() : null;
//
//                if (todaysDate != null && todaysDate.equals(postNatalCareDate)) {
//                    Toast.makeText(
//                            MotherPmtctProfileActivity.this, "You cannot conduct a postnatal visit twice a day",
//                            Toast.LENGTH_SHORT
//                    ).show();
//                } else {
                    try {
                        openFormUsingFormUtils(MotherPmtctProfileActivity.this,"postnatal_care");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                }



                break;

            case R.id.fabx:

                animateFAB();

                break;

            case R.id.mother_prof:

                openMotherProfile();

                break;

            case R.id.hh_prof:

                openHouseholdProfile();

                break;

        }
    }

    public void openFormUsingFormUtils(Context context, String formName) throws JSONException {



        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        switch (formName) {

            case "mother_pmtct":

                householdMapper = new ObjectMapper();

                formToBeOpened.put("entity_id",  this.ptctMotherModel.getBase_entity_id());
              //  formToBeOpened.getJSONObject("step1").put("title", this.commonPersonObjectClient.getColumnmaps().get("caregiver_name") + " "  + txtAge.getText().toString());
                CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptctMotherModel, Map.class));
                populateMotherPmtctContactFields(formToBeOpened, ptctMotherModel);

                break;

            case "pmct_child_hei":

                householdMapper = new ObjectMapper();
                Number = new Random();
                Rnumber = Number.nextInt(900000000);
                String newEntityId =  Integer.toString(Rnumber);

                JSONObject stepOneUniqueId = getFieldJSONObject(fields(formToBeOpened, STEP1), "unique_id");

                if (stepOneUniqueId != null) {
                    stepOneUniqueId.remove(JsonFormUtils.VALUE);
                    try {
                        stepOneUniqueId.put(JsonFormUtils.VALUE, newEntityId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptctMotherModel, Map.class));

                break;
            case "anc_details":
                if (ptctMotherModel != null && !isNullOrEmpty(ptctMotherModel.getHousehold_id())) {
                    JSONObject householdIdField = getFieldJSONObject(fields(formToBeOpened, STEP1), "household_id");
                    if (householdIdField != null) {
                        householdIdField.put(JsonFormUtils.VALUE, ptctMotherModel.getHousehold_id());
                    }
                }
                break;

            case "labour_delivery":
                householdMapper = new ObjectMapper();
                if(pmtctDeliveryDetailsModel == null){
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptctMotherModel, Map.class));
                }
                else {
                    formToBeOpened.put("entity_id",  this.pmtctDeliveryDetailsModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(pmtctDeliveryDetailsModel, Map.class));
                }

                break;


            case "postnatal_care":
                householdMapper = new ObjectMapper();
                PtctMotherModel model = new PtctMotherModel();
                model.setPmtct_id(ptctMotherModel.getPmtct_id());
//                if(ptmctMotherMonitoringModel == null){
//                    formToBeOpened.put("entity_id",  this.ptctMotherModel.getBase_entity_id());
                CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptctMotherModel, Map.class));

//                }
//                else {
//                    formToBeOpened.put("entity_id",  this.ptmctMotherMonitoringModel.getBase_entity_id());
//                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptmctMotherMonitoringModel, Map.class));
//                }

                break;

            case "pmtct_outcome":
                householdMapper = new ObjectMapper();
                if(pmctMotherOutcomeModel == null){
                CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptctMotherModel, Map.class));
                }
                else {
                    formToBeOpened.put("entity_id",  this.pmctMotherOutcomeModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(pmctMotherOutcomeModel, Map.class));
                }

break;

        }
        ensurePmtctHouseholdLinking(formToBeOpened);
        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setName(getString(org.smartregister.chw.core.R.string.child_details));
        form.setHideSaveLabel(true);
        form.setNextLabel(getString(R.string.next));
        form.setPreviousLabel(getString(R.string.previous));
        form.setSaveLabel(getString(R.string.submit));
        form.setNavigationBackground(R.color.primary);
        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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


            try {
                ensurePmtctHouseholdLinking(jsonFormObject);
                jsonString = jsonFormObject.toString();
            } catch (Exception ignored) { }

            if(!jsonFormObject.optString("entity_id").isEmpty()){
                is_edit_mode = true;
            }

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, is_edit_mode);

                getUniqueIdRepository().close(vca_id);
                String encounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");

                switch (encounterType) {

                    case "Mother Pmtct Child":
                    case "Mother Pmtct":
                    case "Mother Pmtct Postnatal":
                    case "Mother Pmtct Delivery":
                        Toasty.success(MotherPmtctProfileActivity.this, "Form Saved", Toast.LENGTH_LONG, true).show();

                        finish();
                        startActivity(getIntent());

                        break;

                }

                Toasty.success(MotherPmtctProfileActivity.this, "Form Saved", Toast.LENGTH_LONG, true).show();

                finish();
                startActivity(getIntent());

            } catch (Exception e) {
                Timber.e(e);
            }

        }

    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

    public ChildIndexEventClient processRegistration(String jsonString) {

        try {
            JSONObject formJsonObject = new JSONObject(jsonString);

            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);

            String entityId = formJsonObject.optString("entity_id");

            if (entityId.isEmpty()) {
                entityId = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }


            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);

            JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);

            switch (encounterType) {
                case "Mother Pmtct":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_mother");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Mother Pmtct Child":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_child");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Mother Pmtct ANC":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_mother_anc");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Mother Pmtct Postnatal":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_mother_postnatal");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Mother Pmtct Delivery":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_delivery_details");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "PMTCT Mother Outcome":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_mother_outcome");
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

                    if (isEditMode && existingClientJsonObject != null) {
                        JSONObject mergedClientJsonObject =
                                org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
                        PmtctChildClientIndexUtils.mirrorClientIndexAttributes(mergedClientJsonObject, event);
                        ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);
                    } else {
                        PmtctChildClientIndexUtils.mirrorClientIndexAttributes(newClientJsonObject, event);
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


    public AllSharedPreferences getAllSharedPreferences () {
        return ChwApplication.getInstance().getContext().allSharedPreferences();
    }

    private ClientProcessorForJava getClientProcessorForJava() {
        return ChwApplication.getInstance().getClientProcessorForJava();
    }


    private void setupFabVisibility() {
        try {
            fab.setVisibility(mViewPager.getCurrentItem() == 0 ? View.VISIBLE : View.GONE);
        } catch (Exception ignored) { }

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position != 0 && isFabOpen) {
                    closeFab();
                }
                fab.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void animateFAB(){

        if (isFabOpen){

            closeFab();

        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);
            mLayout.setVisibility(View.VISIBLE);
            cLayout.setVisibility(View.VISIBLE);
            ancLayout.setVisibility(View.VISIBLE);
            labourLayout.setVisibility(View.VISIBLE);
            postnatalLayout.setVisibility(View.VISIBLE);

        }
    }

    public void closeFab(){
        fab.startAnimation(rotate_backward);
        isFabOpen = false;
        cLayout.setVisibility(View.GONE);
        mLayout.setVisibility(View.GONE);
        ancLayout.setVisibility(View.GONE);
        labourLayout.setVisibility(View.GONE);
        postnatalLayout.setVisibility(View.GONE);
    }
    public HashMap<String, PtctMotherModel> getClientDetails() {

        HashMap<String, PtctMotherModel> map = new HashMap<>();

        map.put("client",ptctMotherModel);

        return map;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pmtct_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                finish();
                startActivity(getIntent());

                break;
            case R.id.case_status:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this, "pmtct_outcome");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;

            case R.id.delete_record:


            Boolean checkForLinks = PmtctChildDao.hasDeletedHei(clientId);
            if (checkForLinks == false) {


                builder.setMessage("You are about to delete this record");
                builder.setNegativeButton("NO", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();

                }).setPositiveButton("YES", ((dialogInterface, i) -> {
                    FormUtils formUtils = null;
                    try {
                        formUtils = new FormUtils(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ptctMotherModel.setDelete_status("1");
                    JSONObject openForm = formUtils.getFormJson("mother_pmtct");
                    try {
                        CoreJsonFormUtils.populateJsonForm(openForm, new ObjectMapper().convertValue(ptctMotherModel, Map.class));
                        openForm.put("entity_id", ptctMotherModel.getBase_entity_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {

                        ChildIndexEventClient childIndexEventClient = processRegistration(openForm.toString());
                        if (childIndexEventClient == null) {
                            return;
                        }
                        saveRegistration(childIndexEventClient, true);


                    } catch (Exception e) {
                        Timber.e(e);
                    }


                    Toasty.success(MotherPmtctProfileActivity.this, "Deleted", Toast.LENGTH_LONG, true).show();


//                            super.onBackPressed();

                    Intent returnToRegister = new Intent(this, PMTCTRegisterActivity.class);
                    startActivity(returnToRegister);
                    finish();
                }));

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert");
                alert.show();
            } else{
                Toasty.success(MotherPmtctProfileActivity.this, "Delete the HEI before deleting the record", Toast.LENGTH_LONG, true).show();
            }
            break;




        }
        return super.onOptionsItemSelected(item);
    }

    private void openMotherProfile() {
        String householdId = resolveHouseholdId();
        if (isNullOrEmpty(householdId)) {
            Toasty.warning(MotherPmtctProfileActivity.this, "Household record not found", Toast.LENGTH_LONG, true).show();
            return;
        }

        CommonPersonObjectClient motherClient = EcMotherIndexDao.getFirstMotherByHousehold(householdId);
        if (motherClient == null) {
            Toasty.warning(MotherPmtctProfileActivity.this, "Mother record not found", Toast.LENGTH_LONG, true).show();
            return;
        }

        Intent intent = new Intent(this, MotherDetail.class);
        intent.putExtra("mother", motherClient);
        startActivity(intent);
    }

    private void openHouseholdProfile() {
        String householdId = resolveHouseholdId();
        if (isNullOrEmpty(householdId)) {
            Toasty.warning(MotherPmtctProfileActivity.this, "Household record not found", Toast.LENGTH_LONG, true).show();
            return;
        }

        Intent intent = new Intent(this, HouseholdDetails.class);
        intent.putExtra("householdId", householdId);
        startActivity(intent);
    }

    /** Patches address/phone onto the model from the household (EcMotherIndex) record when they are missing. Called from IO thread. */
    private void applyMotherIndexContactFallback(PtctMotherModel pmtctMother, String fallbackHouseholdId) {
        if (pmtctMother == null) return;
        if (!isNullOrEmpty(pmtctMother.getHome_address()) && !isNullOrEmpty(pmtctMother.getMothers_phone())) return;
        String householdId = !isNullOrEmpty(pmtctMother.getHousehold_id())
                ? pmtctMother.getHousehold_id()
                : fallbackHouseholdId;
        if (isNullOrEmpty(householdId)) return;
        try {
            List<EcMotherIndexModel> ecMothers = EcMotherIndexDao.getMothers(householdId);
            if (!ecMothers.isEmpty()) {
                EcMotherIndexModel ecMother = ecMothers.get(0);
                if (isNullOrEmpty(pmtctMother.getHome_address()) && !isNullOrEmpty(ecMother.getHome_address())) {
                    pmtctMother.setHome_address(ecMother.getHome_address());
                }
                if (isNullOrEmpty(pmtctMother.getMothers_phone()) && !isNullOrEmpty(ecMother.getMothers_phone())) {
                    pmtctMother.setMothers_phone(ecMother.getMothers_phone());
                }
            }
        } catch (Exception ignored) {}
    }

    private String resolveHouseholdId() {
        if (ptctMotherModel != null && !isNullOrEmpty(ptctMotherModel.getHousehold_id())) {
            return ptctMotherModel.getHousehold_id();
        }
        if (commonPersonObjectClient != null) {
            try {
                String householdId = commonPersonObjectClient.getColumnmaps().get("household_id");
                if (!isNullOrEmpty(householdId)) {
                    return householdId;
                }
            } catch (Exception ignored) { }
        }
        if (!isNullOrEmpty(launchHouseholdId)) {
            return launchHouseholdId;
        }
        return null;
    }

    private void ensurePmtctHouseholdLinking(JSONObject form) throws JSONException {
        if (form == null) return;
        String encounterType = form.optString(JsonFormConstants.ENCOUNTER_TYPE, "");
        if (!isHouseholdLinkedPmtctEncounter(encounterType)) return;

        String householdId = resolveHouseholdId();
        if (isNullOrEmpty(householdId) && ptctMotherModel != null) {
            householdId = ptctMotherModel.getHousehold_id();
        }
        if (isNullOrEmpty(householdId)) {
            householdId = launchHouseholdId;
        }

        String pmtctId = ptctMotherModel != null ? ptctMotherModel.getPmtct_id() : null;
        if (isNullOrEmpty(pmtctId)) {
            pmtctId = clientId;
        }

        if (!isNullOrEmpty(householdId)) {
            setFieldValue(form, "household_id", householdId);
            setFieldValue(form, "pmtct_id", householdId);
            return;
        }
        if (!isNullOrEmpty(pmtctId)) {
            setFieldValueIfMissing(form, "pmtct_id", pmtctId);
        }
    }

    private boolean isHouseholdLinkedPmtctEncounter(String encounterType) {
        if (isNullOrEmpty(encounterType)) return false;
        switch (encounterType) {
            case "Mother Pmtct":
            case "Mother Pmtct Child":
            case "Mother Pmtct ANC":
            case "ANC":
            case "Mother Pmtct Postnatal":
            case "Mother Pmtct Delivery":
            case "PMTCT Mother Outcome":
                return true;
            default:
                return false;
        }
    }

    private void setFieldValue(JSONObject form, String key, String value) throws JSONException {
        if (isNullOrEmpty(value)) return;
        JSONObject field = getFieldJSONObject(fields(form, STEP1), key);
        if (field == null) return;
        field.put(JsonFormUtils.VALUE, value);
    }

    private void setFieldValueIfMissing(JSONObject form, String key, String value) throws JSONException {
        if (isNullOrEmpty(value)) return;
        JSONObject field = getFieldJSONObject(fields(form, STEP1), key);
        if (field == null) return;
        if (isNullOrEmpty(field.optString(JsonFormUtils.VALUE))) {
            field.put(JsonFormUtils.VALUE, value);
        }
    }

    private void populateMotherPmtctContactFields(JSONObject formToBeOpened, PtctMotherModel model) throws JSONException {
        if (formToBeOpened == null || model == null) return;
        setFieldValueIfMissing(formToBeOpened, "homeaddress", model.getHome_address());
        setFieldValueIfMissing(formToBeOpened, "landmark", model.getNearest_landmark());
        setFieldValueIfMissing(formToBeOpened, "caregiver_phone", model.getMothers_phone());
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
    private String valueOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }
    public static String getTodaysDateFormatted() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.now().format(dtf);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PMTCTRegisterActivity.class);
        startActivity(intent);
        this.finish();

    }
}
