package com.bluecodeltd.ecap.chw.fragment;

import static android.app.Activity.RESULT_OK;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

//import com.bluecode.weledger.R;
//import com.bluecode.weledger.activity.FacilitatorNewMemberActivity;
//import com.bluecode.weledger.activity.RefreshWorker;
//import com.bluecode.weledger.adapters.MembersListAdapter;
//import com.bluecode.weledger.auth.AuthInterceptor;
//import com.bluecode.weledger.config.Constants;
//import com.bluecode.weledger.databases.DatabaseHelper;
//import com.bluecode.weledger.interfaces.UserApi;
//import com.bluecode.weledger.models.MembersModel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CreateMemberClass;
import com.bluecodeltd.ecap.chw.activity.DashboardActivity;
import com.bluecodeltd.ecap.chw.adapter.MembersListAdapter;
import com.bluecodeltd.ecap.chw.api.MemberApi;
import com.bluecodeltd.ecap.chw.api.VolleyCallback;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.WeGroupDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.interceptor.AuthInterceptor;
import com.bluecodeltd.ecap.chw.model.Credentials;
import com.bluecodeltd.ecap.chw.model.MemberListModel;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.io.IOException;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MembersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FloatingActionButton addMember;

    RecyclerView members_recyclerview;
    Toolbar toolbar;
    String username, password;
    MembersListAdapter memberListAdapter;
    ArrayList<MembersModel> listMembers;

    private Handler handler = new Handler(Looper.getMainLooper());

    private String mParam1;
    private String mParam2;


    public static MembersFragment newInstance(String param1, String param2) {
        MembersFragment fragment = new MembersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        // Fetch member users data
//        fetchMembersUsers();
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);

        addMember = rootView.findViewById(R.id.fab);
            try{
                username = getArguments().getString("username");
                password = getArguments().getString("password");
            } catch (Exception e){

            }

        getToken(username, password, BuildConfig.OAUTH_CLIENT_ID, BuildConfig.OAUTH_CLIENT_SECRET, result -> {
            Log.d("Access Token", result);

            String token = result;
        getCreds(token);
    });




        listMembers = new ArrayList<>();
        members_recyclerview = rootView.findViewById(R.id.viewGroups);

        listMembers.addAll(WeGroupMembersDao.getWeGroupMembers());
        memberListAdapter = new MembersListAdapter(getContext(), listMembers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        members_recyclerview.setLayoutManager(layoutManager);
        members_recyclerview.setAdapter(memberListAdapter);

        addMember.setOnClickListener(v -> {
            FormUtils formUtils = null;
            try {
                formUtils = new FormUtils(getContext());
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

            JSONObject groupId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
            if (groupId  != null) {
                groupId.remove(JsonFormUtils.VALUE);
                try {
                    groupId.put(JsonFormUtils.VALUE, generateGroupId(9));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            startFormActivity(indexRegisterForm);


        });

        return rootView;
    }


    private String getFormattedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return today.format(formatter);
    }

    public static String generateGroupId(int length) {
        String numbers = "23456789"; // Excludes easily confused characters
        StringBuilder stringBuilder = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(numbers.length());
            char randomChar = numbers.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
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
        Intent intent = new Intent(requireContext(), org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);



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

                saveRegistration(childIndexEventClient, is_edit_mode);

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
                        String stringUsername = email.optString("value");

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



                        postMember(stringDateCreated, "324689599752", stringUniqueId, stringFirstName, stringLastName, stringGender,
                                stringNrc, stringEmail, stringUsername, stringPassword, stringAdmissionDate, stringEcapHhID,
                                stringRole, stringPhoneNumber, stringSingleFemaleCaregiver, stringNextOfKin, stringNextOfKinPhone, stringDeleteStatus);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listMembers.clear();
                                listMembers.addAll(WeGroupMembersDao.getWeGroupMembers());
                                memberListAdapter.notifyDataSetChanged();
                            }
                        });

                        break;

                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
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
        Volley.newRequestQueue(getContext()).add(stringRequest);
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
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
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

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                        if (response.isSuccessful()) {
                            Log.d("API Response", "Response received successfully");

                        } else {
                            Log.e("API Response", "Response error: " + response.errorBody());
                            Log.e("API Response", "Response code: " + response.code());
                            Log.e("API Response", "Response message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<MemberListModel> call, Throwable t) {
                        Log.e("postusererror", "perror : " + t.getMessage());
                    }
                });
            });
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

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