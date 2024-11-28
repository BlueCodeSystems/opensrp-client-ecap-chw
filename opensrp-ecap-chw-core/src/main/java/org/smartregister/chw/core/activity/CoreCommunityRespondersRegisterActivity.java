package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import org.json.JSONObject;
import org.smartregister.chw.anc.fragment.BaseAncRespondersCallDialogFragment;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.adapter.CommunityResponderCustomAdapter;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.CoreCommunityRespondersContract;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.model.CommunityResponderModel;
import org.smartregister.chw.core.presenter.CoreCommunityRespondersPresenter;
import org.smartregister.chw.core.repository.CommunityResponderRepository;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.MultiLanguageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


public class CoreCommunityRespondersRegisterActivity extends MultiLanguageActivity implements CoreCommunityRespondersContract.View {

    private ListView communityRespondersListView;
    private CoreCommunityRespondersContract.Presenter presenter;
    private CommunityResponderCustomAdapter adapter;
    private List<CommunityResponderModel> communityResponders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_responders);
        communityRespondersListView = findViewById(R.id.lv_responder);
        Toolbar toolbar = findViewById(R.id.location_switching_toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.toggle_action_menu).setOnClickListener(v -> onClickDrawer(v));
        initializePresenter();
        initializeAdapter();
        presenter().fetchAllCommunityResponders();
    }

    public void initializeAdapter() {
        communityResponders = new ArrayList<>();
        if (adapter == null) {
            adapter = new CommunityResponderCustomAdapter(communityResponders, getContext(), this);
        }
        communityRespondersListView.setAdapter(adapter);
    }

    public void initializePresenter() {
        if (presenter == null) {
            presenter = new CoreCommunityRespondersPresenter(this);
        }
    }

    public void onClickDrawer(View view) {
        if (view.getId() == R.id.toggle_action_menu) {
            NavigationMenu.getInstance(this, null, null).getDrawer().openDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onResume() {
        presenter().fetchAllCommunityResponders();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_responder, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_responder) {
            CommunityResponderRepository repo = CoreChwApplication.getInstance().communityResponderRepository();
            if (repo.getRespondersCount() > 7) {
                Toast.makeText(this, getString(R.string.add_responder_max_message), Toast.LENGTH_LONG).show();
            } else {
                JSONObject form = FormUtils.getFormUtils().getFormJson(CoreConstants.JSON_FORM.COMMUNITY_RESPONDER_REGISTRATION_FORM);
                startFormActivity(form);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
        presenter().addCommunityResponder(jsonString);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        startActivityForResult(FormUtils.getStartFormActivity(jsonForm, this.getString(R.string.add_community_responder), this), JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void startEditFormActivity(CommunityResponderModel communityResponderModel, String baseEntityID) throws Exception {
        JSONObject jsonObject = org.smartregister.util.FormUtils.getInstance(CoreChwApplication.getInstance().getApplicationContext()).getFormJson(CoreConstants.JSON_FORM.COMMUNITY_RESPONDER_REGISTRATION_FORM);
        jsonObject.put("entity_id", baseEntityID);
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("id", baseEntityID);
        valueMap.put("responder_phone_number", communityResponderModel.getResponderPhoneNumber());
        valueMap.put("responder_name", communityResponderModel.getResponderName());
        valueMap.put("responder_gps", communityResponderModel.getResponderLocation());
        CoreJsonFormUtils.populateJsonForm(jsonObject, valueMap);
        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    public void confirmPurgeResponder(String baseEntityID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.remove_responder_dialog_title));
        builder.setMessage(getString(R.string.remove_responder_dialog_message));
        builder.setCancelable(true);
        builder.setPositiveButton(this.getString(R.string.remove_responder), (dialog, id) -> {
            presenter().removeCommunityResponder(baseEntityID);
        });
        builder.setNegativeButton(this.getString(R.string.cancel), ((dialog, id) -> dialog.cancel()));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public CoreCommunityRespondersContract.Presenter presenter() {
        return presenter;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showPopUpMenu(View view, CoreCommunityRespondersContract.Model communityResponderModel) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.community_responder_contex_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int menuItemItemId = menuItem.getItemId();
            if (menuItemItemId == R.id.call_responder) {
                communityResponderModel.setIsAncResponder(true); // TODO -> This should be persisted
                BaseAncRespondersCallDialogFragment.launchDialog(this, communityResponderModel, null, null, false, false, null);

            } else if (menuItemItemId == R.id.edit_responder) {
                try {
                    this.startEditFormActivity((CommunityResponderModel) communityResponderModel, communityResponderModel.getId());
                } catch (Exception e) {
                    Timber.e(e);
                }
            } else if (menuItemItemId == R.id.remove_responder) {
                try {
                    this.confirmPurgeResponder(communityResponderModel.getId());
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
            return false;
        });
    }

    @Override
    public void refreshCommunityResponders(List<CommunityResponderModel> communityResponderModelList) {
        communityResponders.clear();
        communityResponders.addAll(communityResponderModelList);
        adapter.notifyDataSetChanged();
    }
}