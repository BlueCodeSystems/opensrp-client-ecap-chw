package com.bluecodeltd.ecap.chw.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;

import com.bluecodeltd.ecap.chw.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.function.BooleanSupplier;

import timber.log.Timber;

/**
 * Helper for building consistent bottom-sheet menus used across profile detail screens.
 */
public final class BottomSheetActionHelper {

    private BottomSheetActionHelper() {
        // Utility class
    }

    @FunctionalInterface
    public interface ActionHandler {
        void onAction(View view) throws Exception;
    }

    public static final class ActionItem {
        public final int viewId;
        @StringRes
        public final int labelRes;
        @DrawableRes
        public final Integer iconRes;
        public final BooleanSupplier visibilitySupplier;

        public ActionItem(int viewId, @StringRes int labelRes, @DrawableRes Integer iconRes) {
            this(viewId, labelRes, iconRes, null);
        }

        public ActionItem(int viewId, @StringRes int labelRes, @DrawableRes Integer iconRes, BooleanSupplier visibilitySupplier) {
            this.viewId = viewId;
            this.labelRes = labelRes;
            this.iconRes = iconRes;
            this.visibilitySupplier = visibilitySupplier;
        }
    }

    public static BottomSheetDialog build(Context context, List<ActionItem> items, ActionHandler handler) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View content = inflater.inflate(R.layout.menu_generic_fab, null);
        LinearLayout container = content.findViewById(R.id.action_container);
        TextView titleView = content.findViewById(R.id.menu_title);

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.ChwBottomSheetDialogTheme);
        dialog.setContentView(content);

        for (ActionItem item : items) {
            if (item.visibilitySupplier != null && !item.visibilitySupplier.getAsBoolean()) {
                continue;
            }
            AppCompatButton button = (AppCompatButton) inflater.inflate(R.layout.menu_generic_action, container, false);
            button.setId(item.viewId);
            if (item.labelRes != 0) {
                button.setText(item.labelRes);
            }
            if (item.iconRes != null) {
                button.setCompoundDrawablesWithIntrinsicBounds(
                        AppCompatResources.getDrawable(context, item.iconRes),
                        null,
                        null,
                        null
                );
            }
            button.setOnClickListener(v -> {
                dialog.dismiss();
                try {
                    handler.onAction(v);
                } catch (Exception e) {
                    Timber.e(e, "Bottom sheet action failed for view id %s", context.getResources().getResourceEntryName(v.getId()));
                }
            });
            container.addView(button);
        }

        if (container.getChildCount() == 0) {
            titleView.setVisibility(View.GONE);
        }

        return dialog;
    }
}
