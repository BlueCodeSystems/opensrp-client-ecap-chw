package org.smartregister.chw.core.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.domain.MedicalHistory;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class MedicalHistoryAdapterTest {
    private MedicalHistoryAdapter medicalHistoryAdapter;
    private List<MedicalHistory> medicalHistoryList;
    private Integer layoutId = 12345;
    @Mock
    private MedicalHistoryAdapter.MyViewHolder myViewHolder;
    @Mock
    private LayoutInflater inflater;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setText("Done Today");
        medicalHistory.setTitle("Home Visit");
        medicalHistory.setText(Arrays.asList("1", "2", "3"));
        medicalHistory.setText(Arrays.asList("1", "2", "3"));

        MedicalHistory medicalHistory2 = new MedicalHistory();
        medicalHistory2.setText("Done Today");
        medicalHistory2.setTitle("Home Visit");
        medicalHistoryList = Arrays.asList(medicalHistory, medicalHistory2);

        medicalHistoryAdapter = new MedicalHistoryAdapter(medicalHistoryList, layoutId);
    }

    @Test
    public void onCreateViewHolder() {
        Assert.assertNotNull(myViewHolder);
    }

    @Test
    public void testOnBindViewHolder() {
        TextView tvSubTitle = Mockito.mock(TextView.class);
        LinearLayout llItems = Mockito.mock(LinearLayout.class);

        ReflectionHelpers.setField(myViewHolder, "tvSubTitle", tvSubTitle);
        ReflectionHelpers.setField(myViewHolder, "llItems", llItems);

        // intercept contextual objects and provide mocks
        TextView tvContent = Mockito.mock(TextView.class);
        View view = Mockito.mock(View.class);
        Mockito.doReturn(tvContent).when(view).findViewById(Mockito.anyInt());
        Mockito.doReturn(view).when(inflater).inflate(Mockito.anyInt(), Mockito.any());

        // add state
        ReflectionHelpers.setField(medicalHistoryAdapter, "items", medicalHistoryList);
        ReflectionHelpers.setField(medicalHistoryAdapter, "inflater", inflater);


        medicalHistoryAdapter.onBindViewHolder(myViewHolder, 0);

        Mockito.verify(tvSubTitle).setText("Home Visit");
        Mockito.verify(tvSubTitle).setVisibility(View.VISIBLE);
        Mockito.verify(llItems, Mockito.times(3)).addView(Mockito.any(View.class));
    }

    @Test
    public void testGetItemCount() {
        medicalHistoryAdapter.getItemCount();
        Assert.assertEquals(2, medicalHistoryList.size());
    }
}
