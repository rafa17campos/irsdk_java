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

package com.joffrey.iracing.irsdkjava.telemetry;

import com.joffrey.iracing.irsdkjava.model.IRacingData;
import com.joffrey.iracing.irsdkjava.model.VarReader;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData.FuelAndAngles;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData.PedalsAndSpeed;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData.Session;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData.Weather;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@Service
public class TelemetryService {

    private final VarReader varReader;

    private final Flux<TelemetryData> telemetryDataFlux;

    public TelemetryService(Flux<IRacingData> dataFlux, VarReader varReader) {
        this.varReader = varReader;
        telemetryDataFlux = dataFlux.flatMap(this::loadTelemetryData);
        telemetryDataFlux.subscribe();
    }

    public Flux<TelemetryData> getTelemetryDataFlux() {
        return telemetryDataFlux;
    }

    private Mono<TelemetryData> loadTelemetryData(IRacingData iRacingData) {
        return Mono.just(TelemetryData.builder())
                .map(builder -> builder.pedalsAndSpeed(buildPedalsAndSpeed(iRacingData)))
                .map(builder -> builder.fuelAndAngles(buildFuelAndAngles(iRacingData)))
                .map(builder -> builder.weather(buildWeather(iRacingData)))
                .map(builder -> builder.session(buildSession(iRacingData)))
                .map(TelemetryData.TelemetryDataBuilder::build);
    }

    private Session buildSession(IRacingData iRacingData) {
        return new Session(
                varReader.getVarDouble(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "SessionTime"),
                varReader.getVarDouble(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "SessionTimeRemain"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LapBestLapTime"),
                varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Lap"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LapCurrentLapTime"),
                varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LapBestLap"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LapDistPct"));
    }

    private Weather buildWeather(IRacingData iRacingData) {
        return new Weather(
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "AirPressure"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "AirTemp"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "RelativeHumidity"),
                getSkies(varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Skies")),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "TrackTemp"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "WindDir"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "WindVel"),
                getWeatherType(varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "WeatherType")));
    }

    private FuelAndAngles buildFuelAndAngles(IRacingData iRacingData) {
        return new FuelAndAngles(
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "FuelLevel"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "FuelLevelPct"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "FuelUsePerHour"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LatAccel"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LongAccel"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "SteeringWheelAngle")
        );
    }

    private PedalsAndSpeed buildPedalsAndSpeed(IRacingData iRacingData) {
        return new PedalsAndSpeed(
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Throttle"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Brake"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Clutch"),
                varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Gear"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "ShiftGrindRPM"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "RPM"),
                varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Speed")
        );
    }


    private String getWeatherType(Integer weatherIntVal) {
        if (weatherIntVal == 0) {
            return "Constant";
        } else if (weatherIntVal == 1) {
            return "Dynamic";
        } else {
            return "Unknown";
        }
    }

    private String getSkies(Integer skies) {
        if (skies == 0) {
            return "Clear";
        } else if (skies == 1 || skies == 2) {
            return "Cloudy";
        } else if (skies == 3) {
            return "Overcast";
        } else {
            return "Unknown";
        }
    }


}
