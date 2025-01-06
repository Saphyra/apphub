package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public enum RingType {
    ROCKY("eRingClass_Rocky"),
    METAL_RICH("eRingClass_MetalRich"),
    METALLIC("eRingClass_Metalic"),
    ICY("eRingClass_Icy"),
    ;
    private final String value;

    //TODO unit test
    public static RingType parse(String in) {
        if (isNull(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + RingType.class.getSimpleName()));
    }
}
