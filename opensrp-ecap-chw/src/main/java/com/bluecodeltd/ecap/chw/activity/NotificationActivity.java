package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.HouseholdServiceAdapter;
import com.bluecodeltd.ecap.chw.adapter.NotificationsAdapter;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.model.FamilyServiceModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;

import org.smartregister.chw.core.custom_views.NavigationMenu;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<VcaVisitationModel> notificationsList = new ArrayList<>();
    private LinearLayout linearLayout;


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Final HIV Status Visits");
        NavigationMenu.getInstance(this, null, toolbar);

        recyclerView = findViewById(R.id.myrecyclerView);
        linearLayout = findViewById(R.id.service_container);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(NotificationActivity.this);
        String phone = sp.getString("phone", "anonymous");

        notificationsList.addAll(VcaVisitationDao.getVisitsByCaseWorkerPhone(phone));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(NotificationActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new NotificationsAdapter(notificationsList, NotificationActivity.this);
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();

        if (recyclerViewadapter.getItemCount() > 0){

            linearLayout.setVisibility(View.GONE);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();
    }
}