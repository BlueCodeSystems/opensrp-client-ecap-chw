package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.contract.BaseChwNotificationFragmentContract;

import java.lang.ref.WeakReference;

public class BaseChwNotificationFragmentPresenterTest extends BaseUnitTest {
    @Mock
    private BaseChwNotificationFragmentContract.View view;

    private BaseChwNotificationFragmentPresenter baseChwNotificationDetailsPresenter;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        baseChwNotificationDetailsPresenter = Mockito.mock(BaseChwNotificationFragmentPresenter.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(baseChwNotificationDetailsPresenter, "viewReference", new WeakReference<>(view));
    }

    @Test
    public void baseReferralNotificationPresenterView() {
        Assert.assertEquals(view, baseChwNotificationDetailsPresenter.getView());
    }
}
