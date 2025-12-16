package com.bluecodeltd.ecap.chw.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.dao.VcaAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import com.bluecodeltd.ecap.chw.util.Threading;

import java.util.ArrayList;
import java.util.List;

public class VcaVisitsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vca_visits);

        RecyclerView recyclerView = findViewById(R.id.vca_visits_list);
        TextView empty = findViewById(R.id.empty_state);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        empty.setText("Loading…");
        empty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        Threading.io(() -> {
            List<Child> children = null;
            try { children = IndexPersonDao.getAllChildrenSubpops(); } catch (Exception ignored) { }
            ArrayList<Row> rows = new ArrayList<>();
            if (children != null) {
                for (Child c : children) {
                    try {
                        String uniqueId = c.getUnique_id();
                        String displayName = (c.getAdolescent_first_name() != null ? c.getAdolescent_first_name() : c.getFirst_name()) + " " +
                                (c.getAdolescent_last_name() != null ? c.getAdolescent_last_name() : c.getLast_name());
                        String caseStatus = null;
                        try {
                            com.bluecodeltd.ecap.chw.model.VcaScreeningModel scr = VCAScreeningDao.getVcaScreening(uniqueId);
                            caseStatus = scr != null ? scr.getCase_status() : null;
                        } catch (Exception ignored) {}
                        if (caseStatus != null && ("0".equals(caseStatus) || "2".equals(caseStatus))) continue;

                        VcaVisitationModel visit = null;
                        try { visit = VcaVisitationDao.getVcaVisitationNotification(uniqueId); } catch (Exception ignored) {}
                        String color = null;
                        String date = null;
                        if (visit != null) {
                            color = visit.getStatus_color();
                            date = visit.getVisit_date();
                        } else {
                            VcaAssessmentModel assess = null;
                            try { assess = VcaAssessmentDao.getVcaVisitationNotificationFromAssessment(uniqueId); } catch (Exception ignored) {}
                            if (assess != null) {
                                color = assess.getStatus_color();
                                date = assess.getDate_edited();
                            }
                        }
                        if (color != null && date != null) {
                            rows.add(new Row(displayName, uniqueId, color, date));
                        }
                    } catch (Exception ignored) {}
                }
            }

            final ArrayList<Row> finalRows = rows;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (finalRows.isEmpty()) {
                    empty.setText("No visits due.");
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new Adapter(finalRows));
                }
            });
        });
    }

    static class Row {
        final String name;
        final String id;
        final String color;
        final String date;
        Row(String name, String id, String color, String date) {
            this.name = name; this.id = id; this.color = color; this.date = date;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView name, id, status;
        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.vca_name);
            id = v.findViewById(R.id.vca_id);
            status = v.findViewById(R.id.vca_status);
        }
    }

    static class Adapter extends RecyclerView.Adapter<Holder> {
        final ArrayList<Row> items;
        Adapter(ArrayList<Row> items) { this.items = items; }
        @Override public Holder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vca_visit, parent, false);
            return new Holder(v);
        }
        @Override public void onBindViewHolder(Holder h, int i) {
            Row r = items.get(i);
            h.name.setText(r.name);
            h.id.setText(r.id);
            String label = ("red".equalsIgnoreCase(r.color) ? "Visit Overdue: " : "Visit Due: ") + r.date;
            h.status.setText(label);
            int color = Color.DKGRAY;
            if ("green".equalsIgnoreCase(r.color)) color = Color.parseColor("#2e7d32");
            else if ("yellow".equalsIgnoreCase(r.color)) color = Color.parseColor("#f9a825");
            else if ("red".equalsIgnoreCase(r.color)) color = Color.parseColor("#c62828");
            h.status.setTextColor(color);
        }
        @Override public int getItemCount() { return items.size(); }
    }
}
