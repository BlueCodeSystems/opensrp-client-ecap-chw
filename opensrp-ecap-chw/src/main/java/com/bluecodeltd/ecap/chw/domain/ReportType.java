package com.bluecodeltd.ecap.chw.domain;

import com.bluecodeltd.ecap.chw.contract.ListContract;

public class ReportType implements ListContract.Identifiable {

    private String id;
    private String name;

    public ReportType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
