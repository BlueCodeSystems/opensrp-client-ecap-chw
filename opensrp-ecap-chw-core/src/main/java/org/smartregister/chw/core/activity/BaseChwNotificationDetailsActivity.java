package org.smartregister.chw.core.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.ChwNotificationDetailsContract;
import org.smartregister.chw.core.dao.ChwNotificationDao;
import org.smartregister.chw.core.domain.NotificationItem;
import org.smartregister.chw.core.presenter.BaseChwNotificationDetailsPresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.MultiLanguageActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.List;

import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.NOTIFICATION_ID;
import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.NOTIFICATION_TYPE;

public abstract class BaseChwNotificationDetailsActivity extends MultiLanguageActivity
        implements ChwNotificationDetailsContract.View, View.OnClickListener {

    protected TextView notificationTitle;
    protected LinearLayout notificationDetails;
    protected TextView markAsDoneTextView;
    protected TextView viewProfileTextView;
    protected ChwNotificationDetailsContract.Presenter presenter;
    protected String notificationId;
    protected String notificationType;
    protected CommonPersonObjectClient commonPersonObjectClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chw_notification_details);
        inflateToolbar();
        setupViews();
        setCommonPersonsObjectClient((CommonPersonObjectClient) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.CLIENT));
        initPresenter();
        disableMarkAsDoneAction(ChwNotificationDao.isMarkedAsDone(this, notificationId, notificationType));
    }

    private void inflateToolbar() {
        Toolbar toolbar = findViewById(R.id.back_to_updates_toolbar);
        CustomFontTextView toolBarTextView = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
        toolBarTextView.setOnClickListener(v -> finish());
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }
    }

    protected void setupViews() {
        notificationTitle = findViewById(R.id.notification_title);
        notificationDetails = findViewById(R.id.notification_content);
        markAsDoneTextView = findViewById(R.id.mark_as_done);
        markAsDoneTextView.setOnClickListener(this);
        viewProfileTextView = findViewById(R.id.view_profile);
        viewProfileTextView.setOnClickListener(this);
    }

    @Override
    public void setNotificationDetails(NotificationItem notificationItem) {
        notificationTitle.setText(notificationItem.getTitle());
        addNotificationInnerContent(notificationItem.getDetails());
    }

    @Override
    public void initPresenter() {
        presenter = new BaseChwNotificationDetailsPresenter(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            notificationId = getIntent().getExtras().getString(NOTIFICATION_ID);
            notificationType = getIntent().getExtras().getString(NOTIFICATION_TYPE);
            presenter.getNotificationDetails(notificationId, notificationType);
        }
    }

    @Override
    public void disableMarkAsDoneAction(boolean disable) {
        if (disable) {
            markAsDoneTextView.setEnabled(false);
            markAsDoneTextView.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.disabled_button_background));
            markAsDoneTextView.setTextColor(ContextCompat.getColor(this,
                    R.color.text_black));
        }
    }

    private void addNotificationInnerContent(List<String> details) {
        notificationDetails.removeAllViews();
        for (String entry : details) {
            TextView textView = new TextView(this);
            textView.setTextSize(18f);
            textView.setText(entry);
            textView.setPadding(16, 32, 16, 32);
            textView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            textView.setSingleLine(false);
            textView.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            notificationDetails.addView(textView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    public ChwNotificationDetailsContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void setCommonPersonsObjectClient(CommonPersonObjectClient client) {
        commonPersonObjectClient = client;
    }

    @Override
    public CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.view_profile) {
            goToMemberProfile();
        } else if (view.getId() == R.id.mark_as_done) {
            getPresenter().dismissNotification(notificationId, notificationType);
        } else {
            Utils.showShortToast(this, getString(R.string.perform_click_action));
        }
    }
}
