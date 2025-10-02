package org.smartregister.chw.anc.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseUpcomingService {

    private List<String> serviceName = new ArrayList<>();
    private Date serviceDate;
    private Date overDueDate;
    private Date expiryDate;
    private List<BaseUpcomingService> upcomingServiceList;

    public BaseUpcomingService() {
    }

    public BaseUpcomingService(String serviceName) {
        this.serviceName.add(serviceName);
    }

    public String getServiceName() {
        return (serviceName.size() > 0) ? serviceName.get(0) : "";
    }

    public void setServiceName(String serviceName) {
        this.serviceName.add(serviceName);
    }

    public List<String> getServiceNames() {
        return serviceName;
    }

    public void setServiceName(@NonNull List<String> serviceNames) {
        this.serviceName.addAll(serviceNames);
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public Date getOverDueDate() {
        return overDueDate == null ? getServiceDate() : overDueDate;
    }

    public void setOverDueDate(Date overDueDate) {
        this.overDueDate = overDueDate;
    }

    public List<BaseUpcomingService> getUpcomingServiceList() {
        return upcomingServiceList;
    }

    public void setUpcomingServiceList(List<BaseUpcomingService> upcomingServiceList) {
        this.upcomingServiceList = upcomingServiceList;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
