package org.smartregister.chw.core.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.HIA2ReportsActivity;
import org.smartregister.chw.core.activity.ReportSummaryActivity;
import org.smartregister.chw.core.adapter.ExpandedListAdapter;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.domain.DailyTally;
import org.smartregister.chw.core.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import timber.log.Timber;

public class DailyTalliesFragment extends Fragment {
    private ExpandableListView expandableListView;
    private HashMap<String, ArrayList<DailyTally>> dailyTallies;
    private ProgressDialog progressDialog;

    public static DailyTalliesFragment newInstance() {
        DailyTalliesFragment fragment = new DailyTalliesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.report_expandable_list_view, container, false);
        expandableListView = fragmentView.findViewById(R.id.expandable_list_view);
        expandableListView.setBackgroundColor(getResources().getColor(R.color.white));

        GetAllTalliesTask getAllTalliesTask = new GetAllTalliesTask();
        getAllTalliesTask.execute();

        return fragmentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateExpandableList();
        }
    }

    private void updateExpandableList() {
        updateExpandableList(formatListData());
    }

    @SuppressWarnings("unchecked")
    private void updateExpandableList(final LinkedHashMap<String, List<ExpandedListAdapter.ItemData<String, Date>>> map) {

        if (expandableListView == null) {
            return;
        }

        final Locale locale = Utils.getLocale(getContext());

        ExpandedListAdapter<String, String, Date> expandableListAdapter = new ExpandedListAdapter(getActivity(), map, R.layout.daily_tally_header, R.layout.daily_tally_item);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Object tag = v.getTag(R.id.item_data);
            if (tag instanceof Date) {
                Date date = (Date) tag;
                String dayString = new SimpleDateFormat("dd MMMM yyyy", locale).format(date);
                if (dailyTallies.containsKey(dayString)) {
                    ArrayList<DailyTally> indicators = new ArrayList(dailyTallies.get(dayString));
                    String title = String.format(getString(R.string.daily_tally_), dayString);
                    Intent intent = new Intent(getActivity(), ReportSummaryActivity.class);
                    intent.putExtra(ReportSummaryActivity.EXTRA_TALLIES, indicators);
                    intent.putExtra(ReportSummaryActivity.EXTRA_TITLE, title);
                    startActivity(intent);
                }
            }

            return true;
        });
        expandableListAdapter.notifyDataSetChanged();
    }

    private LinkedHashMap<String, List<ExpandedListAdapter.ItemData<String, Date>>> formatListData() {
        Map<String, List<ExpandedListAdapter.ItemData<String, Date>>> map = new HashMap<>();
        Map<Long, String> sortMap = new TreeMap<>((Comparator<Comparable>) (a, b) -> b.compareTo(a));

        Locale locale = Utils.getLocale(getContext());

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", locale);
        SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd MMMM yyyy", locale);
        if (dailyTallies != null) {
            for (ArrayList<DailyTally> curDay : dailyTallies.values()) {
                if (curDay.size() > 0) {
                    Date day = curDay.get(0).getDay();
                    String monthString = monthFormat.format(day);
                    if (!map.containsKey(monthString)) {
                        map.put(monthString, new ArrayList<>());
                    }

                    map.get(monthString).add(
                            new ExpandedListAdapter.ItemData<>(DAY_FORMAT.format(day), day)
                    );
                    sortMap.put(day.getTime(), monthString);
                }
            }
        }

        LinkedHashMap<String, List<ExpandedListAdapter.ItemData<String, Date>>> sortedMap = new LinkedHashMap<>();
        for (Long curKey : sortMap.keySet()) {
            List<ExpandedListAdapter.ItemData<String, Date>> list = map.get(sortMap.get(curKey));
            Collections.sort(list, new Comparator<ExpandedListAdapter.ItemData<String, Date>>() {
                @Override
                public int compare(ExpandedListAdapter.ItemData<String, Date> lhs,
                                   ExpandedListAdapter.ItemData<String, Date> rhs) {
                    return lhs.getTagData().compareTo(rhs.getTagData());
                }
            });
            sortedMap.put(sortMap.get(curKey), list);
        }

        return sortedMap;
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait_message));
    }

    private void showProgressDialog() {
        try {
            if (progressDialog == null) {
                initializeProgressDialog();
            }

            progressDialog.show();
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    private void hideProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }


    // Inner class
    private class GetAllTalliesTask extends AsyncTask<Void, Void, HashMap<String, ArrayList<DailyTally>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected HashMap<String, ArrayList<DailyTally>> doInBackground(Void... params) {
            Locale locale = Utils.getLocale(getContext());
            Calendar startDate = Calendar.getInstance();

            startDate.set(Calendar.DAY_OF_MONTH, 1);
            startDate.set(Calendar.HOUR_OF_DAY, 0);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);
            startDate.set(Calendar.MILLISECOND, 0);
            startDate.add(Calendar.MONTH, -1 * HIA2ReportsActivity.MONTH_SUGGESTION_LIMIT);
            return CoreChwApplication.getInstance().dailyTalliesRepository()
                    .findAll(new SimpleDateFormat("dd MMMM yyyy", locale), startDate.getTime(), Calendar.getInstance().getTime());
        }

        @Override
        protected void onPostExecute(HashMap<String, ArrayList<DailyTally>> tallies) {
            super.onPostExecute(tallies);
            hideProgressDialog();
            DailyTalliesFragment.this.dailyTallies = tallies;
            updateExpandableList();
        }
    }
}
