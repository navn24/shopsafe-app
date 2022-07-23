package com.navn.safeshop;

public class MarkerTag {

    private String markerName;
    private String markerAddress;

    public MarkerTag(String markerName, String markerAddress) {
        this.markerName = markerName;
        this.markerAddress = markerAddress;
    }


    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getMarkerAddress() {
        return markerAddress;
    }

    public void setMarkerAddress(String markerAddress) {
        this.markerAddress = markerAddress;
    }
}
