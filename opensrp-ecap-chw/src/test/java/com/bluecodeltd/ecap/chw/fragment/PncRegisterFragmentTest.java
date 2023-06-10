package com.bluecodeltd.ecap.chw.fragment;

import androidx.fragment.app.FragmentActivity;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.koin.test.AutoCloseKoinTest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class PncRegisterFragmentTest extends AutoCloseKoinTest {

    @Mock
    private FragmentActivity activity;

    private PncRegisterFragment pncRegisterFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PncRegisterFragment objct = new PncRegisterFragment();
        pncRegisterFragment = Mockito.spy(objct);
    }


    @Test
    public void testInitializePresenter() {
        Mockito.doReturn(activity).when(pncRegisterFragment).getActivity();

        pncRegisterFragment.initializePresenter();
        TestCase.assertNotNull(pncRegisterFragment.presenter());
    }


}


