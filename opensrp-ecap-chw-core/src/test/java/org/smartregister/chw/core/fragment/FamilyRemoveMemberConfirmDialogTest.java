package org.smartregister.chw.core.fragment;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.Robolectric;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.view.customcontrols.CustomFontTextView;

public class FamilyRemoveMemberConfirmDialogTest extends BaseUnitTest {

    private FamilyRemoveMemberConfirmDialog familyRemoveMemberConfirmDialog;

    @Spy
    private Runnable onRemove;

    @Spy
    private Runnable onRemoveActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FragmentActivity activity = Robolectric
                .buildActivity(FragmentActivity.class).create().start()
                .resume().get();

        familyRemoveMemberConfirmDialog = FamilyRemoveMemberConfirmDialog.newInstance("Confirm Closing");

        familyRemoveMemberConfirmDialog.setOnRemove(onRemove);
        familyRemoveMemberConfirmDialog.setOnRemoveActivity(onRemoveActivity);
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Fragment prev = activity.getSupportFragmentManager()
                .findFragmentByTag("FamilyRemoveMemberConfirmDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        familyRemoveMemberConfirmDialog.show(ft, "FamilyRemoveMemberConfirmDialog");
    }

    @Test
    public void testButtonClicks() {
        Assert.assertNotNull(familyRemoveMemberConfirmDialog);
        View view = familyRemoveMemberConfirmDialog.getView();
        Assert.assertNotNull(view);
        CustomFontTextView removeTextView = view.findViewById(R.id.remove);
        removeTextView.performClick();
        Mockito.verify(onRemove, Mockito.atLeastOnce()).run();
        CustomFontTextView cancelTextView = view.findViewById(R.id.cancel);
        cancelTextView.performClick();
        Mockito.verify(onRemoveActivity, Mockito.atLeastOnce()).run();
    }
}