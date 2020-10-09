package com.joffrey.irsdkjava.library.trackmaptracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TrackmapTracker {

    private int    driverIdx;
    private int    driverCarNbr;
    private String driverInitials;
    private float  driverDistPct;

}
