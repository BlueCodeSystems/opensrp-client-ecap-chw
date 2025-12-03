package org.smartregister.chw.core.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.ReportSummaryActivity;
import org.smartregister.chw.core.adapter.ExpandedListAdapter;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.domain.MonthlyTally;
import org.smartregister.chw.core.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class SentMonthlyFragment extends Fragment {

    private ExpandableListView expandableListView;
    private HashMap<String, ArrayList<MonthlyTally>> sentMonthlyTallies;
    private ProgressDialog progressDialog;

    public static SentMonthlyFragment newInstance() {
        SentMonthlyFragment fragment = new SentMonthlyFragment();
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
        Utils.startAsyncTask(new GetSentTalliesTask(this), null);
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
        return fragmentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateExpandedList();
            Utils.startAsyncTask(new GetSentTalliesTask(this), null);
        }
    }


    public void setSentMonthlyTallies(HashMap<String, ArrayList<MonthlyTally>> sentMonthlyTallies) {
        this.sentMonthlyTallies = sentMonthlyTallies;
    }

    public void updateExpandedList() {
        updateExpandedList(formatListData());
    }

    @SuppressWarnings("unchecked")
    private void updateExpandedList(final LinkedHashMap<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>>> map) {
        if (expandableListView == null) {
            return;
        }

        final Locale locale = Utils.getLocale(getContext());
        final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MMMM yyyy", locale);
        ExpandedListAdapter<String, Pair<String, String>, Date> expandableListAdapter =
                new ExpandedListAdapter(getActivity(), map, R.layout.sent_monthly_header, R.layout.sent_monthly_item);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Object tag = v.getTag(R.id.item_data);
            if (tag instanceof Date) {
                Date month = (Date) tag;
                if (sentMonthlyTallies.containsKey(MONTH_YEAR_FORMAT.format(month))
                        && sentMonthlyTallies.get(MONTH_YEAR_FORMAT.format(month)).size() > 0) {
                    ArrayList<MonthlyTally> indicators = sentMonthlyTallies
                            .get(MONTH_YEAR_FORMAT.format(month));
                    String dateSubmitted = new SimpleDateFormat("dd/MM/yy", locale)
                            .format(indicators.get(0).getDateSent());
                    String subTitle = String.format(getString(R.string.submitted_by_),
                            dateSubmitted, indicators.get(0).getProviderId());
                    String monthString = MONTH_YEAR_FORMAT.format(month);
                    String title = String.format(getString(R.string.sent_reports_),
                            monthString);
                    Intent intent = new Intent(getActivity(), ReportSummaryActivity.class);
                    intent.putExtra(ReportSummaryActivity.EXTRA_TALLIES, indicators);
                    intent.putExtra(ReportSummaryActivity.EXTRA_TITLE, title);
                    intent.putExtra(ReportSummaryActivity.EXTRA_SUB_TITLE, subTitle);
                    startActivity(intent);
                }
            }
            return true;
        });
        expandableListAdapter.notifyDataSetChanged();
    }


    private LinkedHashMap<String,
            List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>>> formatListData() {
        Map<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>>> map = new HashMap<>();

        Locale locale = Utils.getLocale(getContext());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", locale);
        SimpleDateFormat dateSentFormat = new SimpleDateFormat("M/d/yy", locale);
        SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MMMM yyyy", locale);
        Map<Long, String> sortMap = new TreeMap<>((Comparator<Comparable>) (a, b) -> b.compareTo(a));

        if (sentMonthlyTallies != null) {
            for (List<MonthlyTally> curMonthTallies : sentMonthlyTallies.values()) {
                if (curMonthTallies != null && curMonthTallies.size() > 0) {
                    Date month = curMonthTallies.get(0).getMonth();
                    String year = yearFormat.format(month);
                    if (!map.containsKey(year)) {
                        map.put(year, new ArrayList<>());
                    }

                    String details = String.format(getString(R.string.sent_by),
                            dateSentFormat.format(curMonthTallies.get(0).getDateSent()),
                            curMonthTallies.get(0).getProviderId());
                    map.get(year)
                            .add(new ExpandedListAdapter.ItemData<>(
                                    Pair.create(MONTH_YEAR_FORMAT.format(month), details), month));
                    sortMap.put(month.getTime(), year);
                }
            }
        }

        LinkedHashMap<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>>> sortedMap = new LinkedHashMap<>();
        for (Long curKey : sortMap.keySet()) {
            List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>> list = map.get(sortMap.get(curKey));
            Collections.sort(list, (lhs, rhs) -> rhs.getTagData().compareTo(lhs.getTagData()));
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

    public ExpandableListView getExpandableListView() {
        return expandableListView;
    }


    // Inner class
    private static class GetSentTalliesTask extends AsyncTask<Void, Void, HashMap<String, ArrayList<MonthlyTally>>> {

        private SentMonthlyFragment sentMonthlyFragment;

        public GetSentTalliesTask(SentMonthlyFragment sentMonthlyFragment) {
            this.sentMonthlyFragment = sentMonthlyFragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sentMonthlyFragment.showProgressDialog();
        }

        @Override
        protected HashMap<String, ArrayList<MonthlyTally>> doInBackground(Void... params) {
            Locale locale = Utils.getLocale(sentMonthlyFragment.getContext());
            SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MMMM yyyy", locale);
            return CoreChwApplication.getInstance().monthlyTalliesRepository().findAllSent(MONTH_YEAR_FORMAT);
        }

        @Override
        protected void onPostExecute(HashMap<String, ArrayList<MonthlyTally>> stringListHashMap) {
            super.onPostExecute(stringListHashMap);
            sentMonthlyFragment.hideProgressDialog();
            sentMonthlyFragment.sentMonthlyTallies = stringListHashMap;
            sentMonthlyFragment.updateExpandedList();
        }
    }
}
