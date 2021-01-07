package com.joffrey.iracing.irsdkjava.model;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.ByteBuffer;

@Slf4j
@RequiredArgsConstructor
public class FileReader {

    public Flux<ByteBuffer> loadData(DiskHeader header) throws IOException {
        Sinks.Many<ByteBuffer> manySink = Sinks.many().replay().all();
        int sessionRecordCount = header.getSessionRecordCount();
        Flux.range(0, sessionRecordCount)
                .doOnNext(idx -> {
                    var byteBuffer = header.getVarByteBuffer(idx);
                    var emitResult = manySink.tryEmitNext(byteBuffer);
                    if (emitResult.isFailure()) {
                        log.warn("Error emitting byte buffer for index " + idx);
                    }
                    if (idx == sessionRecordCount - 1) {
                        manySink.tryEmitComplete();
                    }
                }).subscribe();
        return manySink.asFlux();
    }

}
