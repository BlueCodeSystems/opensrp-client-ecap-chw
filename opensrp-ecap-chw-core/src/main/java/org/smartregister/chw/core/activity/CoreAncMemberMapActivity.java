package org.smartregister.chw.core.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.mapbox.geojson.BoundingBox;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfMeasurement;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.fragment.BaseAncRespondersCallDialogFragment;
import org.smartregister.chw.anc.fragment.BaseAncWomanCallDialogFragment;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.model.CommunityResponderModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;

import io.ona.kujaku.utils.CoordinateUtils;
import io.ona.kujaku.views.KujakuMapView;
import timber.log.Timber;

public class CoreAncMemberMapActivity extends AppCompatActivity {

    public static final String RECYCLER_VIEW_POSITION_PROPERTY = "recycler-view-position";
    private static int BOUNDING_BOX_PADDING = 100;
    protected AppBarLayout appBarLayout;
    private KujakuMapView kujakuMapView;
    private GeoJsonSource communityTransportersSource;
    private String ancWomanName;
    private String ancWomanPhoneNumber;
    private String ancWomanFamilyHeadName;
    private String ancWomanFamilyHeadPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anc_member_map);
        ancWomanName = getIntent().getStringExtra(CoreConstants.KujakuConstants.NAME);
        ancWomanPhoneNumber = getIntent().getStringExtra(CoreConstants.KujakuConstants.ANC_WOMAN_PHONE);
        ancWomanFamilyHeadName = getIntent().getStringExtra(CoreConstants.KujakuConstants.ANC_WOMAN_FAMILY_HEAD);
        ancWomanFamilyHeadPhoneNumber = getIntent().getStringExtra(CoreConstants.KujakuConstants.ANC_WOMAN_FAMILY_HEAD_PHONE);


        kujakuMapView = findViewById(R.id.kujakuMapView);
        kujakuMapView.onCreate(savedInstanceState);
        kujakuMapView.showCurrentLocationBtn(true);
        kujakuMapView.setDisableMyLocationOnMapMove(true);

        kujakuMapView.getMapAsync(mapBoxMap -> {
            Style.Builder builder = new Style.Builder().fromUri("asset://ba_anc_style.json");
            mapBoxMap.setStyle(builder, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    communityTransportersSource = style.getSourceAs("community-transporters-data-set");

                    FeatureCollection featureCollection = loadCommunityTransporters();
                    BoundingBox boundingBox = showCommunityTransporters(mapBoxMap, featureCollection);

                    zoomToPatientLocation(mapBoxMap, boundingBox);
                    addCommunityTransporterClickListener(kujakuMapView);
                }
            });
        });

        inflateToolbar();

    }

    private void inflateToolbar() {
        Toolbar toolbar = findViewById(R.id.back_anc_toolbar);
        CustomFontTextView toolBarTextView = toolbar.findViewById(R.id.anc_map_toolbar_title);
        toolBarTextView.setText(String.format(getString(R.string.return_to_profile), ancWomanName.substring(0, ancWomanName.indexOf(" "))));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
        toolBarTextView.setOnClickListener(v -> finish());
        appBarLayout = findViewById(R.id.map_app_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }

        TextView ancWomanNameView = findViewById(R.id.text_view_name);
        TextView familyNameView = findViewById(R.id.text_view_family);
        TextView landMarkView = findViewById(R.id.text_view_landmark);
        ancWomanNameView.setText(ancWomanName);
        familyNameView.setText(getString(R.string.house_hold_family_name, getIntent().getStringExtra(CoreConstants.KujakuConstants.FAMILY_NAME)));
        landMarkView.setText(getString(R.string.house_hold_discription, getIntent().getStringExtra(CoreConstants.KujakuConstants.LAND_MARK)));
        final View imageButton = findViewById(R.id.call_woman);
        imageButton.setOnClickListener(view -> {
            if (StringUtils.isNotBlank(ancWomanPhoneNumber) || StringUtils.isNotBlank(ancWomanFamilyHeadPhoneNumber))
                BaseAncWomanCallDialogFragment.launchDialog(this, ancWomanName,
                        ancWomanPhoneNumber, ancWomanFamilyHeadName, ancWomanFamilyHeadPhoneNumber, "ANC");
        });
    }

    private void addCommunityTransporterClickListener(@NonNull KujakuMapView kujakuMapView) {
        kujakuMapView.setOnFeatureClickListener(features -> {
            Feature feature = features.get(0);
            if (feature != null)
                featureClicked(feature);
        }, "community-transporters", "health-facilities");
    }

    private void featureClicked(@NonNull Feature feature) {
        String responderName = feature.getStringProperty(CoreConstants.JsonAssets.RESPONDER_NAME);
        String respondersPhoneNumber = feature.getStringProperty(CoreConstants.JsonAssets.RESPONDER_PHONE_NUMBER);
        CommunityResponderModel model = new CommunityResponderModel(responderName, respondersPhoneNumber, null, null);
        model.setIsAncResponder(true);
        if (StringUtils.isNotBlank(responderName) && StringUtils.isNotBlank(respondersPhoneNumber)) {
            BaseAncRespondersCallDialogFragment.launchDialog(this, model, null,
                    null, false, false, null);
            return;
        }

        String facilityName = feature.getStringProperty("Facility Name");
        String facilityType = feature.getStringProperty("Facility Type");
        String status = feature.getStringProperty("Operating Status");
        String basicServiceProvided = feature.getStringProperty("Basic Service Provided ANC_BEmONC_PPC_QI_MIP_ENC_FP_HIV_RMNCH_integration");
        String cEmONC = feature.getStringProperty("CEmONC");
        String ownership = feature.getStringProperty("Ownership");

        if (StringUtils.isNotBlank(facilityName) && StringUtils.isNotBlank(status)) {
            model.setResponderName(null);
            model.setResponderPhoneNumber(null);
            BaseAncRespondersCallDialogFragment.launchDialog(this, model,
                    facilityName, facilityType, basicServiceProvided.equalsIgnoreCase("yes"), cEmONC.equalsIgnoreCase("yes"), ownership);
        } else {
            Toast.makeText(this, getString(R.string.invalid_feature), Toast.LENGTH_SHORT).show();
        }

    }

    @Nullable
    private LatLng extractUserLocation() {
        String location = getIntent().getStringExtra(CoreConstants.KujakuConstants.LAT_LNG);
        if (StringUtils.isBlank(location))
            location = "-1.958955 33.7909233";
        String[] latLong = location.split(" ");
        double latitude = Double.parseDouble(latLong[0]);
        double longitude = Double.parseDouble(latLong[1]);
        return new LatLng(latitude, longitude);
    }

    private void zoomToPatientLocation(@NonNull MapboxMap mapboxMap, @Nullable BoundingBox boundingBox) {
        LatLng userLocation = extractUserLocation();
        if (userLocation != null && boundingBox == null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(userLocation).zoom(16).build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        if (userLocation != null) {
            MarkerOptions markerOptions = new MarkerOptions().position(userLocation);
            mapboxMap.addMarker(markerOptions);
        }

        if (boundingBox != null && !CoordinateUtils.isLocationInBounds(userLocation, boundingBox.north(), boundingBox.south(), boundingBox.east(), boundingBox.west())) {
            double north = boundingBox.north();
            double south = boundingBox.south();
            double east = boundingBox.east();
            double west = boundingBox.west();

            if (userLocation.getLatitude() > north) {
                north = userLocation.getLatitude();
            } else if (userLocation.getLatitude() < south) {
                south = userLocation.getLatitude();
            }

            if (userLocation.getLongitude() > east) {
                east = userLocation.getLongitude();
            } else if (userLocation.getLongitude() < west) {
                west = userLocation.getLongitude();
            }

            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds.from(north, east, south, west), BOUNDING_BOX_PADDING));
        }
    }

    @Nullable
    private BoundingBox showCommunityTransporters(@NonNull MapboxMap mapboxMap, @Nullable FeatureCollection featureCollection) {
        if (featureCollection != null && featureCollection.features() != null && featureCollection.features().size() > 0 && communityTransportersSource != null) {
            BoundingBox boundingBox = featureCollection.bbox();

            if (boundingBox == null) {
                double[] bBox = TurfMeasurement.bbox(featureCollection);
                boundingBox = BoundingBox.fromLngLats(bBox[0], bBox[1], bBox[2], bBox[3]);
            }

            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds.from(boundingBox.north(), boundingBox.east(), boundingBox.south(), boundingBox.west()), BOUNDING_BOX_PADDING));
            communityTransportersSource.setGeoJson(featureCollection);
            return boundingBox;
        }

        return null;
    }

    @Nullable
    private FeatureCollection loadCommunityTransporters() {
        return FeatureCollection.fromFeatures(getRespondersFeatureList());
    }

    private List<Feature> getRespondersFeatureList() {
        List<CommunityResponderModel> communityResponders = CoreChwApplication.getInstance().communityResponderRepository().readAllResponders();
        List<Feature> featureList = new ArrayList<>();
        int counter = 0;
        for (CommunityResponderModel communityResponderModel : communityResponders) {
            try {
                Feature feature = getFeature(communityResponderModel);
                feature.addNumberProperty(RECYCLER_VIEW_POSITION_PROPERTY, counter);
                featureList.add(feature);
                counter++;
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
        return featureList;
    }

    private Feature getFeature(CommunityResponderModel communityResponderModel) throws JSONException {
        String[] latLong = communityResponderModel.getResponderLocation().split(" ");
        double latitude = Double.parseDouble(latLong[0]);
        double longitude = Double.parseDouble(latLong[1]);
        com.cocoahero.android.geojson.Feature feature = new com.cocoahero.android.geojson.Feature();
        feature.setGeometry(new com.cocoahero.android.geojson.Point(latitude, longitude));
        JSONObject properties = new JSONObject();
        properties.put(CoreConstants.JsonAssets.RESPONDER_NAME, communityResponderModel.getResponderName());
        properties.put(CoreConstants.JsonAssets.RESPONDER_PHONE_NUMBER, communityResponderModel.getResponderPhoneNumber());
        feature.setProperties(properties);
        return Feature.fromJson(feature.toJSON().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (kujakuMapView != null) {
            kujakuMapView.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (kujakuMapView != null)
            kujakuMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (kujakuMapView != null)
            kujakuMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (kujakuMapView != null)
            kujakuMapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (kujakuMapView != null)
            kujakuMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (kujakuMapView != null)
            kujakuMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (kujakuMapView != null)
            kujakuMapView.onDestroy();
    }
}
