package com.joffrey.iracing.irsdkjava.model;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.joffrey.iracing.irsdkjava.model.VarHeader.VAR_HEADER_SIZE;

public class DiskHeaderTest {

    private String filename = "C:\\Users\\azrae\\OneDrive\\Share\\porsche911cup_watkinsglen cupcircuit 2020-12-28 20-04-04.ibt";

    @Test
    public void test1() throws IOException {
        DiskHeader diskHeader = new DiskHeader(filename);
        System.out.println(diskHeader.getHeaderByteBuffer());
        System.out.println("Ver: " + diskHeader.getVer());
        System.out.println("Status: " + diskHeader.getStatus());
        System.out.println("Tickrate: " + diskHeader.getTickRate());
        System.out.println("sessionInfoUpdate: " + diskHeader.getSessionInfoUpdate());
        System.out.println("sessionInfoLen: " + diskHeader.getSessionInfoLen());
        System.out.println("sessionInfoOffset: " + diskHeader.getSessionInfoOffset());
        System.out.println("numVars: " + diskHeader.getNumVars());
        System.out.println("varHeaderOffset: " + diskHeader.getVarHeaderOffset());
        System.out.println("numBuf: " + diskHeader.getNumBuf());
        System.out.println("bufLen: " + diskHeader.getBufLen());
        ByteBuffer sessionInfoByteBuffer = diskHeader.getSessionInfoByteBuffer();
        System.out.println("sessionInfoByteBuffer: " + sessionInfoByteBuffer);
//        System.out.println(new String(sessionInfoByteBuffer.array()));
        System.out.println("sessionStartDate: " + diskHeader.getSessionStartDate());
        System.out.println("sessionStartTime: " + diskHeader.getSessionStartTime());
        System.out.println("sessionEndTime: " + diskHeader.getSessionEndTime());
        System.out.println("sessionLapCount: " + diskHeader.getSessionLapCount());
        System.out.println("sessionRecordCount: " + diskHeader.getSessionRecordCount());

        ByteBuffer varHeaderByteBuffer = diskHeader.getVarHeaderByteBuffer();
        List<VarHeader> varHeaderList = new ArrayList<>();
        for (int i = 0; i < diskHeader.getNumVars(); i++) {
            varHeaderList.add(new VarHeader(varHeaderByteBuffer, VAR_HEADER_SIZE * i));
        }
        for (VarHeader varHeader : varHeaderList) {
            System.out.println(varHeader);
        }
    }
}
