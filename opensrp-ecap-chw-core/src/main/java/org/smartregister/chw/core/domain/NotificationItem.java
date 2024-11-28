package org.smartregister.chw.core.domain;

import java.util.List;

public class NotificationItem {
    private String title;
    private List<String> details;
    private String clientBaseEntityId;

    public NotificationItem(String title, List<String> details) {
        this.title = title;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDetails() {
        return details;
    }

    public NotificationItem setClientBaseEntityId(String clientBaseEntityId) {
        this.clientBaseEntityId = clientBaseEntityId;
        return this;
    }

    public String getClientBaseEntityId() {
        return clientBaseEntityId;
    }
}
