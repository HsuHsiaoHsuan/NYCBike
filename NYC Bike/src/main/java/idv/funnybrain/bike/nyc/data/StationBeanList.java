package idv.funnybrain.bike.nyc.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by freeman on 2014/4/28.
 */


/*
{
    "id":72,
    "stationName":"W 52 St & 11 Ave",
    "availableDocks":4,
    "totalDocks":38,
    "latitude":40.76727216,
    "longitude":-73.99392888,
    "statusValue":"In Service",    // if it's 'In Service', statusKey is '1', 'Not In Service' is '3'
    "statusKey":1,
    "availableBikes":32,
    "stAddress1":"W 52 St & 11 Ave",
    "stAddress2":"",
    "city":"",
    "postalCode":"",
    "location":"",
    "altitude":"",
    "testStation":false,
    "lastCommunicationTime":null,
    "landMark":""}
 */
public class StationBeanList {
    private String id;
    private String stationName;
    private int availableDocks;
    private int totalDocks;
    private Double latitude;
    private Double longitude;
    private String statusValue;
    private int statusKey;
    private int availableBikes;
    private String stAddress1;
    private String stAddress2;
    private String city;
    private String postalCode;
    private String location;
    private String altitude;
    private boolean testStation;
    private String lastCommunicationTime;
    private String landMark;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getAvailableDocks() {
        return availableDocks;
    }
    public void setAvailableDocks(int availableDocks) {
        this.availableDocks = availableDocks;
    }

    public int getTotalDocks() {
        return totalDocks;
    }
    public void setTotalDocks(int totalDocks) {
        this.totalDocks = totalDocks;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStatusValue() {
        return statusValue;
    }
    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public int getStatusKey() {
        return statusKey;
    }
    public void setStatusKey(int statusKey) {
        this.statusKey = statusKey;
    }

    public int getAvailableBikes() {
        return availableBikes;
    }
    public void setAvailableBikes(int availableBikes) {
        this.availableBikes = availableBikes;
    }

    public String getStAddress1() {
        return stAddress1;
    }
    public void setStAddress1(String stAddress1) {
        this.stAddress1 = stAddress1;
    }

    public String getStAddress2() {
        return stAddress2;
    }
    public void setStAddress2(String stAddress2) {
        this.stAddress2 = stAddress2;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getAltitude() {
        return altitude;
    }
    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public boolean isTestStation() {
        return testStation;
    }
    public void setTestStation(boolean testStation) {
        this.testStation = testStation;
    }

    public String getLastCommunicationTime() {
        return lastCommunicationTime;
    }
    public void setLastCommunicationTime(String lastCommunicationTime) {
        this.lastCommunicationTime = lastCommunicationTime;
    }

    public String getLandMark() {
        return landMark;
    }
    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("stationName: " + this.stationName);
        result.append(", stAddress1: " + stAddress1);
        result.append(", stAddress2: " + stAddress2);
        result.append(", bike: " + availableBikes);
        result.append(", dock: " + availableDocks);

        return result.toString();
    }

    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }
}
