package com.joffrey.iracing.irsdkjava.model;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public abstract class Header {
    protected static final int HEADER_SIZE = 112; // All fields are int (4 bytes), there are 28 fields (28 * 4) = 112

    protected static final int VARBUF_SIZE = 4 * 4;

    private Map<String, VarHeader> vars;

    public abstract ByteBuffer getHeaderByteBuffer();

    public abstract ByteBuffer getSessionInfoByteBuffer();

    public abstract ByteBuffer getVarHeaderByteBuffer(int index);

    public abstract ByteBuffer getVarHeaderByteBuffer();

    public abstract ByteBuffer getVarByteBuffer(int idx);

    public int getVer() {
        return getHeaderByteBuffer().getInt(0);
    }

    public int getStatus() {
        return getHeaderByteBuffer().getInt(4);
    }

    public int getTickRate() {
        return getHeaderByteBuffer().getInt(8);
    }

    public int getSessionInfoUpdate() {
        return getHeaderByteBuffer().getInt(12);
    }

    public int getSessionInfoLen() {
        return getHeaderByteBuffer().getInt(16);
    }

    public int getSessionInfoOffset() {
        return getHeaderByteBuffer().getInt(20);
    }

    public int getNumVars() {
        return getHeaderByteBuffer().getInt(24);
    }

    public int getVarHeaderOffset() {
        return getHeaderByteBuffer().getInt(28);
    }

    public int getNumBuf() {
        return getHeaderByteBuffer().getInt(32);
    }

    public int getBufLen() {
        return getHeaderByteBuffer().getInt(36);
    }

    public Map<String, VarHeader> fetchVars() {
        if (vars != null) {
            return vars;
        }
        vars = new HashMap<>();
        for (int index = 0; index < getNumVars(); index++) {
            VarHeader vh = getVarHeaderEntry(index);
            vars.put(vh.getName(), vh);
        }
        return vars;
    }

    private VarHeader getVarHeaderEntry(int index) {
        return new VarHeader(getVarHeaderByteBuffer(index));
    }


}
