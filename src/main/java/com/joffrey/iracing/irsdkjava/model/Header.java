/*
 *
 *    Copyright (C) 2020 Joffrey Bonifay
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

import lombok.extern.java.Log;
import org.springframework.util.CollectionUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@Log
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
        if (!CollectionUtils.isEmpty(vars)) {
            return vars;
        }
        vars = new HashMap<>();
        for (int index = 0; index < getNumVars(); index++) {
            VarHeader vh = getVarHeaderEntry(index);
            vars.put(vh.getName(), vh);
            log.info("Found var: " + vh);
        }
        return vars;
    }

    private VarHeader getVarHeaderEntry(int index) {
        return new VarHeader(getVarHeaderByteBuffer(index));
    }


}
