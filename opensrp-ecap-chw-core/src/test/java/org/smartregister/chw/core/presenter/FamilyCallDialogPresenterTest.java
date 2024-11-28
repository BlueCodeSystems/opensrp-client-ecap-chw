package org.smartregister.chw.core.presenter;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.contract.FamilyCallDialogContract;

import java.lang.ref.WeakReference;


public class FamilyCallDialogPresenterTest {

    @Mock
    private FamilyCallDialogContract.Interactor mInteractor;

    @Mock
    private FamilyCallDialogContract.View view;

    @Mock
    private FamilyCallDialogContract.Model model;

    private FamilyCallDialogPresenter presenter;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = Mockito.mock(FamilyCallDialogPresenter.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(presenter, "mView", new WeakReference<>(view));
        ReflectionHelpers.setField(presenter, "mInteractor", mInteractor);
        initalize();
    }

    @Test
    public void updateHeadOfFamily() {
        presenter.updateHeadOfFamily(model);
        Mockito.verify(view).refreshHeadOfFamilyView(model);
    }

    @Test
    public void updateCareGiver() {
        presenter.updateCareGiver(model);
        Mockito.verify(view).refreshCareGiverView(model);
    }

    @Test
    public void initalize() {
        presenter.updateHeadOfFamily(null);
        presenter.updateCareGiver(null);
        Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(view).getCurrentContext();
        presenter.initalize();

        Mockito.verify(mInteractor).getHeadOfFamily(presenter, context);
    }
}