package com.yopie.movie.model;

/**
 * Created by Yopie on 5/27/2017.
 */

public class DummyData {

    private String weatherId;
    private String day;
    private String forecast;
    private int maxTemp;
    private int minTemp;


    public DummyData(String day, String forecast, int maxTemp, int minTemp) {
        this.day = day;
        this.forecast = forecast;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }

    public String getWeatherId() {
        return weatherId + ".png";
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTempDegree(){
        return String.valueOf(maxTemp) + "\u00b0";
    }

    public String getMinTempDegree(){
        return String.valueOf(minTemp) + "\u00b0";
    }
}
