package com.bluecodeltd.ecap.chw.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bluecodeltd.ecap.chw.contract.FindReportContract;

import org.smartregister.CoreLibrary;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.util.AssetHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class FilterReportFragmentModel implements FindReportContract.Model {

    @NonNull
    @Override
    public LinkedHashMap<String, String> getAllLocations() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        // read all the locations and return them as a hierachy
        LinkedHashMap<String, TreeNode<String, Location>> locationMap = readLocationMap();
        if (locationMap == null) return map;

        extractToMap(locationMap, map);

        return map;
    }

    private void extractToMap(@NonNull LinkedHashMap<String, TreeNode<String, Location>> locationMap, @NonNull LinkedHashMap<String, String> destination) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {
            destination.put(entry.getValue().getId(), entry.getValue().getLabel());
            LinkedHashMap<String, TreeNode<String, Location>> children = entry.getValue().getChildren();
            if (children != null && children.size() > 0) {
                extractToMap(children, destination);
            }
        }
    }

    @Nullable
    private LinkedHashMap<String, TreeNode<String, Location>> readLocationMap() {
        String locationData = CoreLibrary.getInstance().context().anmLocationController().get();
        LocationTree locationTree = AssetHandler.jsonStringToJava(locationData, LocationTree.class);
        if (locationTree != null) {
            return locationTree.getLocationsHierarchy();
        }
        return null;
    }


    private final java.util.Map<String, String> additionalFields = new java.util.HashMap<>();

    public java.util.Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public String getAdditionalField(String key) {
        if (key == null) return null;
        return additionalFields.get(key);
    }

    public void setAdditionalField(String key, String value) {
        if (key == null) return;
        additionalFields.put(key, value);
    }
}

