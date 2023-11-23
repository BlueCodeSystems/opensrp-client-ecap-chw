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
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberLoansAdapter;
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberLoansForMemberAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberLoanDao;
import com.bluecodeltd.ecap.chw.model.MemberLoanModel;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupLoanServiceMemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupLoanServiceMemberFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeGroupLoanServiceMemberFragment() {
        // Required empty public constructor
    }
    RecyclerView loans_recyclerview;
    WeGroupMemberLoansForMemberAdapter loansListAdapter;
    ArrayList<MemberLoanModel> listLoans;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupLoanServiceMemberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupLoanServiceMemberFragment newInstance(String param1, String param2) {
        WeGroupLoanServiceMemberFragment fragment = new WeGroupLoanServiceMemberFragment();
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
        View view = inflater.inflate(R.layout.fragment_we_group_loan_service_member, container, false);

//        HashMap<String, WeGroupModel> mymap = ((WeGroupProfileActivity) requireActivity()).getGroupData();
//        WeGroupModel groupModel = mymap.get("groupID");

        HashMap<String, MembersModel> mymap = ((WeGroupMemberProfileActivity) requireActivity()).getData();
        MembersModel membersModel = mymap.get("uniqueID");



        listLoans = new ArrayList<>();
        loans_recyclerview = view.findViewById(R.id.viewGroupLoans);
        TextView infoTxt = view.findViewById(R.id.infoTxt);

        listLoans.addAll(WeGroupMemberLoanDao.getWeGroupMembersLoansByUniqueId(membersModel.getUnique_id()));


        if (listLoans == null || listLoans.isEmpty()) {
            loans_recyclerview.setVisibility(View.GONE);
            infoTxt.setVisibility(View.VISIBLE);
            infoTxt.setText("No loan entries have been added.");
        } else {
            loans_recyclerview.setVisibility(View.VISIBLE);
            infoTxt.setVisibility(View.GONE);
            loansListAdapter = new WeGroupMemberLoansForMemberAdapter(getContext(), listLoans);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            loans_recyclerview.setLayoutManager(layoutManager);
            loans_recyclerview.setAdapter(loansListAdapter);
        }



        return view;
    }
}