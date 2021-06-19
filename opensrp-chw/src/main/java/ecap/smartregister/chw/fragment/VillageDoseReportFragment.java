package ecap.smartregister.chw.fragment;

import androidx.annotation.NonNull;
import ecap.smartregister.chw.viewholder.ListableViewHolder;

import ecap.smartregister.chw.adapter.ListableAdapter;
import ecap.smartregister.chw.adapter.VillageDoseAdapter;
import ecap.smartregister.chw.dao.ReportDao;
import ecap.smartregister.chw.domain.VillageDose;
import ecap.smartregister.chw.model.FilterReportFragmentModel;

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
