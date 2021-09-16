package com.bluecodeltd.ecap.chw.model;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class HouseholdIndexEventClient {

    private final Event event;
    private final Client client;

    public HouseholdIndexEventClient(Event event, Client client){
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
