package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.WeGroupMemberProfileActivity;
import com.bluecodeltd.ecap.chw.activity.WeGroupProfileActivity;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberFineDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberIgaDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberLoanDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSavingDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSocialFundDao;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupServiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeGroupServiceFragment() {
        // Required empty public constructor
    }
    TextView txtGroupSaving,txtGroupLoan,txtGroupFine,txtGroupSocial,txtGroupIga;
    RecyclerView service;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupServiceFragment newInstance(String param1, String param2) {
        WeGroupServiceFragment fragment = new WeGroupServiceFragment();
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
        View view = inflater.inflate(R.layout.fragment_we_group_service, container, false);
        service = view.findViewById(R.id.service);

        txtGroupSaving = view.findViewById(R.id.txtGroupSaving);
        txtGroupLoan = view.findViewById(R.id.txtGroupLoan);
        txtGroupFine = view.findViewById(R.id.txtGroupFine);
        txtGroupSocial = view.findViewById(R.id.txtGroupSocial);
        txtGroupIga = view.findViewById(R.id.txtGroupIga);

        HashMap<String, WeGroupModel> mymap = ((WeGroupProfileActivity) requireActivity()).getGroupData();
        WeGroupModel groupModel = mymap.get("groupID");

        try {

            String totalGroupSavingAmount = String.valueOf(WeGroupMemberSavingDao.getTotalGroupAmount(groupModel.getGroup_id()));

            if (totalGroupSavingAmount != null) {
                txtGroupSaving.setText(totalGroupSavingAmount);
            } else {
                txtGroupSaving.setText("0");
            }


            String totalGroupAmountLoan = String.valueOf(WeGroupMemberLoanDao.getTotalGroupAmount(groupModel.getGroup_id()));

            if (totalGroupAmountLoan != null) {
                txtGroupLoan.setText(totalGroupAmountLoan);
            } else {
                txtGroupLoan.setText("0");
            }


            String totalGroupAmountFine = String.valueOf(WeGroupMemberFineDao.getTotalGroupAmount(groupModel.getGroup_id()));

            if (totalGroupAmountFine != null) {
                txtGroupFine.setText(totalGroupAmountFine);
            } else {
                txtGroupFine.setText("0");
            }


            String totalAmountGroupSocial = String.valueOf(WeGroupMemberSocialFundDao.getTotalGroupAmount(groupModel.getGroup_id()));

            if (totalAmountGroupSocial != null) {
                txtGroupSocial.setText(totalAmountGroupSocial);
            } else {
                txtGroupSocial.setText("0");
            }


            String totalGroupAmountIga = String.valueOf(WeGroupMemberIgaDao.getTotalGroupAmount(groupModel.getGroup_id()));

            if (totalGroupAmountIga != null) {
                txtGroupIga.setText(totalGroupAmountIga);
            } else {
                txtGroupIga.setText("0");
            }




        } catch (Exception e) {

            e.printStackTrace();
        }



        return view;
    }
}