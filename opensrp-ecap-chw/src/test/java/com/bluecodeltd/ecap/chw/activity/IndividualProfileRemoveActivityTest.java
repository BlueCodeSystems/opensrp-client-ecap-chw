package com.bluecodeltd.ecap.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.bluecodeltd.ecap.chw.fragment.IndividualProfileRemoveFragment;
import com.bluecodeltd.ecap.chw.util.Constants;

public class IndividualProfileRemoveActivityTest {
    @Mock
    private IndividualProfileRemoveActivity individualProfileRemoveActivity;

    @Mock
    private IndividualProfileRemoveFragment individualProfileRemoveFragment;

    @Mock
    private JSONObject jsonObject;

    @Mock
    private Intent intent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnActivityResultWithResultCanceled() {
        Mockito.doNothing().when(individualProfileRemoveFragment).confirmRemove(jsonObject);
        individualProfileRemoveActivity.onActivityResult(Constants.ProfileActivityResults.CHANGE_COMPLETED, Activity.RESULT_CANCELED, intent);
        Mockito.verify(individualProfileRemoveFragment, Mockito.never()).confirmRemove(jsonObject);
    }

    @Test
    public void testOnActivityResultWithResultFirstUser() {
        Mockito.doNothing().when(individualProfileRemoveFragment).confirmRemove(jsonObject);
        individualProfileRemoveActivity.onActivityResult(Constants.ProfileActivityResults.CHANGE_COMPLETED, Activity.RESULT_FIRST_USER, intent);
        Mockito.verify(individualProfileRemoveFragment, Mockito.never()).confirmRemove(jsonObject);
    }
}
