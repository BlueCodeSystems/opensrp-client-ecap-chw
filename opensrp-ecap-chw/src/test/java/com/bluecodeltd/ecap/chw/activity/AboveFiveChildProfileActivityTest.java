package com.bluecodeltd.ecap.chw.activity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import com.bluecodeltd.ecap.chw.BaseActivityTest;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.helper.ImageRenderHelper;

import de.hdodenhof.circleimageview.CircleImageView;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class AboveFiveChildProfileActivityTest extends BaseActivityTest<AboveFiveChildProfileActivity> {

    @Mock
    private RelativeLayout layoutLastVisitRow;
    @Mock
    private View viewLastVisitRow;
    @Mock
    private ImageRenderHelper imageRenderHelper;


    @Override
    protected Class<AboveFiveChildProfileActivity> getActivityClass() {
        return AboveFiveChildProfileActivity.class;
    }

    @Test
    public void setProfileImageBorderTest() {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(getActivity());
        CircleImageView imageView = Mockito.spy(new CircleImageView(RuntimeEnvironment.application));
        ReflectionHelpers.setField(spyActivity, "imageViewProfile", imageView);
        ReflectionHelpers.setField(spyActivity, "imageRenderHelper", imageRenderHelper);
        spyActivity.setProfileImage("1234");
        Assert.assertEquals(0, imageView.getBorderWidth());

    }

    @Test
    public void setParentNameViewGone() {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(getActivity());
        TextView textView = Mockito.spy(new TextView(RuntimeEnvironment.application));
        ReflectionHelpers.setField(spyActivity, "textViewParentName", textView);
        spyActivity.setParentName("sdfs");
        Assert.assertEquals(View.GONE, textView.getVisibility());
    }

    @Test
    public void setLastVisitRowGone() {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(getActivity());
        TextView textViewLastVisit = Mockito.spy(new TextView(RuntimeEnvironment.application));
        TextView textViewMedicalHistory = Mockito.spy(new TextView(RuntimeEnvironment.application));
        ReflectionHelpers.setField(spyActivity, "layoutLastVisitRow", layoutLastVisitRow);
        ReflectionHelpers.setField(spyActivity, "viewLastVisitRow", viewLastVisitRow);
        ReflectionHelpers.setField(spyActivity, "textViewLastVisit", textViewLastVisit);
        ReflectionHelpers.setField(spyActivity, "textViewMedicalHistory", textViewMedicalHistory);
        spyActivity.setLastVisitRowView("10");
        Assert.assertEquals(View.GONE, textViewLastVisit.getVisibility());
    }

    @Test
    public void goneRecordVisitPanel() throws Exception {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(getActivity());
        View recordVisitPanel = Mockito.spy(new View(RuntimeEnvironment.application));
        ReflectionHelpers.setField(spyActivity, "recordVisitPanel", recordVisitPanel);
        ReflectionHelpers.callInstanceMethod(spyActivity, "invisibleRecordVisitPanel");
        Assert.assertEquals(View.GONE, recordVisitPanel.getVisibility());
    }

}
