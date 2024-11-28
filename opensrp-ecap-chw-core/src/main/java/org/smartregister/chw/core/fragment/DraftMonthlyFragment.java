package org.smartregister.chw.core.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.HIA2ReportsActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.domain.MonthlyTally;
import org.smartregister.chw.core.repository.MonthlyTalliesRepository;
import org.smartregister.chw.core.task.FetchEditedMonthlyTalliesTask;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DraftMonthlyFragment extends Fragment {
    private final View.OnClickListener monthDraftsClickListener = v -> {
        Object tag = v.getTag();
        if (tag instanceof Date) {
            startMonthlyReportForm((Date) tag);
        }
    };
    protected Button startNewMonthlyReport;
    private AlertDialog alertDialog;
    protected final View.OnClickListener monthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            alertDialog.dismiss();
            Object tag = v.getTag();
            if (tag instanceof Date) {
                startMonthlyReportForm((Date) tag);
            }
        }
    };
    private DraftsAdapter draftsAdapter;
    protected ListView listView;
    protected View noDraftsView;

    public static DraftMonthlyFragment newInstance() {
        DraftMonthlyFragment fragment = new DraftMonthlyFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sent_monthly_fragment, container, false);
        listView = view.findViewById(R.id.list);
        noDraftsView = view.findViewById(R.id.empty_view);
        startNewMonthlyReport = view.findViewById(R.id.start_new_report_enabled);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupEditedDraftsView();
        setupUneditedDraftsView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void updateStartNewReportButton(final List<Date> dates) {
        boolean hia2ReportsReady = dates != null && !dates.isEmpty();
        if (hia2ReportsReady) {
            startNewMonthlyReport.setBackground(ContextCompat.getDrawable(startNewMonthlyReport.getContext(), R.drawable.vaccination_today_bg));
            startNewMonthlyReport.setTextColor(ContextCompat.getColor(startNewMonthlyReport.getContext(), R.color.white));
        } else {
            startNewMonthlyReport.setBackground(ContextCompat.getDrawable(startNewMonthlyReport.getContext(), R.drawable.vaccination_earlier_bg));
            startNewMonthlyReport.setTextColor(ContextCompat.getColor(startNewMonthlyReport.getContext(), R.color.translucent_client_list_grey));
        }
        startNewMonthlyReport.setOnClickListener(v -> {
            if (hia2ReportsReady) {
                Collections.sort(dates, (lhs, rhs) -> rhs.compareTo(lhs));
                updateResults(dates, monthClickListener);
            } else {
                show(Snackbar.make(startNewMonthlyReport, getString(R.string.no_monthly_ready), Snackbar.LENGTH_SHORT));
            }
        });
    }

    private void setupUneditedDraftsView() {
        Utils.startAsyncTask(new AsyncTask<Void, Void, List<Date>>() {
            @Override
            protected List<Date> doInBackground(Void... params) {
                MonthlyTalliesRepository monthlyTalliesRepository = CoreChwApplication
                        .getInstance().monthlyTalliesRepository();
                Calendar startDate = Calendar.getInstance();
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);
                startDate.add(Calendar.MONTH, -1 * HIA2ReportsActivity.MONTH_SUGGESTION_LIMIT);

                Calendar endDate = Calendar.getInstance();
                // should be 1, setting temporarily to 30
                endDate.set(Calendar.DAY_OF_MONTH, 30);// Set date to first day of this month
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                endDate.add(Calendar.DATE, -1);// Move the date to last day of last month

                return monthlyTalliesRepository.findUneditedDraftMonths(startDate.getTime(),
                        endDate.getTime());
            }

            @Override
            protected void onPostExecute(List<Date> dates) {
                updateStartNewReportButton(dates);
            }
        }, null);
    }

    private void setupEditedDraftsView() {
        ((HIA2ReportsActivity) getActivity()).refreshDraftMonthlyTitle();

        Utils.startAsyncTask(new FetchEditedMonthlyTalliesTask(monthlyTallies -> updateDraftsReportListView(monthlyTallies)), null);
    }

    public void updateDraftsReportListView(final List<MonthlyTally> monthlyTallies) {
        if (monthlyTallies != null && !monthlyTallies.isEmpty()) {
            noDraftsView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            if (draftsAdapter == null) {
                draftsAdapter = new DraftsAdapter(monthlyTallies);
                listView.setAdapter(draftsAdapter);
            } else {
                draftsAdapter.setList(monthlyTallies);
                draftsAdapter.notifyDataSetChanged();
            }
        } else {
            noDraftsView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    protected void updateResults(final List<Date> list, final View.OnClickListener clickListener) {
        final SimpleDateFormat DF_YYYYMM = new SimpleDateFormat("yyyy-MM", Utils.getLocale(getContext()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.month_results, null);

        ListView listView = view.findViewById(R.id.list_view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PathDialog);
        builder.setView(view);
        builder.setCancelable(true);

        CustomFontTextView title = new CustomFontTextView(getActivity());
        title.setText(getString(R.string.reports_available));
        title.setGravity(Gravity.START);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        title.setFontVariant(FontVariant.BOLD);
        title.setPadding(getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin), getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));

        builder.setCustomTitle(title);

        alertDialog = builder.create();

        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater)
                            getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                    view = inflater.inflate(R.layout.month_item, null);
                } else {
                    view = convertView;
                }

                TextView tv = view.findViewById(R.id.tv);
                Date date = list.get(position);
                String text = DF_YYYYMM.format(date);
                tv.setText(text);
                tv.setTag(date);

                view.setOnClickListener(clickListener);
                view.setTag(list.get(position));

                return view;
            }
        };

        listView.setAdapter(baseAdapter);
        alertDialog.show();

    }

    private void show(final Snackbar snackbar) {
        if (snackbar == null) {
            return;
        }

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());

        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        snackbar.show();

    }

    protected void startMonthlyReportForm(Date date) {
        ((HIA2ReportsActivity) getActivity()).startMonthlyReportForm("monthly_report", date);
    }

    private class DraftsAdapter extends BaseAdapter {
        private List<MonthlyTally> list;

        private DraftsAdapter(List<MonthlyTally> list) {
            setList(list);
        }

        public void setList(List<MonthlyTally> list) {
            this.list = list;
            if (this.list != null) {
                Collections.sort(list, (lhs, rhs) -> rhs.getMonth().compareTo(lhs.getMonth()));
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            Locale locale = Utils.getLocale(getContext());
            SimpleDateFormat df = new SimpleDateFormat("MMM yyyy", locale);
            SimpleDateFormat DF_DDMMYY = new SimpleDateFormat("dd/MM/yy", locale);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.month_draft_item, null);
            } else {
                view = convertView;
            }

            TextView tv = view.findViewById(R.id.tv);
            TextView startedAt = view.findViewById(R.id.month_draft_started_at);
            MonthlyTally date = list.get(position);
            String text = df.format(date.getMonth());
            String startedAt_ = DF_DDMMYY.format(date.getCreatedAt());
            String started = getActivity().getString(R.string.started);
            tv.setText(text);
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            tv.setTag(text);
            startedAt.setText(started + " " + startedAt_);

            view.setOnClickListener(monthDraftsClickListener);
            view.setTag(date.getMonth());

            return view;
        }
    }
}
