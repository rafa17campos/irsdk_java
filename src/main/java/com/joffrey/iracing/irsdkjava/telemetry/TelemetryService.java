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

    private Flux<TelemetryData> loadTelemetryData(IRacingData iRacingData) {

        Flux<TelemetryData.PedalsAndSpeed> firstGroup = Flux.zip(Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Throttle")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Brake")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Clutch")),
                Mono.just(varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Gear")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "ShiftGrindRPM")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "RPM")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Speed")))
                .map(o -> new PedalsAndSpeed(o.getT1(),
                        o.getT2(),
                        o.getT3(),
                        o.getT4(),
                        o.getT5(),
                        o.getT6(),
                        o.getT7()));

        Flux<TelemetryData.FuelAndAngles> secondGroup = Flux.zip(Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "FuelLevel")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "FuelLevelPct")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "FuelUsePerHour")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LatAccel")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LongAccel")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "SteeringWheelAngle")))
                .map(o -> new FuelAndAngles(o.getT1(),
                        o.getT2(),
                        o.getT3(),
                        o.getT4(),
                        o.getT5(),
                        o.getT6()));

        Flux<TelemetryData.Weather> thirdGroup = Flux.zip(Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "AirPressure")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "AirTemp")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "RelativeHumidity")),
                Mono.just(varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Skies")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "TrackTemp")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "WindDir")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "WindVel")),
                Mono.just(varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "WeatherType")))
                .map(o -> new Weather(o.getT1(),
                        o.getT2(),
                        o.getT3(),
                        getSkies(o.getT4()),
                        o.getT5(),
                        o.getT6(),
                        o.getT7(),
                        getWeatherType(o.getT8())));

        Flux<TelemetryData.Session> fourthGroup = Flux.zip(Mono.just(varReader.getVarDouble(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "SessionTime")),
                Mono.just(varReader.getVarDouble(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "SessionTimeRemain")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LapBestLapTime")),
                Mono.just(varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "Lap")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LapCurrentLapTime")),
                Mono.just(varReader.getVarInt(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LapBestLap")),
                Mono.just(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), "LapDistPct")))
                .map(o -> new Session(o.getT1(),
                        o.getT2(),
                        o.getT3(),
                        o.getT4(),
                        o.getT5(),
                        o.getT6(),
                        o.getT7()));

        Flux<TelemetryData> firstZip = Flux.zip(firstGroup, secondGroup, (pedalsAndSpeed, fuelAndAngles) -> {
            TelemetryData telemetryData = new TelemetryData();
            telemetryData.setThrottle(pedalsAndSpeed.getThrottle());
            telemetryData.setBrake(pedalsAndSpeed.getBrake());
            telemetryData.setClutch(pedalsAndSpeed.getClutch());
            telemetryData.setGear(pedalsAndSpeed.getGear());
            telemetryData.setShiftGrindRPM(pedalsAndSpeed.getShiftGrindRPM());
            telemetryData.setRPM(pedalsAndSpeed.getRPM());
            telemetryData.setSpeed(pedalsAndSpeed.getSpeed());

            telemetryData.setFuelLevel(fuelAndAngles.getFuelLevel());
            telemetryData.setFuelLevelPct(fuelAndAngles.getFuelLevelPct());
            telemetryData.setFuelUsePerHour(fuelAndAngles.getFuelUsePerHour());
            telemetryData.setLatAccel(fuelAndAngles.getLatAccel());
            telemetryData.setLongAccel(fuelAndAngles.getLongAccel());
            telemetryData.setSteeringWheelAngle(fuelAndAngles.getSteeringWheelAngle());
            return telemetryData;
        });
        Flux<TelemetryData> secondZip = Flux.zip(thirdGroup, fourthGroup, (weather, session) -> {
            TelemetryData telemetryData = new TelemetryData();
            telemetryData.setAirPressure(weather.getAirPressure());
            telemetryData.setAirTemp(weather.getAirTemp());
            telemetryData.setRelativeHumidity(weather.getRelativeHumidity());
            telemetryData.setSkies(weather.getSkies());
            telemetryData.setTrackTemp(weather.getTrackTemp());
            telemetryData.setWindDir(weather.getWindDir());
            telemetryData.setWindVel(weather.getWindVel());
            telemetryData.setWeatherType(weather.getWeatherType());

            telemetryData.setSessionTime(session.getSessionTime());
            telemetryData.setSessionTimeRemain(session.getSessionTimeRemain());
            telemetryData.setLapBestLapTime(session.getLapBestLapTime());
            telemetryData.setLap(session.getLap());
            telemetryData.setLapCurrentLapTime(session.getLapCurrentLapTime());
            telemetryData.setLapBestLap(session.getLapBestLap());
            telemetryData.setLapDistPct(session.getLapDistPct());

            return telemetryData;
        });

        return Flux.zip(firstZip, secondZip, (a, b) -> {
            TelemetryData telemetryData = new TelemetryData();
            telemetryData.setThrottle(a.getThrottle());
            telemetryData.setBrake(a.getBrake());
            telemetryData.setClutch(a.getClutch());
            telemetryData.setGear(a.getGear());
            telemetryData.setShiftGrindRPM(a.getShiftGrindRPM());
            telemetryData.setRPM(a.getRPM());
            telemetryData.setSpeed(a.getSpeed());

            telemetryData.setFuelLevel(a.getFuelLevel());
            telemetryData.setFuelLevelPct(a.getFuelLevelPct());
            telemetryData.setFuelUsePerHour(a.getFuelUsePerHour());
            telemetryData.setLatAccel(a.getLatAccel());
            telemetryData.setLongAccel(a.getLongAccel());
            telemetryData.setSteeringWheelAngle(a.getSteeringWheelAngle());

            telemetryData.setAirPressure(b.getAirPressure());
            telemetryData.setAirTemp(b.getAirTemp());
            telemetryData.setRelativeHumidity(b.getRelativeHumidity());
            telemetryData.setSkies(b.getSkies());
            telemetryData.setTrackTemp(b.getTrackTemp());
            telemetryData.setWindDir(b.getWindDir());
            telemetryData.setWindVel(b.getWindVel());
            telemetryData.setWeatherType(b.getWeatherType());

            telemetryData.setSessionTime(b.getSessionTime());
            telemetryData.setSessionTimeRemain(b.getSessionTimeRemain());
            telemetryData.setLapBestLapTime(b.getLapBestLapTime());
            telemetryData.setLap(b.getLap());
            telemetryData.setLapCurrentLapTime(b.getLapCurrentLapTime());
            telemetryData.setLapBestLap(b.getLapBestLap());
            telemetryData.setLapDistPct(b.getLapDistPct());

            return telemetryData;
        });

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
