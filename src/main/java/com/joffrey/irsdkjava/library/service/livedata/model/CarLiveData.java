/*
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

package com.joffrey.irsdkjava.library.service.livedata.model;

import lombok.Data;

@Data
public class CarLiveData {

    private   int     carIdxClassPosition;
    private   float   carIdxEstTime;
    private   float   carIdxF2Time;
    protected int     carIdxGear;
    private   int     carIdxLap;
    private   int     carIdxLapCompleted;
    private   float   carIdxLapDistPct;
    private   boolean carIdxOnPitRoad;
    private   int     carIdxPosition;
    private   float   carIdxRPM;
    private   float   carIdxSteer;
    private   String  carIdxTrackSurface;
    private   String  carIdxTrackSurfaceMaterial;
    private   float   carIdxLastLapTime;
    private   float   carIdxBestLapTime;
    private   int     carIdxBestLapNum;
    private   boolean carIdxP2P_Status;
    private   int     carIdxP2P_Count;
    private   int     paceMode;
    private   int     carIdxPaceLine;
    private   int     carIdxPaceRow;
    private   int     carIdxPaceFlags;

}