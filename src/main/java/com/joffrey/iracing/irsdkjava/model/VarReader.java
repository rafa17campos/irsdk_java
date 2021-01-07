package com.joffrey.iracing.irsdkjava.model;

import com.joffrey.iracing.irsdkjava.model.defines.VarTypeBytes;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class VarReader {

    public boolean getVarBoolean(ByteBuffer byteBuffer, Map<String, VarHeader> vars, String varName) {
        return getVarBoolean(byteBuffer, vars, varName, 0);
    }

    public boolean getVarBoolean(ByteBuffer byteBuffer, Map<String, VarHeader> vars, String varName, int entry) {
        VarHeader varHeader = vars.get(varName);
        if (varHeader != null) {
            if (entry >= 0 && entry < varHeader.getCount()) {
                return (byteBuffer.getChar(varHeader.getOffset() + (entry * VarTypeBytes.IRSDK_BOOL.getValue()))) != 0;
            }
        }
        return false;
    }

    public int getVarInt(ByteBuffer byteBuffer, Map<String, VarHeader> vars, String varName) {
        return getVarInt(byteBuffer, vars, varName, 0);
    }

    public int getVarInt(ByteBuffer byteBuffer, Map<String, VarHeader> vars, String varName, int entry) {
        VarHeader vh = vars.get(varName);
        if (vh != null) {
            if (entry >= 0 && entry < vh.getCount()) {
                return byteBuffer.getInt(vh.getOffset() + (entry * VarTypeBytes.IRSDK_INT.getValue()));
            }
        }
        return 0;
    }

    public float getVarFloat(ByteBuffer byteBuffer, Map<String, VarHeader> vars, String varName) {
        return getVarFloat(byteBuffer, vars, varName, 0);
    }

    public float getVarFloat(ByteBuffer byteBuffer, Map<String, VarHeader> vars, String varName, int entry) {
        VarHeader vh = vars.get(varName);
        if (vh != null) {
            if (entry >= 0 && entry < vh.getCount()) {
                return byteBuffer.getFloat(vh.getOffset() + (entry * VarTypeBytes.IRSDK_FLOAT.getValue()));
            }
        }
        return 0.0F;
    }

    public double getVarDouble(ByteBuffer byteBuffer, Map<String, VarHeader> vars, String varName) {
        return getVarDouble(byteBuffer, vars, varName, 0);
    }

    public double getVarDouble(ByteBuffer byteBuffer, Map<String, VarHeader> vars, String varName, int entry) {
        VarHeader vh = vars.get(varName);
        if (vh != null) {
            if (entry >= 0 && entry < vh.getCount()) {
                return byteBuffer.getDouble(vh.getOffset() + (entry * VarTypeBytes.IRSDK_DOUBLE.getValue()));

            }
        }
        return 0.0;
    }


}
