package com.github.saphyra.apphub.service.elite_base.dao.star_system_data;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
public enum Power {
    ARCHON_DELAINE("Archon Delaine"),
    ARISSA_LAVIGNY_DUVAL("A. Lavigny-Duval"),
    AISLING_DUVAL("Aisling Duval"),
    DENTON_PATREUS("Denton Patreus"),
    EDMUND_MAHON("Edmund Mahon"),
    YURI_GROM("Yuri Grom"),
    FELICIA_WINTERS("Felicia Winters"),
    LOQ_YONG_RUI("Li Yong-Rui"),
    JEROME_ARCHER("Jerome Archer"),
    NAKATO_KAINE("Nakato Kaine"),
    PRANAV_ANTAL("Pranav Antal"),
    ZEMINA_TORVAL("Zemina Torval"),
    ;
    private final String value;

    //TODO unit test
    public static Power parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + Power.class.getSimpleName()));
    }
}
