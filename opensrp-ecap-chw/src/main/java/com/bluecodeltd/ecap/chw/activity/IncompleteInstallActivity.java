package com.bluecodeltd.ecap.chw.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;

/**
 * Shown instead of every screen when the install has no libsqlcipher.so (see
 * {@link com.bluecodeltd.ecap.chw.util.IncompleteInstall}). A plain framework Activity with its
 * layout built in code, so it runs without the app's libraries or initialisation.
 */
public class IncompleteInstallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Created in place of another Activity, so it inherits that Activity's manifest theme.
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        super.onCreate(savedInstanceState);

        int pad = dp(24);
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setGravity(Gravity.CENTER_HORIZONTAL);
        column.setPadding(pad, dp(48), pad, pad);

        TextView title = new TextView(this);
        title.setText(R.string.incomplete_install_title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        title.setTextColor(Color.parseColor("#16201D"));
        title.setGravity(Gravity.CENTER);

        TextView message = new TextView(this);
        message.setText(R.string.incomplete_install_message);
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        message.setTextColor(Color.parseColor("#3D4A46"));
        message.setLineSpacing(0, 1.2f);
        message.setPadding(0, dp(16), 0, dp(24));

        Button open = new Button(this);
        open.setText(R.string.incomplete_install_open_store);
        open.setOnClickListener(v -> openStoreListing());

        column.addView(title);
        column.addView(message);
        column.addView(open);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.WHITE);
        scroll.setFillViewport(true);
        scroll.addView(column);
        setContentView(scroll);
    }

    private void openStoreListing() {
        String pkg = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + pkg)));
            } catch (ActivityNotFoundException ignored) {
                // No store and no browser; the message on screen still says what to do.
            }
        }
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics()));
    }
}
