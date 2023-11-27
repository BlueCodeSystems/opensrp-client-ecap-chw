package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.WeGroupMemberProfileActivity;
import com.bluecodeltd.ecap.chw.activity.WeGroupProfileActivity;
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberMeetingRegisterAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberMeetingRegisterDao;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberMeetingRegisterModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupMemberMeetingRegister#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupMemberMeetingRegister extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView view_recyclerview;
    WeGroupMemberMeetingRegisterAdapter register;
    ArrayList<WeGroupMemberMeetingRegisterModel> listRegister;

    public WeGroupMemberMeetingRegister() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupMemberMeetingRegister.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupMemberMeetingRegister newInstance(String param1, String param2) {
        WeGroupMemberMeetingRegister fragment = new WeGroupMemberMeetingRegister();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_we_group_member_meeting_register, container, false);

        HashMap<String, WeGroupModel> mymap = ((WeGroupProfileActivity) requireActivity()).getGroupData();
        WeGroupModel groupModel = mymap.get("groupID");

//        HashMap<String, MembersModel> mymap = ((WeGroupMemberProfileActivity) requireActivity()).getData();
//        MembersModel membersModel = mymap.get("uniqueID");

        listRegister = new ArrayList<>();
        view_recyclerview = view.findViewById(R.id.viewRegister);

        TextView infoTxt = view.findViewById(R.id.infoTxt);

        listRegister.addAll(WeGroupMemberMeetingRegisterDao.getWeGroupMemberMeetingByGroupId(groupModel.getGroup_id()));


        if (listRegister == null || listRegister.isEmpty()) {
            view_recyclerview.setVisibility(View.GONE);
            infoTxt.setVisibility(View.VISIBLE);
            infoTxt.setText("No meeting was conducted");
        } else {
            view_recyclerview.setVisibility(View.VISIBLE);
            infoTxt.setVisibility(View.GONE);
            register = new  WeGroupMemberMeetingRegisterAdapter(listRegister,getContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            view_recyclerview.setLayoutManager(layoutManager);
            view_recyclerview.setAdapter(register);
        };



        return view;
    }
}