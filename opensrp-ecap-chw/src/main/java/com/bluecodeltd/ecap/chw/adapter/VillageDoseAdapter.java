package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.bluecodeltd.ecap.chw.contract.ListContract;
import com.bluecodeltd.ecap.chw.viewholder.ListableViewHolder;
import com.bluecodeltd.ecap.chw.viewholder.VillageDoseViewHolder;

import com.bluecodeltd.ecap.chw.R;

import com.bluecodeltd.ecap.chw.domain.VillageDose;

import java.util.List;

public class VillageDoseAdapter extends ListableAdapter<VillageDose, ListableViewHolder<VillageDose>> {
   private Context context;
    public VillageDoseAdapter(List<VillageDose> items, ListContract.View<VillageDose> view, Context context) {
        super(items, view);
        this.context = context;
    }

    @NonNull
    @Override
    public ListableViewHolder<VillageDose> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.village_dose_report_item, parent, false);
        return new VillageDoseViewHolder(view, context);
    }
}
