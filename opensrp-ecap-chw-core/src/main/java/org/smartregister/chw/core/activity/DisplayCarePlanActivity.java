package org.smartregister.chw.core.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.AppBarLayout;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.server.FileHTTPServer;

import java.io.IOException;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.CoreConstants.INTENT_KEY.CONTENT_TO_DISPLAY;

public class DisplayCarePlanActivity extends AppCompatActivity {

    protected AppBarLayout appBarLayout;
    private FileHTTPServer fileHTTPServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_care_plan);

        initObjects();
        if (getIntent().hasExtra(CONTENT_TO_DISPLAY) &&
                getIntent().getStringExtra(CONTENT_TO_DISPLAY) != null) {
            
            String contentToDisplay = getIntent().getStringExtra(CONTENT_TO_DISPLAY);

            contentToDisplay = contentToDisplay.replace("&lt;", "<")
                    .replace("&gt;", ">");

            try {
                fileHTTPServer = new FileHTTPServer(this, contentToDisplay);
                fileHTTPServer.start();
            } catch (IOException e) {
                Timber.e(e);
            }

            WebView webView = findViewById(R.id.web_view);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("http://localhost:8085/thinkmd_assessment_parser.html");
        } else {
            showNoContentAlertDialog();
        }
    }


    private void initObjects() {
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(view -> DisplayCarePlanActivity.this.finish());
    }

    private void showNoContentAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("No Content Found");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close",
                (dialog, which) -> {
                    dialog.dismiss();
                    this.finish();
                    DisplayCarePlanActivity.this.finish();
                });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileHTTPServer.destroy();
    }
}