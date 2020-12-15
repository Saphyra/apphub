package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Human {
    private final UUID humanId;
    private Integer morale;
    private Integer satiety;
    private final Skill skill;
}
