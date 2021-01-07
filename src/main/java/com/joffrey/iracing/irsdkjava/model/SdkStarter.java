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

import com.joffrey.iracing.irsdkjava.model.defines.StatusField;
import com.joffrey.iracing.irsdkjava.model.defines.VarTypeBytes;
import com.joffrey.iracing.irsdkjava.windows.WindowsService;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Service
public class SdkStarter {

    private final WindowsService windowsService;
    private Map<String, VarHeader> vars = new HashMap<>();
    private WinNT.HANDLE memMapFile     = null;
    private WinNT.HANDLE dataValidEvent = null;
    @Getter
    private Pointer      sharedMemory   = null;
    @Getter
    private LiveHeader liveHeader = null;
    private boolean isInitialized = false;
    private boolean wasConnected  = false;
    private VarReader varReader = null;
    private Sinks.Many<IRacingData> dataSink = null;

    private boolean isReady() {
        if (!isInitialized) {
            // Try to open Memory Mapped File
            if (memMapFile == null) {
                memMapFile = windowsService.openMemoryMapFile(com.joffrey.iracing.irsdkjava.model.defines.Constant.IRSDK_MEMMAPFILENAME);
            }

            if (memMapFile != null) {
                if (sharedMemory == null) {
                    sharedMemory = windowsService.mapViewOfFile(memMapFile);
                    liveHeader = new LiveHeader(sharedMemory);

                    if (liveHeader.getByteBuffer() == null) {
                        return false;
                    }

                }

                if (sharedMemory != null) {
                    if (dataValidEvent == null) {
                        dataValidEvent =
                                windowsService.openEvent(com.joffrey.iracing.irsdkjava.model.defines.Constant.IRSDK_DATAVALIDEVENTNAME);
                    }
                }

                if (dataValidEvent != null) {
                    isInitialized = true;
                    return true;
                }

                return false;
            }
            return false;
        }
        return true;
    }

    public boolean isRunning() {
        boolean isConnected;

        if (isReady()) {
            isConnected = (liveHeader.getStatus() & StatusField.IRSDK_STCONNECTED.getValue()) > 0;
        } else {
            isConnected = false;
        }

        // keep track of connection status
        if (wasConnected != isConnected) {
            wasConnected = isConnected;
            if (isConnected) {
                log.info("Connected to iRacing.");
                fetchVars();
                startPublishingThread();
            } else {
                log.info("Lost connection to iRacing");
            }
            //****Note, put your connection handling here
        }

        return isConnected;
    }

    private void startPublishingThread() {
        new Thread(() -> {
            while (isRunning()) {
                dataSink.tryEmitNext(new IRacingData(liveHeader, liveHeader.getLatestVarByteBuffer()));
                windowsService.waitForSingleObject(dataValidEvent, 1000);
            }
        }).start();
    }

    public void fetchVars() {
        vars = liveHeader.fetchVars();
    }

    private VarHeader getVarHeaderEntry(int index) {
        return new VarHeader(ByteBuffer.wrap(sharedMemory.getByteArray(liveHeader.getVarHeaderOffset() + ((long) VarHeader.VAR_HEADER_SIZE
                                                                                                      * index),
                                                                       VarHeader.VAR_HEADER_SIZE)));
    }

    public boolean getVarBoolean(String varName) {
        return getVarBoolean(varName, 0);
    }

    public boolean getVarBoolean(String varName, int entry) {
        VarHeader varHeader = vars.get(varName);
        if (varHeader != null) {
            if (entry >= 0 && entry < varHeader.getCount()) {
                return (liveHeader.getLatestVarByteBuffer()
                              .getChar(varHeader.getOffset() + (entry * VarTypeBytes.IRSDK_BOOL.getValue()))) != 0;
            }
        }
        return false;
    }

    public int getVarInt(String varName) {
        return getVarInt(varName, 0);
    }

    public int getVarInt(String varName, int entry) {
        VarHeader vh = vars.get(varName);
        if (vh != null) {
            if (entry >= 0 && entry < vh.getCount()) {
                return liveHeader.getLatestVarByteBuffer().getInt(vh.getOffset() + (entry * VarTypeBytes.IRSDK_INT.getValue()));
            }
        }
        return 0;
    }

    public float getVarFloat(String varName) {
        return getVarFloat(varName, 0);
    }

    public float getVarFloat(String varName, int entry) {

        VarHeader vh = vars.get(varName);
        if (vh != null) {
            if (entry >= 0 && entry < vh.getCount()) {
                return liveHeader.getLatestVarByteBuffer().getFloat(vh.getOffset() + (entry * VarTypeBytes.IRSDK_FLOAT.getValue()));
            }
        }
        return 0.0F;
    }

    public double getVarDouble(String varName) {
        return getVarDouble(varName, 0);
    }

    public double getVarDouble(String varName, int entry) {
        VarHeader vh = vars.get(varName);
        if (vh != null) {
            if (entry >= 0 && entry < vh.getCount()) {
                return liveHeader.getLatestVarByteBuffer().getDouble(vh.getOffset() + (entry * VarTypeBytes.IRSDK_DOUBLE.getValue()));

            }
        }
        return 0.0;
    }

}
