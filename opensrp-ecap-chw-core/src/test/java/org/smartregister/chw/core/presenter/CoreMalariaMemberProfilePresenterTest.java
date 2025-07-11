package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.contract.CoreMalariaProfileContract;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;

public class CoreMalariaMemberProfilePresenterTest {

    @Mock
    private CoreMalariaProfileContract.View view;

    @Mock
    private CoreMalariaProfileContract.Interactor interactor;

    private CoreMalariaMemberProfilePresenter memberProfilePresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        memberProfilePresenter = Mockito.mock(CoreMalariaMemberProfilePresenter.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(memberProfilePresenter, "view", new WeakReference<>(view));
        ReflectionHelpers.setField(memberProfilePresenter, "interactor", interactor);
    }

    @Test
    public void createMalariaFollowUpEventInvokesInteractor() throws Exception {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        String jsonString = "{'Encounter':'Follow-up'}";
        String baseEntityId = "id-123-456";
        memberProfilePresenter.createHfMalariaFollowupEvent(allSharedPreferences, jsonString, baseEntityId);
        Mockito.verify(interactor).createHfMalariaFollowupEvent(allSharedPreferences, jsonString, baseEntityId);
    }

    @Test
    public void nonNullViewReturnedWhenPresenterInitialised() {
        Assert.assertEquals(view, memberProfilePresenter.getView());
    }
}
