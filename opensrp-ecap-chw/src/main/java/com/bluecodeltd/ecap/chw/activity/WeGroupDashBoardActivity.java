package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ViewPagerAdapterFragment;
import com.bluecodeltd.ecap.chw.dao.WeGroupDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.fragment.ConstituitionFragment;
import com.bluecodeltd.ecap.chw.fragment.GroupsFragment;
import com.bluecodeltd.ecap.chw.fragment.MembersFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.smartregister.chw.core.custom_views.NavigationMenu;

import java.util.ArrayList;
import java.util.List;

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_toolbar,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String role = preferences.getString("role_id", "");
//
////        if (id == R.id.action_upload_constitution) {
////            if (!role.equals(Constants.ORDINARY_MEMBER_ROLE)) {
////                Intent intent3 = new Intent(this, PdfActivity.class);
////                startActivity(intent3);
////                return true;
////            }
////        }
////
////        if (id == R.id.action_view_constitution) {
////            Intent intent = new Intent(this, PdfDownloadActivity.class);
////            startActivity(intent);
////            return true;
////        }
////
////        // TODO Add actions for the tool bar
////        if (id == R.id.action_logout) {
//////            dialogLogout();
////            return true;
////        }
////
////        if(id == R.id.action_services){
////            Intent intent = new Intent(getApplicationContext(), NewMemberActivity.class);;
////            startActivity(intent);
////            finish();
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
}