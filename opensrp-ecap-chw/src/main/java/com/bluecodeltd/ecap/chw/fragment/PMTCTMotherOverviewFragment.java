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

    TextView txtHouseholdId, txtAddress, txtPhone, txtPmtctDateEnrolled, txtArt,
            txtdate_of_delivery,txtplace_of_delivery, txt_on_art_at_time_of_delivery;
    FloatingActionButton fab;
    CommonPersonObjectClient mother;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pmtct_mother_overview, container, false);

        txtHouseholdId = view.findViewById(R.id.hh_id);
        txtAddress = view.findViewById(R.id.p_address);
        txtPhone = view.findViewById(R.id.phone);
        txtPmtctDateEnrolled = view.findViewById(R.id.pmtct_date_enrolled);
        txtdate_of_delivery = view.findViewById(R.id.date_of_delivery);
        txtplace_of_delivery = view.findViewById(R.id.place_of_delivery);
        txt_on_art_at_time_of_delivery = view.findViewById(R.id.on_art_at_time_of_delivery);
        txtHouseholdId.setVisibility(View.GONE);

        fab = getActivity().findViewById(R.id.fabx);

        setViews();

        return view;

   }



        public void setViews(){

            HashMap<String, PtctMotherModel> mymap = ((MotherPmtctProfileActivity) requireActivity()).getClientDetails();
            if (mymap != null) {
                PtctMotherModel motherDetails = mymap.get("client");
                PmtctDeliveryDetailsModel pmtctDeliveryModel= PmtctDeliveryDao.getPmtctDeliveryDetails(motherDetails.getPmtct_id());

                if (pmtctDeliveryModel != null) {
                    String dateOfDelivery = pmtctDeliveryModel.getDate_of_delivery();
                    txtdate_of_delivery.setText(dateOfDelivery != null ? dateOfDelivery : "Not set");

                    String placeOfDelivery = pmtctDeliveryModel.getPlace_of_delivery();
                    txtplace_of_delivery.setText(placeOfDelivery != null ? placeOfDelivery : "Not set");

                    String onArtAtTimeOfDelivery = pmtctDeliveryModel.getOn_art_at_time_of_delivery();
                    txt_on_art_at_time_of_delivery.setText(onArtAtTimeOfDelivery != null ? onArtAtTimeOfDelivery : "Not set");
                } else {
                    txtdate_of_delivery.setText("Not set");
                    txtplace_of_delivery.setText("Not set");
                    txt_on_art_at_time_of_delivery.setText("Not set");
                }




                if (motherDetails != null) {
                    String pmtctId = motherDetails.getPmtct_id();
                    txtHouseholdId.setText(pmtctId != null ? pmtctId : "Not set");

                    String homeAddress = motherDetails.getHome_address();
                    txtAddress.setText(homeAddress != null ? homeAddress : "Not set");

                    String mothersPhone = motherDetails.getMothers_phone();
                    txtPhone.setText(mothersPhone != null ? mothersPhone : "Not set");

                    String pmtct_date_enrolled = motherDetails.getDate_enrolled_pmtct();
                    txtPmtctDateEnrolled.setText(pmtct_date_enrolled != null ? pmtct_date_enrolled : "Not set");

//                    String artNumber = motherDetails.getArt_number();
//                    txtArt.setText(artNumber != null ? artNumber : "Not set");
                } else {

                    txtHouseholdId.setText("Not set");
                    txtAddress.setText("Not set");
                    txtPhone.setText("Not set");
                    txtPmtctDateEnrolled.setText("Not set");

                }
            } else {

                txtHouseholdId.setText("Not set");
                txtAddress.setText("Not set");
                txtPhone.setText("Not set");
                txtPmtctDateEnrolled.setText("Not set");

            }

        }


}
