package com.bluecodeltd.ecap.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.koin.test.AutoCloseKoinTest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.GuideBooksFragmentContract;

import java.util.ArrayList;
import java.util.List;

public class GuideBooksFragmentPresenterTest extends AutoCloseKoinTest {

    @Mock
    private GuideBooksFragmentContract.Interactor interactor;

    @Mock
    private GuideBooksFragmentContract.View view;

    private GuideBooksFragmentPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new GuideBooksFragmentPresenter(view, interactor);
    }


    @Test
    public void testInitialize() {
        presenter.initialize("files.json", ChwApplication.getGuideBooksDirectory());
        Mockito.verify(interactor).getFiles(null, "files.json", ChwApplication.getGuideBooksDirectory(),presenter);
    }

    @Test
    public void testGetView() {
        Assert.assertEquals(presenter.getView(), view);
    }

    @Test
    public void testOnDataFetched() {
        List<GuideBooksFragmentContract.RemoteFile> videos = new ArrayList<>();
        presenter.onDataFetched(videos);
        Mockito.verify(view).onDataReceived(videos);
    }
}
