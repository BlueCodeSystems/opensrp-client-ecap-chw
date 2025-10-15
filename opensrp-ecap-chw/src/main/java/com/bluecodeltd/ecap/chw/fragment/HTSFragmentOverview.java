package com.bluecodeltd.ecap.chw.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HTSDetailsActivity;
import com.bluecodeltd.ecap.chw.model.HivTestingServiceModel;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HTSFragmentOverview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HTSFragmentOverview extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentHTSOverviewBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View rootView;
    TextView facility,partner,testing_modality,artNumber,caseworkerName, entry_point,district,art_treatment, date_edited,phone, date_case_created;
    LinearLayout artLayout;
    public HTSFragmentOverview() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HTSFragmentOverview.
     */
    // TODO: Rename and change types and number of parameters
    public static HTSFragmentOverview newInstance(String param1, String param2) {
        HTSFragmentOverview fragment = new HTSFragmentOverview();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.bluecodeltd.ecap.chw.databinding.FragmentHTSOverviewBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();

        facility = binding.facility;
        testing_modality = binding.testingModality;
        artNumber = binding.artNumber;
        entry_point = binding.entryPoint;
        caseworkerName = binding.caseworkerName;
        phone = binding.phone;
        partner = binding.implementingPartner;
        date_edited = binding.dateEdited;
        date_case_created = binding.dateCaseCreated;
        artLayout = binding.artLayout;



        SharedPreferences caseWorkDetails = PreferenceManager.getDefaultSharedPreferences(getContext());
        // Inflate the layout for this fragment
        HashMap<String, HivTestingServiceModel> mymap = ((HTSDetailsActivity) requireActivity()).getLinkID();
        HivTestingServiceModel htsModel = mymap.get("client");

if(htsModel.getTesting_modality() != null && htsModel.getTesting_modality().equals("Other Community")){
    artLayout.setVisibility(View.GONE);
}
        facility.setText(caseWorkDetails.getString("facility", "Anonymous") != null ? caseWorkDetails.getString("facility", "Anonymous") : "Not Set");
        testing_modality.setText(htsModel.getTesting_modality() != null ? htsModel.getTesting_modality() : "Not Set");
        artNumber.setText(htsModel.getArt_number() != null ? htsModel.getArt_number() : "Not Set");
        entry_point.setText(htsModel.getEntry_point() != null ? htsModel.getEntry_point() : "Not Set");
        caseworkerName.setText(htsModel.getCaseworker_name() != null ? htsModel.getCaseworker_name() : "Not Set");
        phone.setText(caseWorkDetails.getString("phone", "Anonymous") != null ? caseWorkDetails.getString("phone", "Anonymous") : "Not Set");
        partner.setText(htsModel.getImplementing_partner() != null ? htsModel.getImplementing_partner() : "Not Set");
        date_edited.setText(htsModel.getDate_edited() != null ? htsModel.getDate_edited() : "Not Set");
        date_case_created.setText(htsModel.getDate_client_created() != null ? htsModel.getDate_client_created() : "Not Set");



        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
