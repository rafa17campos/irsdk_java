package com.joffrey.iracing.irsdkjava.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Data
@RequiredArgsConstructor
public class IRacingData {

    @NonNull
    private Header header;

    @NonNull
    private ByteBuffer data;
}
