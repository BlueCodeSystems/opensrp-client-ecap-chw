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
import com.bluecodeltd.ecap.chw.activity.WeGroupProfileActivity;
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberIgaAdapter;
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberLoansAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberIgaDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberLoanDao;
import com.bluecodeltd.ecap.chw.model.MemberIGAModel;
import com.bluecodeltd.ecap.chw.model.MemberLoanModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupIgaServiceGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupIgaServiceGroupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView iga_recyclerview;
    WeGroupMemberIgaAdapter igaListAdapter;
    ArrayList<MemberIGAModel> igaLoans;

    public WeGroupIgaServiceGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupIgaServiceGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupIgaServiceGroupFragment newInstance(String param1, String param2) {
        WeGroupIgaServiceGroupFragment fragment = new WeGroupIgaServiceGroupFragment();
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_we_group_iga_service_group, container, false);
        HashMap<String, WeGroupModel> mymap = ((WeGroupProfileActivity) requireActivity()).getGroupData();
        WeGroupModel groupModel = mymap.get("groupID");

        igaLoans = new ArrayList<>();
        iga_recyclerview = view.findViewById(R.id.viewGroupIgas);

        TextView infoTxt = view.findViewById(R.id.infoTxt);

        igaLoans.addAll(WeGroupMemberIgaDao.getWeGroupMembersIgaByGroupId(groupModel.getGroup_id()));


        if (igaLoans == null || igaLoans.isEmpty()) {
            iga_recyclerview.setVisibility(View.GONE);

            infoTxt.setVisibility(View.VISIBLE);
            infoTxt.setText("No IGA loans have been added.");
        } else {
            iga_recyclerview.setVisibility(View.VISIBLE);
            infoTxt.setVisibility(View.GONE);
            igaListAdapter = new WeGroupMemberIgaAdapter(getContext(), igaLoans);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            iga_recyclerview.setLayoutManager(layoutManager);
            iga_recyclerview.setAdapter(igaListAdapter);
        }



        return view;
    }
}