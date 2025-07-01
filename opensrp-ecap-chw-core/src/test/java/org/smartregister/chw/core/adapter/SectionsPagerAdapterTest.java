package org.smartregister.chw.core.adapter;

import android.os.Build;

import androidx.fragment.app.FragmentManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.fragment.DailyTalliesFragment;
import org.smartregister.chw.core.fragment.DraftMonthlyFragment;
import org.smartregister.chw.core.fragment.SentMonthlyFragment;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, sdk = Build.VERSION_CODES.P)
public class SectionsPagerAdapterTest {
    @Mock
    private FragmentManager fragmentManager;

    private android.content.Context context = RuntimeEnvironment.application;

    private SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(fragmentManager, context);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetItem() {
        Assert.assertTrue(sectionsPagerAdapter.getItem(0) instanceof DailyTalliesFragment);
        Assert.assertTrue(sectionsPagerAdapter.getItem(1) instanceof DraftMonthlyFragment);
        Assert.assertTrue(sectionsPagerAdapter.getItem(2) instanceof SentMonthlyFragment);
    }

    @Test
    public void testGetCount() {
        Assert.assertEquals(3, sectionsPagerAdapter.getCount());
    }

    @Test
    public void testGetPageTitle() {
        Assert.assertEquals("Daily Tallies", sectionsPagerAdapter.getPageTitle(0));
        Assert.assertEquals("Draft Monthly", sectionsPagerAdapter.getPageTitle(1));
        Assert.assertEquals("Sent Monthly", sectionsPagerAdapter.getPageTitle(2));
    }
}