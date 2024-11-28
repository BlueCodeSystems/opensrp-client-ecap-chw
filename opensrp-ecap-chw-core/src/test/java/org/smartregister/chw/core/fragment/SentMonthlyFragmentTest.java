package org.smartregister.chw.core.fragment;

import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.domain.MonthlyTally;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SentMonthlyFragmentTest extends BaseUnitTest {

    private SentMonthlyFragment sentMonthlyFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FragmentActivity activity = Robolectric
                .buildActivity(FragmentActivity.class).create().start()
                .resume().get();

        sentMonthlyFragment = SentMonthlyFragment.newInstance();
        HashMap<String, ArrayList<MonthlyTally>> monthlyTallies = new HashMap<String, ArrayList<MonthlyTally>>() {
            {
                ArrayList<MonthlyTally> juneTally = new ArrayList<>();
                MonthlyTally june = new MonthlyTally();
                june.setCreatedAt(new Date());
                juneTally.add(june);
                put("June", juneTally);
            }
        };
        sentMonthlyFragment.setSentMonthlyTallies(monthlyTallies);

        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        Fragment prev = activity.getSupportFragmentManager()
                .findFragmentByTag("SentMonthlyFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.add(sentMonthlyFragment, "SentMonthlyFragment");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Test
    public void testExpandableListNotNull() {
        Assert.assertNotNull(sentMonthlyFragment);
        Assert.assertNotNull(sentMonthlyFragment.getExpandableListView());
    }

    @Test
    public void testUpdateExpandedList() {
        ExpandableListView expandableListView = Mockito.mock(ExpandableListView.class);
        sentMonthlyFragment = Mockito.spy(sentMonthlyFragment);
        HashMap<String, ArrayList<MonthlyTally>> monthlyTallies = new HashMap<String, ArrayList<MonthlyTally>>() {
            {
                ArrayList<MonthlyTally> juneTally = new ArrayList<>();
                MonthlyTally june = new MonthlyTally();
                june.setCreatedAt(new Date());
                june.setMonth(new Date());
                june.setDateSent(new Date());
                juneTally.add(june);
                put("June", juneTally);
            }
        };
        sentMonthlyFragment.setSentMonthlyTallies(monthlyTallies);

        Whitebox.setInternalState(sentMonthlyFragment, "expandableListView", expandableListView);
        sentMonthlyFragment.updateExpandedList();

        Mockito.verify(expandableListView).setAdapter(Mockito.any(ExpandableListAdapter.class));
    }
}