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
import com.bluecodeltd.ecap.chw.dao.PtmctMotherMonitoringDao;
import com.bluecodeltd.ecap.chw.model.PtmctMotherMonitoringModel;

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
    TextView dbsDueDate,dbsActualDate,dateTested,nvpDate,childMonitoringVisit;
    ImageView imageviewProfile;

    PtmctMotherMonitoringModel childMonitoring;

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

        HashMap<String, PtmctMotherMonitoringModel> mymap = ((HeiDetailsActivity) requireActivity()).getClientDetails();

// Initialize motherDetails as null.
        PtmctMotherMonitoringModel motherDetails = null;

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

        dbsDueDate = inflateView.findViewById(R.id.dbs_due_date);
        dbsActualDate = inflateView.findViewById(R.id.dbs_actual_date);
        dateTested = inflateView.findViewById(R.id.date_tested);
        nvpDate = inflateView.findViewById(R.id.nvp_date_start);
        childMonitoringVisit = inflateView.findViewById(R.id.child_monitoring_visit);
        imageviewProfile = inflateView.findViewById(R.id.imageview_profile);

        imageviewProfile.setImageResource((motherDetails.getInfants_sex() != null && motherDetails.getInfants_sex().equals("male")) ? R.drawable.child_boy_infant : R.drawable.child_girl_infant);

        cardNumber.setText("Under 5 Card Number: " + (motherDetails.getUnder_five_clinic_card() != null ? motherDetails.getUnder_five_clinic_card() : "Not set"));
        childBirthDate.setText("Date of Birth: " + (motherDetails.getInfants_date_of_birth() != null ? motherDetails.getInfants_date_of_birth() : "Not set"));
        weight.setText("Weight at Birth: " + (motherDetails.getWeight_at_birth() != null ? motherDetails.getWeight_at_birth() + "kg" : "Not set"));
        childFeedingOption.setText("Infant Feeding Option: " + (motherDetails.getInfant_feeding_options() != null ? motherDetails.getInfant_feeding_options() : "Not set"));


//        childMonitoring = new PtmctMotherMonitoringModel();
        childMonitoring = PtmctMotherMonitoringDao.getRecentChildVisit(uniqueId);

        if (childMonitoring != null) {
            dbsDueDate.setText(childMonitoring.getDbs_at_birth_due_date() != null ? childMonitoring.getDbs_at_birth_due_date() : "Not set");
            dbsActualDate.setText(childMonitoring.getDbs_at_birth_actual_date() != null ? childMonitoring.getDbs_at_birth_actual_date() : "Not set");
            dateTested.setText(childMonitoring.getDate_tested() != null ? childMonitoring.getDate_tested() : "Not set");
            nvpDate.setText(childMonitoring.getNvp_date_given() != null ? childMonitoring.getNvp_date_given() : "Not set");
            childMonitoringVisit.setText(childMonitoring.getChild_monitoring_visit() != null ? childMonitoring.getChild_monitoring_visit() : "Not set");
        } else {
            dbsDueDate.setText("Not Conducted");
            dbsActualDate.setText("Not Conducted");
            dateTested.setText("Not Conducted");
            nvpDate.setText("Not Conducted");
            childMonitoringVisit.setText("Not Conducted");
        }




        return inflateView;
    }
}