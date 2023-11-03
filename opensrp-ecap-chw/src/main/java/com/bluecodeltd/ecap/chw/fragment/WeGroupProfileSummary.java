package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.MembersListAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupProfileSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupProfileSummary extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView cycle_number,annual_interest_rate,first_training_meeting_date,date_savings_started,reinvested_savings_cycle_start,registered_members_cycle_start,group_mgt;

    public WeGroupProfileSummary() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupProfileSummary.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupProfileSummary newInstance(String param1, String param2) {
        WeGroupProfileSummary fragment = new WeGroupProfileSummary();
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
        View view = inflater.inflate(R.layout.fragment_we_group_profile_summary, container, false);

        cycle_number = view.findViewById(R.id.cycle_number);
        annual_interest_rate = view.findViewById(R.id.annual_interest_rate);
        first_training_meeting_date = view.findViewById(R.id.first_training_meeting_date);
        date_savings_started = view.findViewById(R.id.date_savings_started);
        reinvested_savings_cycle_start = view.findViewById(R.id.reinvested_savings_cycle_start);
        registered_members_cycle_start = view.findViewById(R.id.registered_members_cycle_start);
        group_mgt = view.findViewById(R.id.group_mgt);

        if (getArguments() != null) {
            String groupId = getArguments().getString("groupId");

            WeGroupModel model = WeGroupDao.getWeGroupsById(groupId);

            if (model != null) {
                if (cycle_number != null) {
                    String cycleNumberText = model.getCycle_number();
                    if (cycleNumberText != null) {
                        cycle_number.setText(cycleNumberText);
                    }
                }

                if (annual_interest_rate != null) {
                    String annualInterestRateText = model.getAnnual_interest_rate();
                    if (annualInterestRateText != null) {
                        annual_interest_rate.setText(annualInterestRateText);
                    }
                }

                if (first_training_meeting_date != null) {
                    String firstTrainingMeetingDateText = model.getFirst_training_meeting_date();
                    if (firstTrainingMeetingDateText != null) {
                        first_training_meeting_date.setText(firstTrainingMeetingDateText);
                    }
                }

                if (date_savings_started != null) {
                    String dateSavingsStartedText = model.getDate_savings_started();
                    if (dateSavingsStartedText != null) {
                        date_savings_started.setText(dateSavingsStartedText);
                    }
                }

                if (reinvested_savings_cycle_start != null) {
                    String reinvestedSavingsCycleStartText = model.getReinvested_savings_cycle_start();
                    if (reinvestedSavingsCycleStartText != null) {
                        reinvested_savings_cycle_start.setText(reinvestedSavingsCycleStartText);
                    }
                }

                if (group_mgt != null) {
                    String groupMgtText = model.getGroup_mgt();
                    if (groupMgtText != null) {
                        group_mgt.setText(groupMgtText);
                    }
                }
            } else {

            }


        }
        return view;
    }
}