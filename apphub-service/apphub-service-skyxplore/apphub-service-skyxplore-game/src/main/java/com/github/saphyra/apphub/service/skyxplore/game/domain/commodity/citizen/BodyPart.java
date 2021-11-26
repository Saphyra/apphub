package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BodyPart {
    HEAD("CITIZEN_ARMOR_HEAD"),
    LEFT_HAND("CITIZEN_ARMOR_LEFT_HAND"),
    RIGHT_HAND("CITIZEN_ARMOR_RIGHT_HAND"),
    CHEST("CITIZEN_ARMOR_CHEST"),
    LEFT_LEG("CITIZEN_ARMOR_LEFT_LEG"),
    RIGHT_LEG("CITIZEN_ARMOR_RIGHT_LEG");

    private final String metadata;
}
