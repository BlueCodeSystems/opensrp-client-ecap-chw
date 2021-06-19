package ecap.smartregister.chw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import ecap.smartregister.chw.contract.ListContract;
import ecap.smartregister.chw.viewholder.ListableViewHolder;
import ecap.smartregister.chw.viewholder.ReportViewHolder;

import ecap.smartregister.chw.R;

import ecap.smartregister.chw.domain.ReportType;

import java.util.List;

public class ReportsFragmentAdapter extends ListableAdapter<ReportType, ListableViewHolder<ReportType>> {

    public ReportsFragmentAdapter(List<ReportType> items, ListContract.View<ReportType> view) {
        super(items, view);
    }

    @NonNull
    @Override
    public ListableViewHolder<ReportType> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_fragment_item, parent, false);
        return new ReportViewHolder(view);
    }

}
