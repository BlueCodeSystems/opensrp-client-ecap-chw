package com.bluecodeltd.ecap.chw.model;

public class ServicesModel {
    private  String serviceName;
    private  String userService;
    private  String groupService;

    public ServicesModel(String serviceName, String userService, String groupService) {
        this.serviceName = serviceName;
        this.userService = userService;
        this.groupService = groupService;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUserService() {
        return userService;
    }

    public void setUserService(String userService) {
        this.userService = userService;
    }

    public String getGroupService() {
        return groupService;
    }

    public void setGroupService(String groupService) {
        this.groupService = groupService;
    }
}
