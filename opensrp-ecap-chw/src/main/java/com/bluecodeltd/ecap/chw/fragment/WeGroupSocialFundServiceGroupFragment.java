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
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberLoansAdapter;
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberSocialAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberLoanDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSocialFundDao;
import com.bluecodeltd.ecap.chw.model.MemberLoanModel;
import com.bluecodeltd.ecap.chw.model.MemberSocialFundModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupSocialFundServiceGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupSocialFundServiceGroupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView social_fund_recyclerview;
    WeGroupMemberSocialAdapter socialFundListAdapter;
    ArrayList<MemberSocialFundModel> listSocialFund;

    public WeGroupSocialFundServiceGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupSocialFundServiceGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupSocialFundServiceGroupFragment newInstance(String param1, String param2) {
        WeGroupSocialFundServiceGroupFragment fragment = new WeGroupSocialFundServiceGroupFragment();
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
        View view = inflater.inflate(R.layout.fragment_we_group_social_fund_service_group, container, false);
        HashMap<String, WeGroupModel> mymap = ((WeGroupProfileActivity) requireActivity()).getGroupData();
        WeGroupModel groupModel = mymap.get("groupID");

        listSocialFund = new ArrayList<>();
        social_fund_recyclerview = view.findViewById(R.id.viewGroupSocialFund);

        TextView infoTxt = view.findViewById(R.id.infoTxt);

        listSocialFund.addAll(WeGroupMemberSocialFundDao.getWeGroupMembersSocialFundByGroupId(groupModel.getGroup_id()));


        if (listSocialFund == null || listSocialFund.isEmpty()) {
            social_fund_recyclerview.setVisibility(View.GONE);
            infoTxt.setVisibility(View.VISIBLE);
            infoTxt.setText("No social fund entries have been added.");
        } else {
            social_fund_recyclerview.setVisibility(View.VISIBLE);
            infoTxt.setVisibility(View.GONE);
            socialFundListAdapter = new WeGroupMemberSocialAdapter(getContext(), listSocialFund);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            social_fund_recyclerview.setLayoutManager(layoutManager);
            social_fund_recyclerview.setAdapter(socialFundListAdapter);
        };



        return view;
    }
}