package org.smartregister.chw.core.custom_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.malaria.custom_views.BaseMalariaFloatingMenu;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.fragment.BaseMalariaCallDialogFragment;

import static org.smartregister.chw.core.utils.Utils.redrawWithOption;

public abstract class CoreMalariaFloatingMenu extends BaseMalariaFloatingMenu implements BottomSheetMenu {
    public FloatingActionButton fab;
    protected View referLayout;
    protected View callLayout;
    protected BottomSheetDialog bottomSheetDialog;
    protected View bottomSheetView;
    protected boolean isFabMenuOpen = false;
    protected OnClickFloatingMenu onClickFloatingMenu;

    private final MemberObject memberObject;

    public CoreMalariaFloatingMenu(Context context, MemberObject memberObject) {
        super(context, memberObject);
        this.memberObject = memberObject;
    }

    public void setFloatMenuClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
    }

    @Override
    protected void initUi() {
        inflate(getContext(), R.layout.view_malaria_floating_menu, this);
        fab = (FloatingActionButton) findViewById(R.id.malaria_fab);
        if (fab != null) {
            fab.setOnClickListener(v -> animateFAB());
        }
        setupBottomSheet();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.malaria_fab) {
            animateFAB();
            return;
        }

        if (onClickFloatingMenu != null) {
            onClickFloatingMenu.onClickMenu(view.getId());
        }
        dismissMenu();
    }

    public void animateFAB() {
        if (bottomSheetDialog == null) {
            setupBottomSheet();
        }
        if (bottomSheetDialog == null) {
            return;
        }

        if (bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        } else {
            bottomSheetDialog.show();
            isFabMenuOpen = true;
        }
    }

    public void launchCallWidget() {
        FragmentActivity activity = (FragmentActivity) getContext();
        BaseMalariaCallDialogFragment.launchDialog(activity, memberObject);
    }

    public void redraw(boolean hasPhoneNumber) {
        redrawWithOption(this, hasPhoneNumber);
    }

    public View getCallLayout() {
        return callLayout;
    }

    private void setupBottomSheet() {
        if (bottomSheetDialog != null) {
            return;
        }
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.ChwBottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.menu_call_refer_bottom_sheet, null);
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetView = sheetView;

        callLayout = sheetView.findViewById(R.id.call_layout);
        referLayout = sheetView.findViewById(R.id.refer_to_facility_layout);

        if (callLayout != null) {
            callLayout.setOnClickListener(this);
        }
        if (referLayout != null) {
            referLayout.setOnClickListener(this);
        }

        bottomSheetDialog.setOnShowListener(dialog -> {
            isFabMenuOpen = true;
            if (fab != null) {
                fab.setImageResource(R.drawable.ic_input_add);
            }
        });

        bottomSheetDialog.setOnDismissListener(dialog -> {
            isFabMenuOpen = false;
            if (fab != null) {
                fab.setImageResource(com.vijay.jsonwizard.R.drawable.ic_edit_white);
            }
        });
    }

    private void dismissMenu() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

    @Override
    public View getBottomSheetView() {
        return bottomSheetView;
    }
}
