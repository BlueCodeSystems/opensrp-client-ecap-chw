package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.MotherPmtctProfileActivity;
import com.bluecodeltd.ecap.chw.dao.PmtctDeliveryDao;
import com.bluecodeltd.ecap.chw.model.PmtctDeliveryDetailsModel;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;

public class PMTCTMotherOverviewFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentPmtctMotherOverviewBinding binding;

    TextView txtHouseholdId, txtAddress, txtPhone, txtPmtctDateEnrolled, txtArt,
            txtdate_of_delivery,txtplace_of_delivery, txt_on_art_at_time_of_delivery;
    FloatingActionButton fab;
    CommonPersonObjectClient mother;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = com.bluecodeltd.ecap.chw.databinding.FragmentPmtctMotherOverviewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        txtHouseholdId = binding.hhId;
        txtAddress = binding.pAddress;
        txtPhone = binding.phone;
        txtPmtctDateEnrolled = binding.pmtctDateEnrolled;
        txtdate_of_delivery = binding.dateOfDelivery;
        txtplace_of_delivery = binding.placeOfDelivery;
        txt_on_art_at_time_of_delivery = binding.onArtAtTimeOfDelivery;
        txtHouseholdId.setVisibility(View.GONE);

        fab = getActivity().findViewById(R.id.fabx);

        setViews();

        return view;

   }



    public void setViews() {
        if (getActivity() == null) return;  // safety check

        HashMap<String, PtctMotherModel> mymap = ((MotherPmtctProfileActivity) requireActivity()).getClientDetails();

        String notSet = "Not set";

        // Set default values first
        txtdate_of_delivery.setText(notSet);
        txtplace_of_delivery.setText(notSet);
        txt_on_art_at_time_of_delivery.setText(notSet);
        txtHouseholdId.setText(notSet);
        txtAddress.setText(notSet);
        txtPhone.setText(notSet);
        txtPmtctDateEnrolled.setText(notSet);

        if (mymap == null) return;

        PtctMotherModel motherDetails = mymap.get("client");
        if (motherDetails != null) {
            // Mother details
            txtHouseholdId.setText(getSafeString(motherDetails.getPmtct_id()));
            txtAddress.setText(getSafeString(motherDetails.getHome_address()));
            txtPhone.setText(getSafeString(motherDetails.getMothers_phone()));
            txtPmtctDateEnrolled.setText(getSafeString(motherDetails.getDate_enrolled_pmtct()));

            // Delivery details
            PmtctDeliveryDetailsModel pmtctDeliveryModel = PmtctDeliveryDao.getPmtctDeliveryDetails(motherDetails.getPmtct_id());
            if (pmtctDeliveryModel != null) {
                txtdate_of_delivery.setText(getSafeString(pmtctDeliveryModel.getDate_of_delivery()));
                txtplace_of_delivery.setText(getSafeString(pmtctDeliveryModel.getPlace_of_delivery()));
                txt_on_art_at_time_of_delivery.setText(getSafeString(pmtctDeliveryModel.getOn_art_at_time_of_delivery()));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String getSafeString(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "Not set";
    }



}
