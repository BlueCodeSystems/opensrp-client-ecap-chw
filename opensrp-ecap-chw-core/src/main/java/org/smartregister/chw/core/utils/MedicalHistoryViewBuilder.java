package org.smartregister.chw.core.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.adapter.MedicalHistoryAdapter;
import org.smartregister.chw.core.domain.MedicalHistory;

import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryViewBuilder {
    @LayoutRes
    protected int rootLayout = R.layout.medical_history_nested;
    protected String title = null;
    protected List<MedicalHistory> histories = new ArrayList<>();
    protected boolean hasSeparator = true;
    @LayoutRes
    protected int childLayout = R.layout.medical_history_nested_sub_item;
    protected LayoutInflater inflater;
    protected Context context;
    protected MedicalHistoryAdapter adapter;

    public MedicalHistoryViewBuilder(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        this.context = context;
    }

    public MedicalHistoryViewBuilder withRootLayout(int rootLayout) {
        this.rootLayout = rootLayout;
        return this;
    }

    public MedicalHistoryViewBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public MedicalHistoryViewBuilder withHistory(List<MedicalHistory> histories) {
        this.histories = histories;
        return this;
    }

    public MedicalHistoryViewBuilder withSeparator(boolean hasSeparator) {
        this.hasSeparator = hasSeparator;
        return this;
    }

    public MedicalHistoryViewBuilder withChildLayout(int childLayout) {
        this.childLayout = childLayout;
        return this;
    }

    public MedicalHistoryViewBuilder withAdapter(MedicalHistoryAdapter medicalHistoryAdapter) {
        this.adapter = medicalHistoryAdapter;
        return this;
    }

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
            adapter = new MedicalHistoryAdapter(histories, childLayout);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        if (hasSeparator)
            recyclerView.addItemDecoration(new CustomDividerItemDecoration(ContextCompat.getDrawable(context, R.drawable.divider)));

        return view;
    }
}
