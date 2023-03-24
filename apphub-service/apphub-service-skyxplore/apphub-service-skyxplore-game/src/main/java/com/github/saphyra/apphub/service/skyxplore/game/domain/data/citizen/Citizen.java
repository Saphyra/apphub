package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
//TODO unit test
public class Citizen {
    private final UUID citizenId;
    private UUID location;
    private String name;
    private int morale;
    private int satiety;

    public void reduceMorale(int morale) {
        this.morale -= morale;
    }
}
