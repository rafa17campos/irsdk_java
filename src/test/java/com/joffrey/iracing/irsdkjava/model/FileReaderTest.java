package com.joffrey.iracing.irsdkjava.model;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FileReaderTest {

    private String filename = "C:\\Users\\azrae\\OneDrive\\iRacing\\telemetry\\bmwm4gt3_daytona 2011 road 2021-01-06 00-02-59.ibt";

    private VarReader varReader = new VarReader();

    @Test
    public void test() throws IOException {
        var diskHeader = new DiskHeader(filename);
        var fileReader = new FileReader();
        var vars = diskHeader.fetchVars();
        fileReader.loadData(diskHeader).doOnNext(
                bb -> {
                    var lap = varReader.getVarInt(bb, vars, "Lap");
                    var value = varReader.getVarFloat(bb, vars, "RFwearL");
                    System.out.println(lap + ": " + value);
                }
        ).blockLast();
    }

}
