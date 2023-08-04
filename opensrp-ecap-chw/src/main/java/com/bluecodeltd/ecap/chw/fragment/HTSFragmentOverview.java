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
import com.bluecodeltd.ecap.chw.model.HIVTestingServiceModel;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HTSFragmentOverview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HTSFragmentOverview extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View rootView;
    TextView partner,facility,province, district,art_treatment,artNumber, edited_by,phone,date_edited;
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
        rootView = inflater.inflate(R.layout.fragment_h_t_s_overview, container, false);
        partner = rootView.findViewById(R.id.partner);
        facility = rootView.findViewById(R.id.facility);
        province = rootView.findViewById(R.id.province);
        district = rootView.findViewById(R.id.district);
        art_treatment = rootView.findViewById(R.id.art_treatment);
        artNumber = rootView.findViewById(R.id.art_number);
        edited_by = rootView.findViewById(R.id.edited_by);
        phone = rootView.findViewById(R.id.phone);
        date_edited = rootView.findViewById(R.id.date_edited);
        artLayout = rootView.findViewById(R.id.artLayout);

        SharedPreferences caseWorkDetails = PreferenceManager.getDefaultSharedPreferences(getContext());
        // Inflate the layout for this fragment
        HashMap<String, HIVTestingServiceModel> mymap = ((HTSDetailsActivity) requireActivity()).getLinkID();
        HIVTestingServiceModel htsModel = mymap.get("client");

        partner.setText(htsModel.getImplementing_partner());
        facility.setText(caseWorkDetails.getString("facility", "Anonymous"));
        province.setText(htsModel.getProvince());
        district.setText(htsModel.getDistrict());
        artNumber.setText(htsModel.getArt_number());
        phone.setText(caseWorkDetails.getString("phone", "Anonymous"));

        artLayout.setVisibility(View.GONE);


        return rootView;
    }
}