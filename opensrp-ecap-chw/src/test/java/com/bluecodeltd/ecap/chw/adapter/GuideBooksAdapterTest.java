package com.bluecodeltd.ecap.chw.adapter;

import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import com.bluecodeltd.ecap.chw.BaseUnitTest;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.GuideBooksFragmentContract;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class GuideBooksAdapterTest extends BaseUnitTest {

    private GuideBooksAdapter guideBooksAdapter;

    @Mock
    private List<GuideBooksFragmentContract.RemoteFile> remoteFiles;

    @Mock
    private GuideBooksFragmentContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        guideBooksAdapter = Mockito.spy(new GuideBooksAdapter(remoteFiles, view, ChwApplication.getCounselingDocsDirectory()));
    }

    @Test
    public void testOnCreateViewHolder() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        RecyclerView.ViewHolder itemView = guideBooksAdapter.onCreateViewHolder(linearLayout, 0);
        assertNotNull(itemView);
        assertTrue(itemView instanceof GuideBooksAdapter.MyViewHolder);
    }
}