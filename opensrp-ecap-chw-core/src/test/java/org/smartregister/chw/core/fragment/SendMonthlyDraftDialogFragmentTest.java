package org.smartregister.chw.core.fragment;

import android.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;

public class SendMonthlyDraftDialogFragmentTest extends BaseUnitTest {

    private SendMonthlyDraftDialogFragment sendMonthlyDraftDialogFragment;

    @Before
    public void setUp() {
        View.OnClickListener onSendClickedListener = Mockito.spy(View.OnClickListener.class);
        sendMonthlyDraftDialogFragment = SendMonthlyDraftDialogFragment.newInstance("Feb", "2019-06-07", onSendClickedListener);
        AppCompatActivity activity = Robolectric
                .buildActivity(AppCompatActivity.class).create().start()
                .resume().get();

        FragmentTransaction ft = activity.getFragmentManager()
                .beginTransaction();
        android.app.Fragment prev = activity.getFragmentManager()
                .findFragmentByTag("SendMonthlyDraftDialogFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        sendMonthlyDraftDialogFragment.show(ft, "SendMonthlyDraftDialogFragment");
    }

    @Test
    public void shouldInitializeFragment() {
        Assert.assertNotNull(sendMonthlyDraftDialogFragment);
        View view = sendMonthlyDraftDialogFragment.getView();
        Assert.assertNotNull(view);
        Button buttonSend = view.findViewById(R.id.button_send);
        buttonSend.performClick();
        Assert.assertFalse(sendMonthlyDraftDialogFragment.isVisible());
    }
}