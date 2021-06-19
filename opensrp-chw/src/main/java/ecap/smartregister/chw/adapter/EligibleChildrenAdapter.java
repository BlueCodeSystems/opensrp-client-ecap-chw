package ecap.smartregister.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import ecap.smartregister.chw.contract.ListContract;
import ecap.smartregister.chw.viewholder.EligibleChildrenViewHolder;
import ecap.smartregister.chw.viewholder.ListableViewHolder;

import ecap.smartregister.chw.R;

import ecap.smartregister.chw.domain.EligibleChild;

import java.util.List;

public class EligibleChildrenAdapter extends ListableAdapter<EligibleChild, ListableViewHolder<EligibleChild>> {
     private Context context;
    public EligibleChildrenAdapter(List<EligibleChild> items, ListContract.View<EligibleChild> view, Context context) {
        super(items, view);
        this.context = context;
    }

    @NonNull
    @Override
    public ListableViewHolder<EligibleChild> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eligible_children_report_item, parent, false);
        return new EligibleChildrenViewHolder(view, context);
    }
}
