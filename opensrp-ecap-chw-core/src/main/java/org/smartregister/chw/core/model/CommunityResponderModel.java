package org.smartregister.chw.core.model;

import org.smartregister.chw.core.contract.CoreCommunityRespondersContract;

public class CommunityResponderModel implements CoreCommunityRespondersContract.Model {

    private String responderName;
    private String responderPhoneNumber;
    private String responderLocation;
    private String id;
    private boolean isAncResponder;

    public CommunityResponderModel() {
    }

    public CommunityResponderModel(String responderName, String responderPhoneNumber, String responderLocation, String id) {
        this.id = id;
        this.responderName = responderName;
        this.responderPhoneNumber = responderPhoneNumber;
        this.responderLocation = responderLocation;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setResponderName(String responderName) {
        this.responderName = responderName;
    }

    public void setResponderPhoneNumber(String responderPhoneNumber) {
        this.responderPhoneNumber = responderPhoneNumber;
    }

    public void setResponderLocation(String responderLocation) {
        this.responderLocation = responderLocation;
    }

    public String getResponderName() {
        return responderName;
    }

    public String getResponderPhoneNumber() {
        return responderPhoneNumber;
    }

    public String getResponderLocation() {
        return responderLocation;
    }

    @Override
    public boolean isAncResponder() {
        return isAncResponder;
    }

    @Override
    public void setIsAncResponder(boolean isAncResponder) {
        this.isAncResponder = isAncResponder;
    }


}
