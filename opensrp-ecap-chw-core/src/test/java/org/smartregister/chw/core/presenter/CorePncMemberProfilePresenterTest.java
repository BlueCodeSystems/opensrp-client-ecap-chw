package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.contract.CorePncMemberProfileContract;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;

public class CorePncMemberProfilePresenterTest {

    @Mock
    private CorePncMemberProfileContract.View view;

    @Mock
    private CorePncMemberProfileContract.Interactor interactor;

    private CorePncMemberProfilePresenter pncMemberProfilePresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pncMemberProfilePresenter = Mockito.mock(CorePncMemberProfilePresenter.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(pncMemberProfilePresenter, "view", new WeakReference<>(view));
        ReflectionHelpers.setField(pncMemberProfilePresenter, "interactor", interactor);
    }

    @Test
    public void createPncDangerSignsEventInvokesInteractor() throws Exception {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        String jsonString = "{'Encounter':'PNC-Follow-up'}";
        String baseEntityId = "id-123-456";
        pncMemberProfilePresenter.createPncDangerSignsOutcomeEvent(allSharedPreferences, jsonString, baseEntityId);
        Mockito.verify(interactor).createPncDangerSignsOutcomeEvent(allSharedPreferences, jsonString, baseEntityId);
    }

    @Test
    public void nonNullViewReturnedWhenPresenterInitialised() {
        Assert.assertEquals(view, pncMemberProfilePresenter.getView());
    }
}
