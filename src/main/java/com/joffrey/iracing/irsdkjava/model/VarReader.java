/*
 *
 *    Copyright (C) 2021 Rafael Campos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.joffrey.iracing.irsdkjava.model;

import com.joffrey.iracing.irsdkjava.model.defines.VarTypeBytes;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@Component
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
