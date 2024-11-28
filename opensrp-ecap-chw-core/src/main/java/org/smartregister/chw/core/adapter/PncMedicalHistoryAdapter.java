package org.smartregister.chw.core.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.domain.MedicalHistory;

import java.util.List;

public class PncMedicalHistoryAdapter extends MedicalHistoryAdapter {


    public PncMedicalHistoryAdapter(List<MedicalHistory> items, @LayoutRes int layoutID) {
        super(items, layoutID);
    }

    @NonNull
    @Override
    public MedicalHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.pnc_child_medical_history_nested_item, parent, false);
        return new MedicalHistoryAdapter.MyViewHolder(view);
    }
}
