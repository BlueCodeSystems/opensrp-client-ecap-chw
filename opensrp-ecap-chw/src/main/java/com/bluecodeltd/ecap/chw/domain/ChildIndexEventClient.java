package com.bluecodeltd.ecap.chw.domain;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class ChildIndexEventClient {

    private final Event event;

    private final Client client;

    public ChildIndexEventClient(Event event, Client client) {
        this.event = event;
        this.client = client;
    }

    public Event getEvent() {
        return event;
    }

    public Client getClient() {
        return client;
    }
}
