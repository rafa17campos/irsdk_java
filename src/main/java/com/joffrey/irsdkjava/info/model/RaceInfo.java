package com.joffrey.irsdkjava.info.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RaceInfo {

    // Track info
    private String trackDisplayName;
    private String trackConfigName;
    private String trackLength;
    private String trackCity;
    private String trackCountry;
    private String eventType;

    // Info on remaining
    private double remainingTime;  // SessionTimeRemain
    private int    remainingLaps;  // SessionLapsRemain
    private float  fuelLevel;      // FuelLevel
    private float  fuelLevelPct;   // FuelLevelPct
    private float  fuelUsePerHour; // FuelUsePerHour

    // Rating
    private int averageSOF;

    // Useful booleans
    private int playerCarIdx;

    @AllArgsConstructor
    @Data
    public static class LiveData {

        // Info on remaining
        private double remainingTime;  // SessionTimeRemain
        private int    remainingLaps;  // SessionLapsRemain
        private float  fuelLevel;      // FuelLevel
        private float  fuelLevelPct;   // FuelLevelPct
        private float  fuelUsePerHour; // FuelUsePerHour

    }

    @AllArgsConstructor
    @Data
    public static class YamlData {

        private String trackDisplayName;
        private String trackConfigName;
        private String trackLength;
        private String trackCity;
        private String trackCountry;
        private String eventType;
        // Rating
        private int    averageSOF;
        // Useful booleans
        private int    playerCarIdx;

    }


}