package org.smartregister.chw.core.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.domain.MedicalHistory;

import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryViewBuilderTest extends BaseUnitTest {

    private Context context = RuntimeEnvironment.application.getApplicationContext();

    @Mock
    private LayoutInflater inflater;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHistoryBuildConstructs() {

        int rootLayout = 1234;
        int childLayout = 35345;
        String title = "My Title";
        List<MedicalHistory> histories = new ArrayList<>();

        View view = Mockito.mock(View.class);
        Mockito.doReturn(view).when(inflater).inflate(rootLayout, null);

        TextView tvTitle = Mockito.mock(TextView.class);
        Mockito.doReturn(tvTitle).when(view).findViewById(R.id.tvTitle);

        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Mockito.doReturn(recyclerView).when(view).findViewById(R.id.recyclerView);

        View sampleView = new MedicalHistoryViewBuilder(inflater, context)
                .withRootLayout(rootLayout)
                .withTitle(title)
                .withHistory(histories)
                .withSeparator(true)
                .withChildLayout(childLayout)
                .build();

        Mockito.verify(tvTitle).setText(title);
        Mockito.verify(tvTitle).setAllCaps(true);

        Assert.assertNotNull(sampleView);

    }
}
