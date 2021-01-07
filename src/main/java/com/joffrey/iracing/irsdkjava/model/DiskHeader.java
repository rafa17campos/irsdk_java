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

package com.joffrey.iracing.irsdkjava.model;/*
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
 */


import lombok.Data;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Date;

import static com.joffrey.iracing.irsdkjava.model.VarHeader.VAR_HEADER_SIZE;

@Data
public class DiskHeader extends Header {

    private final static int SUB_HEADER_SIZE = 32;

    private RandomAccessFile file;

    private FileChannel fileChannel;

    private ByteBuffer headerByteBuffer;

    public DiskHeader(String path) throws IOException {
        file = new RandomAccessFile(path, "r");
        fileChannel = file.getChannel();
        headerByteBuffer = ByteBuffer.allocate(HEADER_SIZE + SUB_HEADER_SIZE);
        fileChannel.read(headerByteBuffer);
        headerByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public ByteBuffer getHeaderByteBuffer() {
        return headerByteBuffer;
    }

    @SneakyThrows
    @Override
    public ByteBuffer getSessionInfoByteBuffer() {
        int sessionInfoLen = getSessionInfoLen();
        ByteBuffer sessionInfoByteBuffer = ByteBuffer.allocate(sessionInfoLen);
        fileChannel.read(sessionInfoByteBuffer, getSessionInfoOffset());
        sessionInfoByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return sessionInfoByteBuffer;
    }

    @SneakyThrows
    @Override
    public ByteBuffer getVarHeaderByteBuffer() {
        int len = getNumVars() * VAR_HEADER_SIZE;
        ByteBuffer varHeaderByteBuffer = ByteBuffer.allocate(len);
        fileChannel.read(varHeaderByteBuffer, getVarHeaderOffset());
        varHeaderByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return varHeaderByteBuffer;
    }

    @SneakyThrows
    @Override
    public ByteBuffer getVarHeaderByteBuffer(int index) {
        ByteBuffer varHeaderByteBuffer = ByteBuffer.allocate(VAR_HEADER_SIZE);
        fileChannel.read(varHeaderByteBuffer, getVarHeaderOffset() + VAR_HEADER_SIZE * index);
        varHeaderByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return varHeaderByteBuffer;
    }

    @SneakyThrows
    @Override
    public ByteBuffer getVarByteBuffer(int idx) {
        ByteBuffer dest = ByteBuffer.allocate(getBufLen());
        fileChannel.read(dest, getSessionInfoOffset() + getSessionInfoLen() + idx * getBufLen());
        dest.order(ByteOrder.LITTLE_ENDIAN);
        return dest;
    }

    public Date getSessionStartDate() {
        return new Date(getHeaderByteBuffer().getLong(HEADER_SIZE) * 1000);
    }

    public double getSessionStartTime() {
        return getHeaderByteBuffer().getDouble(HEADER_SIZE + 8);
    }

    public double getSessionEndTime() {
        return getHeaderByteBuffer().getDouble(HEADER_SIZE + 16);
    }

    public int getSessionLapCount() {
        return getHeaderByteBuffer().getInt(HEADER_SIZE + 24);
    }

    public int getSessionRecordCount() {
        return getHeaderByteBuffer().getInt(HEADER_SIZE + 28);
    }


}
