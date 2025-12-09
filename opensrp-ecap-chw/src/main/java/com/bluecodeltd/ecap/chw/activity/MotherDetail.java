package com.bluecodeltd.ecap.chw.activity;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.chw.core.utils.CoreReferralUtils.getCommonRepository;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bluecodeltd.ecap.chw.model.MotherAncModel;
import com.google.android.material.tabs.TabLayoutMediator;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.MotherAncDao;
import com.bluecodeltd.ecap.chw.dao.MotherDeliveryDao;
import com.bluecodeltd.ecap.chw.dao.MotherOutcomeDao;
import com.bluecodeltd.ecap.chw.dao.MotherLongitudinalFollowUpDao;
import com.bluecodeltd.ecap.chw.dao.MotherPostnatalCareDao;
import com.bluecodeltd.ecap.chw.dao.EcMotherIndexDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.MotherChildrenFragment;
import com.bluecodeltd.ecap.chw.fragment.MotherAncFragment;
import com.bluecodeltd.ecap.chw.fragment.MotherOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.MotherLongitudinalFragment;
import com.bluecodeltd.ecap.chw.fragment.MotherPostnatalFragment;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.MotherDeliveryModel;
import com.bluecodeltd.ecap.chw.model.MotherOutcomeModel;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;
import com.bluecodeltd.ecap.chw.model.EcMotherIndexModel;
import com.bluecodeltd.ecap.chw.util.Constants;
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
import org.smartregister.commonregistry.CommonPersonObject;
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
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class MotherDetail extends AppCompatActivity {

    private com.bluecodeltd.ecap.chw.databinding.ActivityMotherDetailBinding binding;


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
    private RelativeLayout cLayout, mLayout,
            motherAncLayout, motherDeliveryLayout, motherLongitudinalLayout,
            motherOutcomeLayout, motherPostnatalLayout,
            childFinalOutcomeLayout, childLongitudinalLayout, childPostnatalLayout;
    private boolean caregiverHivPositive;
    private UniqueIdRepository uniqueIdRepository;
    public String vca_id;
    public Household family;
    private EcMotherIndexModel motherIndex;
    Random Number;
    int Rnumber;
    ObjectMapper householdMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.bluecodeltd.ecap.chw.databinding.ActivityMotherDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbarx;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);
        mTabLayout =  binding.tabs;
        mViewPager  = binding.viewpager;
        motherName = binding.motherName;
        txtAge = binding.motherAge;
        // Pre-filled header fields
        TextView householdIdView = binding.householdId;
        TextView motherFacilityView = binding.motherFacility;
        TextView motherLastVisitView = binding.motherLastVisit;
        TextView motherNextAppointmentView = binding.motherNextAppointment;
        mLayout = binding.motherForm;
        cLayout = binding.childForm;
        motherAncLayout = findViewById(R.id.mother_anc);
        motherDeliveryLayout = findViewById(R.id.mother_delivery);
        motherLongitudinalLayout = findViewById(R.id.mother_longitudinal_follow_up);
        motherOutcomeLayout = findViewById(R.id.mother_outcome);
        motherPostnatalLayout = findViewById(R.id.mother_postnatal_care);
        childFinalOutcomeLayout = findViewById(R.id.child_final_outcome);
        childLongitudinalLayout = findViewById(R.id.child_longitudinal_follow_up);
        childPostnatalLayout = findViewById(R.id.child_postnatal_care);

        commonMother = (CommonPersonObjectClient) getIntent().getSerializableExtra("mother");
        // Refresh flag (optional extra)
        try {
            refresh = getIntent() != null && getIntent().getExtras() != null ? getIntent().getExtras().getString("refresh") : null;
        } catch (Exception ignored) {}

        // Prefer the serialized mother passed via intent to avoid schema mismatches
        boolean forceRefreshFromRepo = refresh != null && refresh.equalsIgnoreCase("true");
        if (commonMother != null && !forceRefreshFromRepo) {
            commonPersonObjectClient = commonMother;
        } else {
            // Fallback: try resolve using extras but guard for failures
            String baseId = null;
            try {
                if (getIntent() != null && getIntent().getExtras() != null) {
                    baseId = getIntent().getExtras().getString("base_entity_id", null);
                    if (baseId == null) baseId = getIntent().getExtras().getString("baseId", null);
                }
            } catch (Exception ignored) {}

            // If we are refreshing, prefer pulling the baseId from the existing cached mother payload
            if ((baseId == null || baseId.isEmpty()) && commonMother != null) {
                try {
                    baseId = commonMother.getColumnmaps().get("base_entity_id");
                } catch (Exception ignored) { }
            }

            if (baseId != null) {
                try {
                    CommonPersonObject personObject = getCommonRepository("ec_mother_index").findByBaseEntityId(baseId);
                    if (personObject != null) {
                        CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
                        client.setColumnmaps(personObject.getColumnmaps());
                        commonPersonObjectClient = client;
                    }
                } catch (Exception e) {
                    Timber.w(e, "Fallback repository lookup failed for baseId=%s", baseId);
                }
            }

            if (commonPersonObjectClient == null) {
                Toasty.error(MotherDetail.this, "Mother record not found", Toast.LENGTH_LONG, true).show();
                finish();
                return;
            }
        }

        try {
            String motherBaseId = commonPersonObjectClient.getColumnmaps().get("base_entity_id");
            String hhId = commonPersonObjectClient.getColumnmaps().get("household_id");
            motherIndex = EcMotherIndexDao.getMotherByBaseEntityId(motherBaseId);
            if (motherIndex == null && hhId != null) {
                List<EcMotherIndexModel> mothersByHh = EcMotherIndexDao.getMothers(hhId);
                if (!mothersByHh.isEmpty()) {
                    motherIndex = mothersByHh.get(0);
                }
            }
        } catch (Exception e) {
            Timber.w(e, "Unable to load mother index record via EcMotherIndexDao");
        }

        try {
            family = HouseholdDao.getHousehold(commonPersonObjectClient.getColumnmaps().get("household_id"));
        } catch (Exception e) {
            Timber.e(e);
        }
        if (family == null) {
            Toasty.warning(MotherDetail.this, "Household record not found", Toast.LENGTH_LONG, true).show();
        }

        motherName.setText(commonPersonObjectClient.getColumnmaps().get("caregiver_name"));
        String birthdate = commonPersonObjectClient.getColumnmaps().get("caregiver_birth_date");
        String age = getAge(birthdate);
        txtAge.setText(age);

        // Prefill household_id and mother_facility in the header
        try {
            if (householdIdView != null) {
                String hhId = commonPersonObjectClient.getColumnmaps().get("household_id");
                if (hhId != null && !hhId.isEmpty()) {
                    householdIdView.setText("ID: " + hhId);
                }
            }
        } catch (Exception ignored) { }

        try {
            if (motherFacilityView != null) {
                // Try facility on mother first, then fall back to household facility
                String facility = commonPersonObjectClient.getColumnmaps().get("mother_facility");
                if ((facility == null || facility.isEmpty()) && family != null) {
                    facility = family.getFacility();
                }
                if (facility != null && !facility.isEmpty()) {
                    motherFacilityView.setText(facility);
                }
            }
        } catch (Exception ignored) { }

        // Prefill mother_last_visit and mother_next_appointment from the latest ANC record
        try {
            if (motherLastVisitView != null || motherNextAppointmentView != null) {
                String baseId = commonPersonObjectClient.getColumnmaps().get("base_entity_id");
                MotherAncModel latestAnc = baseId != null ? MotherAncDao.getLatestByBaseEntityId(baseId) : null;
                if (latestAnc != null) {
                    String ancVisitDate = latestAnc.getLast_interacted_with();
                    if ((ancVisitDate == null || ancVisitDate.isEmpty())) {
                        ancVisitDate = latestAnc.getDate_1st_visit();
                    }
                    if (motherLastVisitView != null && ancVisitDate != null && !ancVisitDate.isEmpty()) {
                        String formattedDate = ancVisitDate;
                        try {
                            long ts = Long.parseLong(ancVisitDate);
                            formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(ts));
                        } catch (Exception ignored) { }
                        motherLastVisitView.setText("Last ANC: " + formattedDate);
                        motherLastVisitView.setVisibility(View.VISIBLE);
                    } else if (motherLastVisitView != null) {
                        motherLastVisitView.setVisibility(View.GONE);
                    }
                    String edd = latestAnc.getEdd_date();
                    if (motherNextAppointmentView != null && edd != null && !edd.isEmpty()) {
                        motherNextAppointmentView.setText("EDD: " + edd);
                        motherNextAppointmentView.setVisibility(View.VISIBLE);
                    } else if (motherNextAppointmentView != null) {
                        motherNextAppointmentView.setVisibility(View.GONE);
                    }
                }
                if (latestAnc == null) {
                    if (motherLastVisitView != null) motherLastVisitView.setVisibility(View.GONE);
                    if (motherNextAppointmentView != null) motherNextAppointmentView.setVisibility(View.GONE);
                }
            }
        } catch (Exception ignored) { }

        oMapper = new ObjectMapper();

        fab = binding.fabx;
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        setupViewPager(false);
        updateChildTabTitle();
        updateAncTabTitle();
        updateLongitudinalTabTitle();
        updatePostnatalTabTitle();
        setupFabVisibility();
        updateCaregiverHivStatusAndVisibility();

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

    private void updateCaregiverHivStatusAndVisibility() {
        try {
            String caregiverStatus = null;

            if (motherIndex != null && motherIndex.getCaregiver_hiv_status() != null) {
                caregiverStatus = motherIndex.getCaregiver_hiv_status();
            }
            if ((caregiverStatus == null || caregiverStatus.isEmpty()) && commonPersonObjectClient != null && commonPersonObjectClient.getColumnmaps() != null) {
                caregiverStatus = commonPersonObjectClient.getColumnmaps().get("caregiver_hiv_status");
            }
            if ((caregiverStatus == null || caregiverStatus.isEmpty()) && family != null) {
                caregiverStatus = family.getCaregiver_hiv_status();
            }

            caregiverHivPositive = caregiverStatus != null &&
                    (caregiverStatus.equalsIgnoreCase("positive") || caregiverStatus.equalsIgnoreCase("HIV+"));

            View pmtctBtn = binding != null ? binding.pmtctProf : null;
            if (pmtctBtn != null) {
                pmtctBtn.setVisibility(caregiverHivPositive ? View.VISIBLE : View.GONE);
            }
        } catch (Exception ignored) {
            caregiverHivPositive = false;
        }
    }

    private void updateChildTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.child_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("CHILDREN");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);


        String children = IndexPersonDao.countChildren(commonPersonObjectClient.getColumnmaps().get("household_id"));

        childTabCount.setText(children);

        mTabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }

    private void updateAncTabTitle() {
        try {
            ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
            TextView title = layout.findViewById(R.id.visits_title);
            TextView countView = layout.findViewById(R.id.visits_count);
            title.setText("ANC");

            int count = 0;
            try {
                String householdId = commonPersonObjectClient.getColumnmaps().get("household_id");
                count = MotherAncDao.listByHouseholdId(householdId).size();
            } catch (Exception ignored) { }
            countView.setText(String.valueOf(count));

            if (mTabLayout.getTabCount() > 2 && mTabLayout.getTabAt(2) != null) {
                mTabLayout.getTabAt(2).setCustomView(layout);
            }
        } catch (Exception ignored) { }
    }

    private void updateLongitudinalTabTitle() {
        try {
            ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
            TextView title = layout.findViewById(R.id.visits_title);
            TextView countView = layout.findViewById(R.id.visits_count);
            title.setText("LONGITUDINAL");

            int count = 0;
            try {
                String householdId = commonPersonObjectClient.getColumnmaps().get("household_id");
                count = MotherLongitudinalFollowUpDao.listByHouseholdId(householdId).size();
            } catch (Exception ignored) { }
            countView.setText(String.valueOf(count));

            if (mTabLayout.getTabCount() > 3 && mTabLayout.getTabAt(3) != null) {
                mTabLayout.getTabAt(3).setCustomView(layout);
            }
        } catch (Exception ignored) { }
    }

    private void updatePostnatalTabTitle() {
        try {
            ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
            TextView title = layout.findViewById(R.id.visits_title);
            TextView countView = layout.findViewById(R.id.visits_count);
            title.setText("POSTNATAL");

            int count = 0;
            try {
                String householdId = commonPersonObjectClient.getColumnmaps().get("household_id");
                count = MotherPostnatalCareDao.listByHouseholdId(householdId).size();
            } catch (Exception ignored) { }
            countView.setText(String.valueOf(count));

            if (mTabLayout.getTabCount() > 4 && mTabLayout.getTabAt(4) != null) {
                mTabLayout.getTabAt(4).setCustomView(layout);
            }
        } catch (Exception ignored) { }
    }


    private void setupViewPager(boolean forceRefresh){
        // Rebuild adapter when forced or when not yet initialized
        if (!forceRefresh && mViewPager.getAdapter() != null) return;

        if (tabMediator != null) { try { tabMediator.detach(); } catch (Exception ignored) {} tabMediator = null; }
        mViewPager.setAdapter(null);

        java.util.List<androidx.fragment.app.Fragment> fragments = new java.util.ArrayList<>();
        fragments.add(new MotherOverviewFragment());
        fragments.add(new MotherChildrenFragment());
        fragments.add(new MotherAncFragment());
        fragments.add(new MotherLongitudinalFragment());
        fragments.add(new MotherPostnatalFragment());

        com.bluecodeltd.ecap.chw.adapter.ViewPager2Adapter adapter = new com.bluecodeltd.ecap.chw.adapter.ViewPager2Adapter(this, fragments);
        mViewPager.setAdapter(adapter);
        tabMediator = new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            if (position == 0) tab.setText("Overview");
            else if (position == 1) tab.setText("Children");
            else if (position == 2) tab.setText("ANC");
            else if (position == 3) tab.setText("Longitudinal");
            else if (position == 4) tab.setText("Postnatal");
        });
        tabMediator.attach();

    }


    private String getAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate today = LocalDate.now();

        try {
            if (birthdate == null || birthdate.trim().isEmpty()) {
                return "";
            }
            LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
            Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);

            if(periodBetweenDateOfBirthAndNow.getYears() > 0) {
                return periodBetweenDateOfBirthAndNow.getYears() + " Years";
            } else if (periodBetweenDateOfBirthAndNow.getMonths() > 0) {
                return periodBetweenDateOfBirthAndNow.getMonths() + " Months ";
            } else {
                return periodBetweenDateOfBirthAndNow.getDays() + " Days ";
            }
        } catch (DateTimeParseException e) {
            return "Invalid date format";
        }
    }

    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.mother_form:

                try {
                    openFormUsingFormUtils(MotherDetail.this,"mother_index_edit");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.child_form:

                try {
                    openFormUsingFormUtils(MotherDetail.this,"child");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.mother_anc:

                try {
                    openFormUsingFormUtils(MotherDetail.this,"mother_anc");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.mother_delivery:

                try {
                    openFormUsingFormUtils(MotherDetail.this,"mother_delivery");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.mother_longitudinal_follow_up:

                try {
                    openFormUsingFormUtils(MotherDetail.this,"mother_longitudinal_follow_up");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.mother_outcome:

                try {
                    openFormUsingFormUtils(MotherDetail.this,"mother_outcome");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.mother_postnatal_care:

                try {
                    openFormUsingFormUtils(MotherDetail.this,"mother_postnatal_care");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.fabx:

                animateFAB();

                break;

            case R.id.hh_prof:
//
//                if(childTabCount == null || childTabCount.getText().toString().equals("0")){
//
//                    Toasty.warning(MotherDetail.this, "Household should have at least 1 Child", Toast.LENGTH_LONG, true).show();

//                } else {

                    Intent householdIntent = new Intent(this, HouseholdDetails.class);
                    householdIntent.putExtra("householdId",  commonPersonObjectClient.getColumnmaps().get("household_id"));
                    startActivity(householdIntent);


//                }

                break;

            case R.id.pmtct_prof:

                try {
                    String baseId = commonPersonObjectClient.getColumnmaps().get("household_id");
                    PtctMotherModel pmtctMother = getPmtctMotherByBaseEntity(baseId);
                    if (pmtctMother == null || pmtctMother.getHousehold_id() == null || pmtctMother.getHousehold_id().isEmpty()) {
                        Toasty.warning(MotherDetail.this, "Mother not enrolled in PMTCT", Toast.LENGTH_LONG, true).show();
                    } else {
                        Intent intent = new Intent(this, MotherPmtctProfileActivity.class);
                        intent.putExtra("client_id", pmtctMother.getHousehold_id());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                    Toasty.error(MotherDetail.this, "Unable to open PMTCT profile", Toast.LENGTH_LONG, true).show();
                }

                break;

        }
    }

    private PtctMotherModel getPmtctMotherByBaseEntity(String baseEntityId) {
        try {
            return PMTCTMotherDao.getPMCTMotherByBaseEntityId(baseEntityId);
        } catch (Exception e) {
            Timber.e(e);
            return null;
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

            case "mother_index_edit":

                householdMapper = new ObjectMapper();

                formToBeOpened.put("entity_id", this.commonPersonObjectClient.getColumnmaps().get("base_entity_id"));
                String titleName = motherIndex != null ? motherIndex.getCaregiver_name() : this.commonPersonObjectClient.getColumnmaps().get("caregiver_name");
                formToBeOpened.getJSONObject("step1").put("title", (titleName != null ? titleName : "") + " "  + txtAge.getText().toString());

                // Populate using the loaded mother record; fall back to the client column map to avoid NPE
                Map<String, String> motherMap = new HashMap<>();
                try {
                    Map<String, Object> mappedMother = motherIndex != null ? householdMapper.convertValue(motherIndex, Map.class) : null;
                    if (mappedMother != null) {
                        for (Map.Entry<String, Object> entry : mappedMother.entrySet()) {
                            if (entry.getKey() != null && entry.getValue() != null) {
                                motherMap.put(entry.getKey(), String.valueOf(entry.getValue()));
                            }
                        }
                    }
                } catch (Exception e) {
                    Timber.w(e, "Unable to map motherIndex for form population");
                }
                try {
                    Map<String, String> columns = this.commonPersonObjectClient != null ? this.commonPersonObjectClient.getColumnmaps() : null;
                    if (columns != null) {
                        motherMap.putAll(columns);
                    }
                } catch (Exception e) {
                    Timber.w(e, "Unable to merge columnmaps for form population");
                }

                CoreJsonFormUtils.populateJsonForm(formToBeOpened, motherMap);


                break;

            case "child":
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MotherDetail.this);
                Object obj = sp.getAll();
                CoreJsonFormUtils.populateJsonForm(formToBeOpened,oMapper.convertValue(obj, Map.class));
                formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", this.commonPersonObjectClient.getColumnmaps().get("household_id"));

                Number = new Random();
                Rnumber = Number.nextInt(900000000);
                String newEntityId =  Integer.toString(Rnumber);


                //******** POPULATE JSON FORM VCA UNIQUE ID ******//
                JSONObject stepOneUniqueId = getFieldJSONObject(fields(formToBeOpened, STEP1), "unique_id");

                if (stepOneUniqueId != null) {
                    stepOneUniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                    try {
                        stepOneUniqueId.put(JsonFormUtils.VALUE, newEntityId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(commonPersonObjectClient.getColumnmaps(), Map.class));

                break;

            case "mother_anc":

                // From the mother profile, open a fresh ANC form (do not edit latest record).
                // Only link using household_id (no base_entity_id / entity_id).
                try {
                    String householdId = this.commonPersonObjectClient.getColumnmaps().get("household_id");
                    try {
                        JSONArray flds = formToBeOpened.getJSONObject("step1").getJSONArray("fields");
                        for (int i = 0; i < flds.length(); i++) {
                            JSONObject f = flds.getJSONObject(i);
                            String key = f.optString("key");
                            if ("household_id".equals(key) && householdId != null) {
                                f.put("value", householdId);
                            }
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }

                break;

            case "mother_longitudinal_follow_up":

                // From the mother profile, open a fresh longitudinal form.
                // Only link using household_id (no base_entity_id / entity_id).
                try {
                    String householdId = this.commonPersonObjectClient.getColumnmaps().get("household_id");
                    try {
                        JSONArray flds = formToBeOpened.getJSONObject("step1").getJSONArray("fields");
                        for (int i = 0; i < flds.length(); i++) {
                            JSONObject f = flds.getJSONObject(i);
                            String key = f.optString("key");
                            if ("household_id".equals(key) && householdId != null) {
                                f.put("value", householdId);
                            }
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }

                break;

            case "mother_postnatal_care":

                // When adding from the mother profile, open a fresh Postnatal form.
                // Only link using household_id (no base_entity_id / entity_id).
                try {
                    String householdId = this.commonPersonObjectClient.getColumnmaps().get("household_id");
                    try {
                        JSONArray flds = formToBeOpened.getJSONObject("step1").getJSONArray("fields");
                        for (int i = 0; i < flds.length(); i++) {
                            JSONObject f = flds.getJSONObject(i);
                            String key = f.optString("key");
                            if ("household_id".equals(key) && householdId != null) {
                                f.put("value", householdId);
                            }
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }

                break;

            case "mother_delivery":

                try {
                    String baseId = this.commonPersonObjectClient.getColumnmaps().get("base_entity_id");
                    MotherDeliveryModel delivery = MotherDeliveryDao.getLatestByBaseEntityId(baseId);
                    formToBeOpened.put("entity_id", baseId);
                    if (delivery != null) {
                        // Prefill using the latest delivery record (includes household_id)
                        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(delivery, Map.class));
                    } else {
                        // Fallback to mother details so household_id and basic info appear
                        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(commonPersonObjectClient.getColumnmaps(), Map.class));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }

                break;

            case "mother_outcome":

                try {
                    String baseId = this.commonPersonObjectClient.getColumnmaps().get("base_entity_id");
                    MotherOutcomeModel outcome = MotherOutcomeDao.getLatestByBaseEntityId(baseId);
                    formToBeOpened.put("entity_id", baseId);
                    if (outcome != null) {
                        // Prefill using the latest outcome record (includes household_id)
                        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(outcome, Map.class));
                    } else {
                        // Fallback to mother details so household_id and basic info appear
                        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(commonPersonObjectClient.getColumnmaps(), Map.class));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }

                break;
        }
        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName(getString(org.smartregister.chw.core.R.string.child));
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


            if(!jsonFormObject.optString("entity_id").isEmpty()){
                is_edit_mode = true;
            }

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                Runnable postSaveAction = () -> {
                    Toasty.success(MotherDetail.this, "Form Saved", Toast.LENGTH_LONG, true).show();
                    refreshActivity();

                };

                boolean refreshScheduled = saveRegistration(childIndexEventClient, is_edit_mode, postSaveAction);

                getUniqueIdRepository().close(vca_id);

                if (!refreshScheduled) {
                    postSaveAction.run();
                }
                return;

            } catch (Exception e) {
                Timber.e(e);
            }

        }

        getData();
        setupViewPager(true);
        updateChildTabTitle();
        updateAncTabTitle();
        updateLongitudinalTabTitle();
        updatePostnatalTabTitle();
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
                case "Mother Register":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_MOTHER_INDEX);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;



                case "Child":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CLIENT_INDEX);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "ANC":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_MOTHER_ANC);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Longitudinal Follow Up Record":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_mother_longitudinal_follow_up");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Post Natal Care-Mother":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_mother_postnatal_care");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Labour and Delivery":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_mother_delivery");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Final Outcome of Mother":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_mother_outcome");
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

    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode, Runnable onComplete) {

        Runnable runnable = () -> {

            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();

            try {
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
            } finally {
                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }
            }

        };

        try {
            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(runnable);
            return true;
        } catch (Exception exception) {
            Timber.e(exception);
            if (onComplete != null) {
                runOnUiThread(onComplete);
            }
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

    private void refreshActivity() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        Intent intent = new Intent(getIntent());
        // Force re-query from repository instead of reusing stale serialized mother payload
        intent.putExtra("refresh", "true");
        String baseId = null;
        if (commonPersonObjectClient != null && commonPersonObjectClient.getColumnmaps() != null) {
            try {
                baseId = commonPersonObjectClient.getColumnmaps().get("base_entity_id");
                if (baseId != null) {
                    intent.putExtra("base_entity_id", baseId);
                }
            } catch (Exception ignored) { }
        }
        if (baseId != null) {
            intent.removeExtra("mother");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    public void animateFAB(){

        if (isFabOpen){

            closeFab();

        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);
            mLayout.setVisibility(View.VISIBLE);
            cLayout.setVisibility(View.VISIBLE);

            if (!caregiverHivPositive) {
                if (motherAncLayout != null) motherAncLayout.setVisibility(View.VISIBLE);
                if (motherDeliveryLayout != null) motherDeliveryLayout.setVisibility(View.VISIBLE);
                if (motherLongitudinalLayout != null) motherLongitudinalLayout.setVisibility(View.VISIBLE);
                if (motherOutcomeLayout != null) motherOutcomeLayout.setVisibility(View.VISIBLE);
                if (motherPostnatalLayout != null) motherPostnatalLayout.setVisibility(View.VISIBLE);
                if (childFinalOutcomeLayout != null) childFinalOutcomeLayout.setVisibility(View.VISIBLE);
                if (childLongitudinalLayout != null) childLongitudinalLayout.setVisibility(View.VISIBLE);
                if (childPostnatalLayout != null) childPostnatalLayout.setVisibility(View.VISIBLE);
            } else {
                if (motherAncLayout != null) motherAncLayout.setVisibility(View.GONE);
                if (motherDeliveryLayout != null) motherDeliveryLayout.setVisibility(View.GONE);
                if (motherLongitudinalLayout != null) motherLongitudinalLayout.setVisibility(View.GONE);
                if (motherOutcomeLayout != null) motherOutcomeLayout.setVisibility(View.GONE);
                if (motherPostnatalLayout != null) motherPostnatalLayout.setVisibility(View.GONE);
                if (childFinalOutcomeLayout != null) childFinalOutcomeLayout.setVisibility(View.GONE);
                if (childLongitudinalLayout != null) childLongitudinalLayout.setVisibility(View.GONE);
                if (childPostnatalLayout != null) childPostnatalLayout.setVisibility(View.GONE);
            }

        }
    }

    private void setupFabVisibility() {
        // Show the floating menu only on the Overview tab, and force-hide it elsewhere
        updateFabVisibilityForPosition(safeViewPagerPosition());
        try {
            TabLayout.Tab selectedTab = mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition());
            if (selectedTab != null) {
                updateFabVisibilityForPosition(selectedTab.getPosition());
            }
        } catch (Exception ignored) { }

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Hide the menu and close it when leaving Overview
                if (position != 0 && isFabOpen) {
                    closeFab();
                }
                updateFabVisibilityForPosition(position);
            }
        });

        // Also react to tab selections as a defensive guard
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == null) return;
                if (tab.getPosition() != 0 && isFabOpen) {
                    closeFab();
                }
                updateFabVisibilityForPosition(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void updateFabVisibilityForPosition(int position) {
        try {
            if (position == 0) {
                fab.show();
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.hide();
                fab.setVisibility(View.GONE);
            }
        } catch (Exception ignored) { }
    }

    private int safeViewPagerPosition() {
        try {
            return mViewPager.getCurrentItem();
        } catch (Exception e) {
            return 0;
        }
    }

    public void closeFab(){
        fab.startAnimation(rotate_backward);
        isFabOpen = false;
        cLayout.setVisibility(View.GONE);
        mLayout.setVisibility(View.GONE);
        if (motherAncLayout != null) motherAncLayout.setVisibility(View.GONE);
        if (motherDeliveryLayout != null) motherDeliveryLayout.setVisibility(View.GONE);
        if (motherLongitudinalLayout != null) motherLongitudinalLayout.setVisibility(View.GONE);
        if (motherOutcomeLayout != null) motherOutcomeLayout.setVisibility(View.GONE);
        if (motherPostnatalLayout != null) motherPostnatalLayout.setVisibility(View.GONE);
        if (childFinalOutcomeLayout != null) childFinalOutcomeLayout.setVisibility(View.GONE);
        if (childLongitudinalLayout != null) childLongitudinalLayout.setVisibility(View.GONE);
        if (childPostnatalLayout != null) childPostnatalLayout.setVisibility(View.GONE);
    }
}
