package org.smartregister.chw.core.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.adapter.PncMedicalHistoryAdapter;

public class PncMedicalHistoryViewBuilder extends MedicalHistoryViewBuilder {


    public PncMedicalHistoryViewBuilder(LayoutInflater inflater, Context context) {
        super(inflater, context);
    }

    @Override
    public View build() {
        View view = inflater.inflate(rootLayout, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        if (StringUtils.isBlank(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setAllCaps(true);
        }

        if (adapter == null) {
            adapter = new PncMedicalHistoryAdapter(histories, childLayout);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        if (hasSeparator)
            recyclerView.addItemDecoration(new CustomDividerItemDecoration(ContextCompat.getDrawable(context, R.drawable.light_divider)));

        return view;
    }
}
