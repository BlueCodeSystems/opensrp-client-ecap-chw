package org.smartregister.chw.core.presenter;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.activity.ServiceJsonFormActivity;

public class ServiceJsonFormFragmentPresenterTest {

    private ServiceJsonFormFragmentPresenter serviceJsonFormFragmentPresenter;

    @Spy
    private JsonFormFragment formFragment;

    @Spy
    private JsonFormInteractor formInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setField(formFragment, "mJsonApi", Mockito.spy(ServiceJsonFormActivity.class));
        serviceJsonFormFragmentPresenter = Mockito.spy(new ServiceJsonFormFragmentPresenter(formFragment, formInteractor));
    }

    @Test
    public void testMoveToNextWizardStepShouldReturnFalse() {
        Assert.assertFalse(serviceJsonFormFragmentPresenter.moveToNextWizardStep());
    }
}