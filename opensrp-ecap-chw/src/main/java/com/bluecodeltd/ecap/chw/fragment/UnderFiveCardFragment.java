package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HeiDetailsActivity;
import com.bluecodeltd.ecap.chw.dao.ChildMonitoringDao;
import com.bluecodeltd.ecap.chw.model.ChildMonitoringModel;
import com.bluecodeltd.ecap.chw.model.PmtctChildModel;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UnderFiveCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnderFiveCardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View inflateView;
    TextView cardNumber,childBirthDate,weight, childFeedingOption;
    TextView followUpVisitDate, pediaticDate, hiv_status,childMonitoringVisit;
    ImageView imageviewProfile;

    ChildMonitoringModel childMonitoring;

    public UnderFiveCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UnderFiveCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UnderFiveCardFragment newInstance(String param1, String param2) {
        UnderFiveCardFragment fragment = new UnderFiveCardFragment();
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
        // Inflate the layout for this fragment
        inflateView = inflater.inflate(R.layout.fragment_under_five_card, container, false);

        HashMap<String, PmtctChildModel> mymap = ((HeiDetailsActivity) requireActivity()).getClientDetails();

// Initialize motherDetails as null.
        PmtctChildModel motherDetails = null;

        String uniqueId = null;


        if (mymap != null) {
            motherDetails = mymap.get("client");

            if (motherDetails != null) {
                uniqueId = motherDetails.getUnique_id();

                if (uniqueId == null || uniqueId.isEmpty()) {
                }
            } else {

            }
        } else {

        }

        cardNumber = inflateView.findViewById(R.id.card_number);
        childBirthDate = inflateView.findViewById(R.id.child_dob);
        weight = inflateView.findViewById(R.id.child_weight);
        childFeedingOption = inflateView.findViewById(R.id.infant_feeding_option);

        followUpVisitDate = inflateView.findViewById(R.id.pediatic_visit);
        pediaticDate = inflateView.findViewById(R.id.pediatic_date);
        hiv_status= inflateView.findViewById(R.id.hiv_status_r_nr);
//        hiv_status = inflateView.findViewById(R.id.nvp_date_start);
//        childMonitoringVisit = inflateView.findViewById(R.id.child_monitoring_visit);
        imageviewProfile = inflateView.findViewById(R.id.imageview_profile);

        try {
            imageviewProfile.setImageResource((motherDetails != null && motherDetails.getInfants_sex() != null && motherDetails.getInfants_sex().equals("male"))
                    ? org.smartregister.R.drawable.child_boy_infant
                    : org.smartregister.R.drawable.child_girl_infant);
        } catch (NullPointerException e) {
            // Handle the exception, maybe set a default image or log the error
//            imageviewProfile.setImageResource(R.drawable.default_infant_image);
            e.printStackTrace();
        }


        cardNumber.setText("Under 5 Card Number: " + (motherDetails.getUnder_five_clinic_card() != null ? motherDetails.getUnder_five_clinic_card() : "Not set"));
        childBirthDate.setText("Date of Birth: " + (motherDetails.getInfants_date_of_birth() != null ? motherDetails.getInfants_date_of_birth() : "Not set"));
        weight.setText("Weight at Birth: " + (motherDetails.getWeight_at_birth() != null ? motherDetails.getWeight_at_birth() + "kg" : "Not set"));
        childFeedingOption.setText("Infant Feeding Option: " + (motherDetails.getInfant_feeding_options() != null ? motherDetails.getInfant_feeding_options() : "Not set"));


//        childMonitoring = new PtmctMotherMonitoringModel();
        childMonitoring = ChildMonitoringDao.getRecentChildVisit(uniqueId);

        if (childMonitoring != null) {
            followUpVisitDate.setText(childMonitoring.getPediatic_care_follow_up() != null ? childMonitoring.getPediatic_care_follow_up() : "Not set");
            pediaticDate.setText(childMonitoring.getDate() != null ? childMonitoring.getDate() : "Not set");
            hiv_status.setText(childMonitoring.getHiv_test() != null ? childMonitoring.getHiv_test() : "Not set");
//            childMonitoringVisit.setText(childMonitoring.getDate_tested() != null ? childMonitoring.getDate_tested() : "Not set");
        } else {
            followUpVisitDate.setText("Not Conducted");
            pediaticDate.setText("Not Conducted");
//            dateTested.setText("Not Conducted");
            hiv_status.setText("Not Conducted");
//            childMonitoringVisit.setText("Not Conducted");
        }




        return inflateView;
    }
}