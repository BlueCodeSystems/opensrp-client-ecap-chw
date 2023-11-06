package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.WeGroupMemberProfileActivity;
import com.bluecodeltd.ecap.chw.adapter.ServicesModeAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberFineDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberIgaDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberLoanDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSavingDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSocialFundDao;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.ServicesModel;

import java.util.HashMap;
import java.util.List;

public class ServicesFragment extends Fragment {

    private RecyclerView servicesRecyclerView;
    private ServicesModeAdapter servicesModelAdapter;
    private List<ServicesModel> servicesList;

    public ServicesFragment() {
        // Required empty public constructor
    }
    TextView txtPersonalSaving,txtGroupSaving,txtPersonalLoan,txtGroupLoan,txtPersonalFine,txtGroupFine,txtPersonalSocial,txtGroupSocial,txtPersonalIga,txtGroupIga;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);


        txtPersonalSaving = view.findViewById(R.id.txtPersonalSaving);
        txtGroupSaving = view.findViewById(R.id.txtGroupSaving);
        txtPersonalLoan = view.findViewById(R.id.txtPersonalLoan);
        txtGroupLoan = view.findViewById(R.id.txtGroupLoan);
        txtPersonalFine = view.findViewById(R.id.txtPersonalFine);
        txtGroupFine = view.findViewById(R.id.txtGroupFine);
        txtPersonalSocial = view.findViewById(R.id.txtPersonalSocial);
        txtGroupSocial = view.findViewById(R.id.txtGroupSocial);
        txtPersonalIga = view.findViewById(R.id.txtPersonalIga);
        txtGroupIga = view.findViewById(R.id.txtGroupIga);
//        servicesRecyclerView = view.findViewById(R.id.servicesRecyclerView);
//        servicesList = createDummyData();
//
//        servicesModelAdapter = new ServicesModeAdapter(requireContext(), servicesList);
//        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        servicesRecyclerView.setAdapter(servicesModelAdapter);

        HashMap<String, MembersModel> mymap = ((WeGroupMemberProfileActivity) requireActivity()).getData();
        MembersModel membersModel = mymap.get("uniqueID");

        try {
            String totalAmount = String.valueOf(WeGroupMemberSavingDao.getTotalPersonalAmount(membersModel.getUnique_id()));

            if (totalAmount != null) {
                txtPersonalSaving.setText(totalAmount);
            } else {
                txtPersonalSaving.setText("0");
            }
            String totalGroupSavingAmount = String.valueOf(WeGroupMemberSavingDao.getTotalGroupAmount(membersModel.getGroup_id()));

            if (totalGroupSavingAmount != null) {
                txtGroupSaving.setText(totalGroupSavingAmount);
            } else {
                txtGroupSaving.setText("0");
            }

            String totalAmountLoan = String.valueOf(WeGroupMemberLoanDao.getTotalAmount(membersModel.getUnique_id()));

            if (totalAmountLoan != null) {
                txtPersonalLoan.setText(totalAmountLoan);
            } else {
                txtPersonalLoan.setText("0");
            }

            String totalGroupAmountLoan = String.valueOf(WeGroupMemberLoanDao.getTotalGroupAmount(membersModel.getGroup_id()));

            if (totalGroupAmountLoan != null) {
                txtGroupLoan.setText(totalGroupAmountLoan);
            } else {
                txtGroupLoan.setText("0");
            }

            String totalAmountFine = String.valueOf(WeGroupMemberFineDao.getTotalAmount(membersModel.getUnique_id()));

            if (totalAmountFine != null) {
                txtPersonalFine.setText(totalAmountFine);
            } else {
                txtPersonalFine.setText("0");
            }

            String totalGroupAmountFine = String.valueOf(WeGroupMemberFineDao.getTotalGroupAmount(membersModel.getGroup_id()));

            if (totalGroupAmountFine != null) {
                txtGroupFine.setText(totalGroupAmountFine);
            } else {
                txtGroupFine.setText("0");
            }


            String totalAmountSocial = String.valueOf(WeGroupMemberSocialFundDao.getTotalAmount(membersModel.getUnique_id()));

            if (totalAmountSocial != null) {
                txtPersonalSocial.setText(totalAmountSocial);
            } else {
                txtPersonalSocial.setText("0");
            }

            String totalAmountGroupSocial = String.valueOf(WeGroupMemberSocialFundDao.getTotalGroupAmount(membersModel.getGroup_id()));

            if (totalAmountGroupSocial != null) {
                txtGroupSocial.setText(totalAmountGroupSocial);
            } else {
                txtGroupSocial.setText("0");
            }

            String totalAmountIga = String.valueOf(WeGroupMemberIgaDao.getTotalAmount(membersModel.getUnique_id()));

            if (totalAmountIga != null) {
                txtPersonalIga.setText(totalAmountIga);
            } else {
                txtPersonalIga.setText("0");
            }
            String totalGroupAmountIga = String.valueOf(WeGroupMemberIgaDao.getTotalGroupAmount(membersModel.getGroup_id()));

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

//    private List<ServicesModel> createDummyData() {
//        List<ServicesModel> addService = new ArrayList<>();
//        addService.add(new ServicesModel("Saving", "User Saving: K 0", "Group Saving: K 0"));
//        addService.add(new ServicesModel("Loan", "User Loan: K 0", "Group Loan: K 0"));
//        addService.add(new ServicesModel("Fine", "User Fine: K 0", "Group Fine: K 0"));
//        addService.add(new ServicesModel("Social Fund", "User Social Fund: K 0", "Group Social Fund: K 0"));
//        addService.add(new ServicesModel("Repayment", "User Repayment: K 0", "Group Repayment: K 0"));
//        addService.add(new ServicesModel("IGA", "User IGA: K 0", "Group IGA: K 0"));
//        return addService;
//    }
}
