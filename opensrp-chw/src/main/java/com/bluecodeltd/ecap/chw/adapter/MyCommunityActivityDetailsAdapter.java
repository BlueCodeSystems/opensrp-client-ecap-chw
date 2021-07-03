package com.bluecodeltd.ecap.chw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.bluecodeltd.ecap.chw.contract.ListContract;
import com.bluecodeltd.ecap.chw.viewholder.ListableViewHolder;
import com.bluecodeltd.ecap.chw.viewholder.MyCommunityActivityDetailsViewHolder;

import com.bluecodeltd.ecap.chw.R;

import com.bluecodeltd.ecap.chw.domain.EligibleChild;

import java.util.List;

public class MyCommunityActivityDetailsAdapter extends ListableAdapter<EligibleChild, ListableViewHolder<EligibleChild>> {

    public MyCommunityActivityDetailsAdapter(List<EligibleChild> items, ListContract.View<EligibleChild> view) {
        super(items, view);
    }

    @NonNull
    @Override
    public ListableViewHolder<EligibleChild> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_community_activity_details_fragment_item, parent, false);
        return new MyCommunityActivityDetailsViewHolder(view);
    }
}