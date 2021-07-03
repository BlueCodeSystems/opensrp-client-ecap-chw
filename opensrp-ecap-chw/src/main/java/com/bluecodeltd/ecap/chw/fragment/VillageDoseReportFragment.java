package com.bluecodeltd.ecap.chw.fragment;

import androidx.annotation.NonNull;
import com.bluecodeltd.ecap.chw.viewholder.ListableViewHolder;

import com.bluecodeltd.ecap.chw.adapter.ListableAdapter;
import com.bluecodeltd.ecap.chw.adapter.VillageDoseAdapter;
import com.bluecodeltd.ecap.chw.dao.ReportDao;
import com.bluecodeltd.ecap.chw.domain.VillageDose;
import com.bluecodeltd.ecap.chw.model.FilterReportFragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rkodev
 */
public class VillageDoseReportFragment extends ReportResultFragment<VillageDose> {
    public static final String TAG = "VillageDoseReportFragment";

    @Override
    protected void executeFetch() {
        presenter.fetchList(() -> {
            boolean includeAll = communityNames.get(0).equals("All communities");
            FilterReportFragmentModel model = new FilterReportFragmentModel();
            List<VillageDose> result = new ArrayList<>(ReportDao.fetchLiveVillageDosesReport(communityIds, reportDate, includeAll,
                    includeAll ? communityNames.get(0) : null, model.getAllLocations()));

            return result;
        });
    }

    @NonNull
    @Override
    public ListableAdapter<VillageDose, ListableViewHolder<VillageDose>> adapter() {
        return new VillageDoseAdapter(list, this, this.getContext());
    }
}
