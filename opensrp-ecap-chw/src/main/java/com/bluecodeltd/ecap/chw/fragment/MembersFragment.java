package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CreateMemberClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
//    MembersListAdapter memberListAdapter;
//    ArrayList<MembersModel> listMembers;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MembersFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        username = getArguments().getString("username");
        password = getArguments().getString("password");

//        listMembers = new ArrayList<>();
//        members_recyclerview = rootView.findViewById(R.id.viewGroups);
//
//        listMembers = returnUsers();
//        memberListAdapter = new MembersListAdapter(getContext(), listMembers);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//        members_recyclerview.setLayoutManager(layoutManager);
//        members_recyclerview.setAdapter(memberListAdapter);
//
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateMemberClass.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });

        return rootView;
    }


//    public void fetchMembersUsers(){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//        String group_id = preferences.getString("group_id", "");
//        SharedPreferences sharedPref = null;
//        if(getActivity() != null) {
//            sharedPref = getActivity().getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
//        }
//        String token = sharedPref != null ? sharedPref.getString("access_token", null) : null;
//
//        refreshToken();
//        //Show Progress Bar
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.DIRECTUS_API)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(token)).build())
//                .build();
//
//        UserApi api = retrofit.create(UserApi.class);
//
//        Call<ResponseBody> call = api.getAllMembersGroups();
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                if (response != null && response.isSuccessful() && response.body() != null) {
//                    DatabaseHelper db = new DatabaseHelper(getContext());
//                    db.emptyTableMembers();
//                    try {
//                        String json = response.body().string();
//                        JSONObject jsonObject = new JSONObject(json);
//                        JSONArray data = jsonObject.getJSONArray("data");
//                        for (int i = 0; i < data.length(); i++) {
//                            JSONObject user = data.getJSONObject(i);
//
//
//                            String firstname = user.getString("first_name");
//                            String lastname = user.getString("last_name");
//                            String userId = user.getString("id");
//                            String email = user.getString("email");
//                            String admission_date = user.getString("admission_date");
//                            String ecap_hh_ID = user.getString("ecap_hh_ID");
//                            String gender = user.getString("gender");
//                            String phone_number = user.getString("phone_number");
//                            String single_female_caregiver = user.getString("single_female_caregiver");
//                            String groupId = user.getString("group_id");
//                            String role = user.getString("role");
//                            String NRC = user.getString("NRC");
//                            String next_of_kin = user.getString("next_of_kin");
//                            String next_of_kin_phone = user.getString("next_of_kin_phone");
//                            String facility_id = user.getString("facility_id");
//                            String user_id = user.getString("user_id");
//
//                            db.addMemberInfo(firstname,lastname, userId,email,admission_date,ecap_hh_ID,gender,phone_number,single_female_caregiver,groupId,role, NRC,next_of_kin,next_of_kin_phone, facility_id,user_id);
//                            listMembers = returnUsers();
//                            memberListAdapter = new MembersListAdapter(getContext(), listMembers);
//                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//                            members_recyclerview.setLayoutManager(layoutManager);
//                            members_recyclerview.setAdapter(memberListAdapter);
//                        }
//
//                    } catch (IOException | JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("TAG", "Error: " + t.getMessage());
//            }
//        });
//    }
//    public ArrayList<MembersModel> returnUsers() {
//        ArrayList<MembersModel> memberListModels = new ArrayList<>();
//        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
//        Cursor cursor = dbHelper.selectMembers();
//        if (cursor.moveToFirst()) {
//            do {
//                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("first_name")) + " " + cursor.getString(cursor.getColumnIndex("last_name"));
//                @SuppressLint("Range") String user_id = cursor.getString(cursor.getColumnIndex("id"));
//                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
//                @SuppressLint("Range") String admissionDate = cursor.getString(cursor.getColumnIndex("admission_date"));
//                @SuppressLint("Range") String ecapHHID = cursor.getString(cursor.getColumnIndex("ecap_hh_ID"));
//                @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex("gender"));
//                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex("phone_number"));
//                @SuppressLint("Range") String singleFemaleCaregiver = cursor.getString(cursor.getColumnIndex("single_female_caregiver"));
//                @SuppressLint("Range") String groupID = cursor.getString(cursor.getColumnIndex("group_id"));
//                @SuppressLint("Range") boolean deleted = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("deleted")));
//                @SuppressLint("Range") String role = cursor.getString(cursor.getColumnIndex("role"));
//                @SuppressLint("Range") String NRC = cursor.getString(cursor.getColumnIndex("nrc"));
//                @SuppressLint("Range") String next_of_kin = cursor.getString(cursor.getColumnIndex("next_of_kin"));
//                @SuppressLint("Range") String next_of_kin_phone = cursor.getString(cursor.getColumnIndex("next_of_kin_phone"));
//                @SuppressLint("Range") String facility_id = cursor.getString(cursor.getColumnIndex("facility_id"));
//                @SuppressLint("Range") String userId = cursor.getString(cursor.getColumnIndex("user_id"));
//
//                String alias = user_id.substring(0,4);
//                memberListModels.add(new MembersModel(name, user_id, deleted, email, admissionDate, ecapHHID, gender, phoneNumber, singleFemaleCaregiver, groupID, role, NRC, next_of_kin, next_of_kin_phone, facility_id, userId));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return memberListModels;
//    }
//    public void refreshToken() {
//        WorkManager workManager = WorkManager.getInstance(getContext());
//        OneTimeWorkRequest oneTimeRequest = new OneTimeWorkRequest.Builder(RefreshWorker.class)
//                .build();
//        workManager.enqueue(oneTimeRequest);
//    }
}