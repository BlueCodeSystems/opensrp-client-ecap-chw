package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HTSDetailsActivity;
import com.bluecodeltd.ecap.chw.activity.WeGroupMemberProfileActivity;
import com.bluecodeltd.ecap.chw.model.HivTestingServiceModel;
import com.bluecodeltd.ecap.chw.model.MembersModel;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView admission_date,user_role,user_phone,user_nrc,next_of_kin,next_of_kin_phone,group_name,group_id;

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(String param1, String param2) {
        SummaryFragment fragment = new SummaryFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        admission_date = view.findViewById(R.id.admission_date);
        user_role = view.findViewById(R.id.user_role);
        user_phone = view.findViewById(R.id.user_phone);
        user_nrc = view.findViewById(R.id.user_nrc);
        next_of_kin = view.findViewById(R.id.next_of_kin);
        next_of_kin_phone = view.findViewById(R.id.next_of_kin_phone);
        group_name = view.findViewById(R.id.group_name);
        group_id = view.findViewById(R.id.group_id);

        HashMap<String, MembersModel> mymap = ((WeGroupMemberProfileActivity) requireActivity()).getData();
        MembersModel membersModel = mymap.get("uniqueID");
        try {
            String admissionDate = membersModel.getAdmission_date();
            if (admissionDate != null) {
                admission_date.setText(admissionDate);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            String role = membersModel.getRole();
            if (role != null) {
                user_role.setText(role);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            String phoneNumber = membersModel.getPhone_number();
            if (phoneNumber != null) {
                user_phone.setText(phoneNumber);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            String nrc = membersModel.getNrc();
            if (nrc != null) {
                user_nrc.setText(nrc);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            String nextOfKin = membersModel.getNext_of_kin();
            if (nextOfKin != null) {
                next_of_kin.setText(nextOfKin);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            String nextOfKinPhone = membersModel.getNext_of_kin_phone();
            if (nextOfKinPhone != null) {
                next_of_kin_phone.setText(nextOfKinPhone);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            String groupId = membersModel.getGroup_id();
            if (groupId != null) {
                group_id.setText(groupId);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        return view;
    }

}