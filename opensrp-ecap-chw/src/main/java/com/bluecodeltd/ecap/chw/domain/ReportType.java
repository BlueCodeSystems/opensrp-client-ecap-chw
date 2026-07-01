package com.bluecodeltd.ecap.chw.domain;

import com.bluecodeltd.ecap.chw.contract.ListContract;

public class ReportType implements ListContract.Identifiable {

    private String id;
    private String name;
    private long count;
    private String lastSubmitted;

    public ReportType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ReportType(String id, String name, long count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public ReportType(String id, String name, long count, String lastSubmitted) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.lastSubmitted = lastSubmitted;
    }

    @Override
    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getLastSubmitted() {
        return lastSubmitted;
    }

    public void setLastSubmitted(String lastSubmitted) {
        this.lastSubmitted = lastSubmitted;
    }
}
