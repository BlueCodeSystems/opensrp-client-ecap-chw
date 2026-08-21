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
import com.bluecodeltd.ecap.chw.dao.EcMotherIndexDao;
import com.bluecodeltd.ecap.chw.dao.PmtctDeliveryDao;
import com.bluecodeltd.ecap.chw.model.EcMotherIndexModel;
import com.bluecodeltd.ecap.chw.model.PmtctDeliveryDetailsModel;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

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

        fab = getActivity().findViewById(R.id.fabx);

        refreshViews();

        return view;

   }



    public void refreshViews() {
        if (getActivity() == null || binding == null) return;  // safety check

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
            String pmtctId = motherDetails.getPmtct_id();
            String householdIdForDisplay = motherDetails.getHousehold_id();
            txtHouseholdId.setText(getSafeString(!isNullOrEmpty(pmtctId) ? pmtctId : householdIdForDisplay));
            txtPmtctDateEnrolled.setText(getSafeString(motherDetails.getDate_enrolled_pmtct()));

            // Address and phone — fall back to EcMotherIndexDao if not set on the model
            String homeAddress = motherDetails.getHome_address();
            String mothersPhone = motherDetails.getMothers_phone();
            if ((homeAddress == null || homeAddress.trim().isEmpty())
                    || (mothersPhone == null || mothersPhone.trim().isEmpty())) {
                String householdId = motherDetails.getHousehold_id();
                if (householdId != null && !householdId.trim().isEmpty()) {
                    List<EcMotherIndexModel> ecMothers = EcMotherIndexDao.getMothers(householdId);
                    if (!ecMothers.isEmpty()) {
                        EcMotherIndexModel ecMother = ecMothers.get(0);
                        if (homeAddress == null || homeAddress.trim().isEmpty()) {
                            homeAddress = ecMother.getHome_address();
                        }
                        if (mothersPhone == null || mothersPhone.trim().isEmpty()) {
                            mothersPhone = ecMother.getMothers_phone();
                        }
                    }
                }
            }
            txtAddress.setText(getSafeString(homeAddress));
            txtPhone.setText(getSafeString(mothersPhone));

            // Delivery details — DAO queries by household_id column; fall back to pmtct_id
            String deliveryLookupId = !isNullOrEmpty(motherDetails.getHousehold_id())
                    ? motherDetails.getHousehold_id()
                    : motherDetails.getPmtct_id();
            PmtctDeliveryDetailsModel pmtctDeliveryModel = PmtctDeliveryDao.getPmtctDeliveryDetails(deliveryLookupId);
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

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }



}
