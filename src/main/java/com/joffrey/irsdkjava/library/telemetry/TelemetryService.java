package com.joffrey.irsdkjava.library.telemetry;

import com.joffrey.irsdkjava.SdkStarter;
import com.joffrey.irsdkjava.library.telemetry.model.TelemetryData;
import com.joffrey.irsdkjava.library.telemetry.model.TelemetryData.FuelAndAngles;
import com.joffrey.irsdkjava.library.telemetry.model.TelemetryData.PedalsAndSpeed;
import com.joffrey.irsdkjava.library.telemetry.model.TelemetryData.Session;
import com.joffrey.irsdkjava.library.telemetry.model.TelemetryData.Weather;
import java.time.Duration;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@Service
public class TelemetryService {

    private final SdkStarter                     sdkStarter;
    public final  ConnectableFlux<TelemetryData> telemetryDataFlux;

    public TelemetryService(SdkStarter sdkStarter) {
        this.sdkStarter = sdkStarter;
        this.telemetryDataFlux = Flux.interval(Duration.ofMillis(100)).filter(aLong -> sdkStarter.isRunning())
                                     .flatMap(aLong -> loadTelemetryData()).publish();

    }

    public Flux<TelemetryData> getTelemetryDataFlux() {
        return telemetryDataFlux.autoConnect();
    }

    private Flux<TelemetryData> loadTelemetryData() {

        Flux<TelemetryData.PedalsAndSpeed> firstGroup = Flux.zip(Mono.just(sdkStarter.getVarFloat("Throttle")),
                                                                 Mono.just(sdkStarter.getVarFloat("Brake")),
                                                                 Mono.just(sdkStarter.getVarFloat("Clutch")),
                                                                 Mono.just(sdkStarter.getVarInt("Gear")),
                                                                 Mono.just(sdkStarter.getVarFloat("ShiftGrindRPM")),
                                                                 Mono.just(sdkStarter.getVarFloat("RPM")),
                                                                 Mono.just(sdkStarter.getVarFloat("Speed")))
                                                            .map(o -> new PedalsAndSpeed(o.getT1(),
                                                                                         o.getT2(),
                                                                                         o.getT3(),
                                                                                         o.getT4(),
                                                                                         o.getT5(),
                                                                                         o.getT6(),
                                                                                         o.getT7()));

        Flux<TelemetryData.FuelAndAngles> secondGroup = Flux.zip(Mono.just(sdkStarter.getVarFloat("FuelLevel")),
                                                                 Mono.just(sdkStarter.getVarFloat("FuelLevelPct")),
                                                                 Mono.just(sdkStarter.getVarFloat("FuelUsePerHour")),
                                                                 Mono.just(sdkStarter.getVarFloat("LatAccel")),
                                                                 Mono.just(sdkStarter.getVarFloat("LongAccel")),
                                                                 Mono.just(sdkStarter.getVarFloat("SteeringWheelAngle")))
                                                            .map(o -> new FuelAndAngles(o.getT1(),
                                                                                        o.getT2(),
                                                                                        o.getT3(),
                                                                                        o.getT4(),
                                                                                        o.getT5(),
                                                                                        o.getT6()));

        Flux<TelemetryData.Weather> thirdGroup = Flux.zip(Mono.just(sdkStarter.getVarFloat("airPressure")),
                                                          Mono.just(sdkStarter.getVarFloat("airTemp")),
                                                          Mono.just(sdkStarter.getVarFloat("relativeHumidity")),
                                                          Mono.just(sdkStarter.getVarInt("Skies")),
                                                          Mono.just(sdkStarter.getVarFloat("trackTemp")),
                                                          Mono.just(sdkStarter.getVarFloat("windDir")),
                                                          Mono.just(sdkStarter.getVarFloat("windVel")),
                                                          Mono.just(sdkStarter.getVarInt("WeatherType")))
                                                     .map(o -> new Weather(o.getT1(), o.getT2(), o.getT3(), switch (o.getT4()) {
                                                         case 0 -> "Clear";
                                                         case 1, 2 -> "Cloudy";
                                                         case 3 -> "Overcast";
                                                         default -> "";
                                                     }, o.getT5(), o.getT6(), o.getT7(), switch (o.getT8()) {
                                                         case 0 -> "Constant";
                                                         case 1 -> "Dynamic";
                                                         default -> "";
                                                     }));

        Flux<TelemetryData.Session> fourthGroup = Flux.zip(Mono.just(sdkStarter.getVarDouble("SessionTime")),
                                                           Mono.just(sdkStarter.getVarDouble("SessionTimeRemain")),
                                                           Mono.just(sdkStarter.getVarFloat("LapBestLapTime")),
                                                           Mono.just(sdkStarter.getVarInt("Lap")),
                                                           Mono.just(sdkStarter.getVarFloat("LapCurrentLapTime")),
                                                           Mono.just(sdkStarter.getVarInt("LapBestLap")),
                                                           Mono.just(sdkStarter.getVarFloat("LapDistPct")))
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


}
