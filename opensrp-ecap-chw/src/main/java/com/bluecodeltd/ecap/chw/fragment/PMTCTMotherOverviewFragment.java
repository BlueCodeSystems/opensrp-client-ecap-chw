package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.MotherPmtctProfileActivity;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;

public class PMTCTMotherOverviewFragment extends Fragment {

    TextView txtHouseholdId, txtAddress, txtPhone, txtTreatment, txtArt;
    FloatingActionButton fab;
    CommonPersonObjectClient mother;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pmtct_mother_overview, container, false);

        txtHouseholdId = view.findViewById(R.id.hh_id);
        txtAddress = view.findViewById(R.id.p_address);
        txtPhone = view.findViewById(R.id.phone);
        txtTreatment = view.findViewById(R.id.treatment);
        txtArt = view.findViewById(R.id.art_number);

        fab = getActivity().findViewById(R.id.fabx);

        setViews();

        return view;

   }



        public void setViews(){

            HashMap<String, PtctMotherModel> mymap = ((MotherPmtctProfileActivity) requireActivity()).getClientDetails();
            if (mymap != null) {
                PtctMotherModel motherDetails = mymap.get("client");

                if (motherDetails != null) {
                    String pmtctId = motherDetails.getPmtct_id();
                    txtHouseholdId.setText(pmtctId != null ? pmtctId : "Not set");

                    String homeAddress = motherDetails.getHome_address();
                    txtAddress.setText(homeAddress != null ? homeAddress : "Not set");

                    String mothersPhone = motherDetails.getMothers_phone();
                    txtPhone.setText(mothersPhone != null ? mothersPhone : "Not set");

                    String treatmentInitiated = motherDetails.getTreatment_initiated();
                    txtTreatment.setText(treatmentInitiated != null ? treatmentInitiated : "Not set");

                    String artNumber = motherDetails.getArt_number();
                    txtArt.setText(artNumber != null ? artNumber : "Not set");
                } else {

                    txtHouseholdId.setText("Not set");
                    txtAddress.setText("Not set");
                    txtPhone.setText("Not set");
                    txtTreatment.setText("Not set");
                    txtArt.setText("Not set");
                }
            } else {

                txtHouseholdId.setText("Not set");
                txtAddress.setText("Not set");
                txtPhone.setText("Not set");
                txtTreatment.setText("Not set");
                txtArt.setText("Not set");
            }

        }


}
