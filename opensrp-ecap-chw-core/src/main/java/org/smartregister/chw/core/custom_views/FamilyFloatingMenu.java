package org.smartregister.chw.core.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;

public class FamilyFloatingMenu extends LinearLayout implements View.OnClickListener, BottomSheetMenu {
    private FloatingActionButton fab;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private OnClickFloatingMenu onClickFloatingMenu;

    private View callLayout;
    private View addNewMember;
    private View assessment;

    public FamilyFloatingMenu(Context context) {
        super(context);
        initUi();
    }

    public FamilyFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public FamilyFloatingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_family_floating_menu, this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> animateFAB());
        }
        setupBottomSheet();
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
        }
    }

    public void reDraw(boolean has_phone) {
        TextView callTextView = bottomSheetView != null ? bottomSheetView.findViewById(R.id.CallTextView) : null;
        TextView callTextViewHint = bottomSheetView != null ? bottomSheetView.findViewById(R.id.CallTextViewHint) : null;

        if (callTextView == null || callTextViewHint == null || callLayout == null) {
            return;
        }

        callTextViewHint.setVisibility(has_phone ? GONE : VISIBLE);
        callLayout.setOnClickListener(has_phone ? this : null);
        callTextView.setTypeface(null, has_phone ? Typeface.NORMAL : Typeface.ITALIC);
        callTextView.setTextColor(getResources().getColor(has_phone ? android.R.color.black : org.smartregister.chw.opensrp_chw_anc.R.color.grey));

        View callIconView = bottomSheetView != null ? bottomSheetView.findViewById(R.id.callFab) : null;
        if (callIconView instanceof FloatingActionButton) {
            FloatingActionButton callFab = (FloatingActionButton) callIconView;
            if (callFab.getDrawable() != null) {
                callFab.getDrawable().setAlpha(has_phone ? 255 : 122);
            }
        } else if (callIconView instanceof android.widget.ImageView) {
            android.widget.ImageView callIcon = (android.widget.ImageView) callIconView;
            callIcon.setImageAlpha(has_phone ? 255 : 122);
        }
    }

    public void setClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
    }

    @Override
    public void onClick(View v) {
        if (onClickFloatingMenu != null) {
            onClickFloatingMenu.onClickMenu(v.getId());
        }
        dismissMenu();
    }

    private void setupBottomSheet() {
        if (bottomSheetDialog != null) {
            return;
        }
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.ChwBottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.menu_family_floating_menu, null);
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetView = sheetView;

        callLayout = sheetView.findViewById(R.id.call_layout);
        addNewMember = sheetView.findViewById(R.id.add_new_member_layout);
        assessment = sheetView.findViewById(R.id.vunarability_assessment_layout);

        if (callLayout != null) {
            callLayout.setOnClickListener(this);
        }
        if (addNewMember != null) {
            addNewMember.setOnClickListener(this);
        }
        if (assessment != null) {
            assessment.setOnClickListener(this);
        }

        bottomSheetDialog.setOnShowListener(dialog -> {
            if (fab != null) {
                fab.setImageResource(R.drawable.ic_input_add);
            }
        });
        bottomSheetDialog.setOnDismissListener(dialog -> {
            if (fab != null) {
                fab.setImageResource(R.drawable.ic_edit_white);
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
