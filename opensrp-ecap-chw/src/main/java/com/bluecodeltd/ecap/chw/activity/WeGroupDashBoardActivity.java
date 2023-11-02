package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ViewPagerAdapterFragment;
import com.bluecodeltd.ecap.chw.dao.WeGroupDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.ConstituitionFragment;
import com.bluecodeltd.ecap.chw.fragment.GroupsFragment;
import com.bluecodeltd.ecap.chw.fragment.MembersFragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class WeGroupDashBoardActivity extends AppCompatActivity {
    private ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton addMember;
    TextView groupTabCount;
    private Toolbar toolbar;
    private AppBarLayout myAppbar;
    String username,password;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_group_dash_board);

        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        SharedPreferences sharedPreferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();

        addMember = findViewById(R.id.fab);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = findViewById(R.id.collapsing_toolbar_appbarlayout);
        NavigationMenu.getInstance(this, null, toolbar);


        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        returnViewPager();
        updateGroupTabTitle();
        updateMemberTabTitle();

    }
    public  void returnViewPager(){
        List<Fragment> fragments = new ArrayList<>();

        fragments.add(new GroupsFragment());

        MembersFragment membersFragment = new MembersFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("password", password);
        membersFragment.setArguments(args);
        fragments.add(membersFragment);

//        fragments.add(new ConstituitionFragment());

        ViewPagerAdapterFragment adapter = new ViewPagerAdapterFragment(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Groups");
        tabLayout.getTabAt(1).setText("Members");
//        tabLayout.getTabAt(2).setText("Constitution");

    }
    private void updateGroupTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.update_tab_layout, null);
        TextView groupTabTitle = taskTabTitleLayout.findViewById(R.id.tab_title);
        groupTabCount = taskTabTitleLayout.findViewById(R.id.tab_count);
        groupTabTitle.setText("Groups");

        int count = WeGroupDao.groupCount();

        groupTabCount.setText(String.valueOf(count));
        tabLayout.getTabAt(0).setCustomView(taskTabTitleLayout);
    }
    private void updateMemberTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.update_tab_layout, null);
        TextView groupTabTitle = taskTabTitleLayout.findViewById(R.id.tab_title);
        groupTabCount = taskTabTitleLayout.findViewById(R.id.tab_count);
        groupTabTitle.setText("Members");

        int count = WeGroupMembersDao.getMembersCount();

        groupTabCount.setText(String.valueOf(count));
        tabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.we_group_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                finish();
                startActivity(getIntent());
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}