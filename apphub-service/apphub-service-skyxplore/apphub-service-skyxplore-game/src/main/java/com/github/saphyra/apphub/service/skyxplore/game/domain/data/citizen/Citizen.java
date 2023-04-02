package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Citizen {
    @NonNull
    private final UUID citizenId;

    @NonNull
    private UUID location;

    @NonNull
    private String name;


    private Integer morale;
    private Integer satiety;

    public void reduceMorale(int morale) {
        this.morale -= morale;
    }
}
