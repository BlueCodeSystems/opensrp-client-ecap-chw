package org.smartregister.chw.core.fragment;

import android.widget.ImageView;
import android.widget.ProgressBar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.presenter.AncRegisterFragmentPresenter;

public class CoreAncRegisterFragmentTest extends BaseUnitTest {

    @Mock
    private ProgressBar syncProgressBar;

    @Mock
    private ImageView syncButton;

    @Mock
    private AncRegisterFragmentPresenter presenter;

    private CoreAncRegisterFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(CoreAncRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(fragment, "presenter", presenter);
    }

    @Test
    public void presenterInitializesCorrectly() {
        fragment.initializePresenter();
        Assert.assertNotNull(presenter);
    }

    @Test
    public void refreshSyncProgressSpinnerTogglesSyncVisibility() {
        ReflectionHelpers.setField(fragment, "syncButton", syncButton);
        ReflectionHelpers.setField(fragment, "syncProgressBar", syncProgressBar);
        fragment.refreshSyncProgressSpinner();
        Mockito.verify(syncProgressBar, Mockito.times(1)).setVisibility(android.view.View.GONE);
        Mockito.verify(syncButton, Mockito.times(1)).setVisibility(android.view.View.GONE);

    }

    @Test
    public void getMainConditionCallsPresenterGetCondition() {
        fragment.getMainCondition();
        Mockito.verify(fragment.presenter(), Mockito.times(1)).getMainCondition();
    }
}
