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

package com.joffrey.iracing.irsdkjava.tires;

import com.joffrey.iracing.irsdkjava.model.IRacingData;
import com.joffrey.iracing.irsdkjava.model.VarReader;
import com.joffrey.iracing.irsdkjava.tires.model.Tires;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@Service
public class TiresService {

    private final VarReader varReader;

    private final Flux<Tires> tiresFlux;

    public TiresService(Flux<IRacingData> dataFlux, VarReader varReader) {
        this.varReader = varReader;
        tiresFlux = dataFlux.flatMap(this::loadTires);
        tiresFlux.subscribe();
    }

    public Flux<Tires> getTiresFlux() {
        return tiresFlux;
    }

    private Mono<Tires> loadTires(IRacingData iRacingData) {
        return Mono.just(Tires.builder())
                .map(tiresBuilder -> tiresBuilder.lfTire(buildTireData("LF", iRacingData)))
                .map(tiresBuilder -> tiresBuilder.rfTire(buildTireData("RF", iRacingData)))
                .map(tiresBuilder -> tiresBuilder.lrTire(buildTireData("LR", iRacingData)))
                .map(tiresBuilder -> tiresBuilder.rrTire(buildTireData("RR", iRacingData)))
                .map(Tires.TiresBuilder::build);
    }

    private Tires.Tire buildTireData(String position, IRacingData iRacingData) {
        return Tires.Tire.builder()
                .wearL(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "wearL"))
                .wearM(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "wearM"))
                .wearR(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "wearR"))
                .tempL(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "tempL"))
                .tempM(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "tempM"))
                .tempR(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "tempR"))
                .tempCL(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "tempCL"))
                .tempCM(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "tempCM"))
                .tempCR(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "tempCR"))
                .pressure(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "pressure"))
                .speed(varReader.getVarFloat(iRacingData.getData(), iRacingData.getHeader().fetchVars(), position + "speed"))
                .build();

    }
}
