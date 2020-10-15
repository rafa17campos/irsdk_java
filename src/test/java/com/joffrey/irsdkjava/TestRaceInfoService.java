package com.joffrey.irsdkjava;

import static com.joffrey.irsdkjava.YamlHelperTest.createByteBufferYamlFile;
import static com.joffrey.irsdkjava.YamlHelperTest.loadYamlObject;
import static org.assertj.core.api.Assertions.assertThat;

import com.joffrey.irsdkjava.laptiming.LapTimingService;
import com.joffrey.irsdkjava.model.Header;
import com.joffrey.irsdkjava.model.SdkStarter;
import com.joffrey.irsdkjava.raceinfo.RaceInfoService;
import com.joffrey.irsdkjava.yaml.YamlService;
import com.joffrey.irsdkjava.yaml.irsdkyaml.YamlFile;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class TestRaceInfoService {

    @MockBean
    private SdkStarter  sdkStarter;
    @MockBean
    private Header      header;
    @MockBean
    private YamlService yamlService;

    // Class under test
    private RaceInfoService raceInfoService;

    // Helpers Objects
    private ByteBuffer byteBufferYamlFile;

    @BeforeEach
    void init() {
        raceInfoService = new RaceInfoService(sdkStarter, yamlService);
        byteBufferYamlFile = createByteBufferYamlFile("raceinfo/raceinfo.yml");
        YamlFile yamlFile = loadYamlObject(byteBufferYamlFile);

        Mockito.when(sdkStarter.getHeader()).thenReturn(header);
        Mockito.when(sdkStarter.getHeader().getSessionInfoByteBuffer()).thenReturn(byteBufferYamlFile);
        Mockito.when(sdkStarter.isRunning()).thenReturn(true);
        Mockito.when(yamlService.getYamlFile()).thenReturn(yamlFile);

        Mockito.when(sdkStarter.getVarDouble("SessionTimeRemain")).thenReturn(9999d);
        Mockito.when(sdkStarter.getVarInt("SessionLapsRemain")).thenReturn(100);
        Mockito.when(sdkStarter.getVarFloat("FuelLevel")).thenReturn(50.0f);
        Mockito.when(sdkStarter.getVarFloat("FuelLevelPct")).thenReturn(13.7f);
        Mockito.when(sdkStarter.getVarFloat("FuelUsePerHour")).thenReturn(9.9f);
    }


    @DisplayName("loadRaceInfo() - Assert that values are good transformed into Flux object")
    @Test
    void Given_ValuesFromYamlAndMemMapFile_WhenCallingGetRaceInfoFlux_Then_SHouldReturnFluxWithValidValues() {

        StepVerifier.create(raceInfoService.getRaceInfoFlux())
        .assertNext(raceInfo -> {
            assertThat(raceInfo.getTrackDisplayName()).isEqualTo("Okayama International Circuit");
            assertThat(raceInfo.getTrackConfigName()).isEqualTo("Short");
            assertThat(raceInfo.getTrackCity()).isEqualTo("Okayama");
            assertThat(raceInfo.getTrackCountry()).isEqualTo("Japan");
            assertThat(raceInfo.getTrackLength()).isEqualTo("1.93 km");
            assertThat(raceInfo.getAverageSOF()).isEqualTo(219);
            assertThat(raceInfo.getEventType()).isEqualTo("Practice");
            assertThat(raceInfo.getFuelLevel()).isEqualTo(50.0f);
            assertThat(raceInfo.getFuelLevelPct()).isEqualTo(13.7f);
            assertThat(raceInfo.getFuelUsePerHour()).isEqualTo(9.9f);
            assertThat(raceInfo.getPlayerCarIdx()).isEqualTo(63);
            assertThat(raceInfo.getRemainingLaps()).isEqualTo(100);
            assertThat(raceInfo.getRemainingTime()).isEqualTo(9999.0d);
        })
        .thenCancel()
        .verifyThenAssertThat()
        .hasNotDroppedElements()
        .hasNotDiscardedElements()
        .hasNotDroppedErrors();
    }


}