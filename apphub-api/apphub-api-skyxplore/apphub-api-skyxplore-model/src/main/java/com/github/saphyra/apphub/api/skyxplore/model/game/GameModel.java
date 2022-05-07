package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GameModel extends GameItem {
    private UUID host;
    private String name;
    private LocalDateTime lastPlayed;
    private Boolean markedForDeletion;
    private LocalDateTime markedForDeletionAt;
}
