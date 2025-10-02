package org.smartregister.thinkmd;

/**
 * Minimal configuration holder for the ThinkMD integration. The original
 * library expects endpoint metadata to be supplied during initialisation, so we
 * keep the same surface area even though the stub does not actively use the
 * values.
 */
public class ThinkMDConfig {

    private String thinkmdEndPoint;
    private String thinkmdBaseUrl;

    public String getThinkmdEndPoint() {
        return thinkmdEndPoint;
    }

    public void setThinkmdEndPoint(String thinkmdEndPoint) {
        this.thinkmdEndPoint = thinkmdEndPoint;
    }

    public String getThinkmdBaseUrl() {
        return thinkmdBaseUrl;
    }

    public void setThinkmdBaseUrl(String thinkmdBaseUrl) {
        this.thinkmdBaseUrl = thinkmdBaseUrl;
    }
}
